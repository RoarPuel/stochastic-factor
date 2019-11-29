package com.range.stcfactor.signal;

import com.range.stcfactor.common.Constant;
import com.range.stcfactor.common.utils.FileUtils;
import com.range.stcfactor.expression.ExpVariables;
import com.range.stcfactor.expression.tree.ExpTree;
import com.range.stcfactor.signal.data.DataBean;
import com.range.stcfactor.signal.data.DataFactory;
import com.range.stcfactor.signal.data.DataModel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.nd4j.linalg.api.ndarray.INDArray;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * 信号生成
 *
 * @author renjie.zhu@woqutech.com
 * @create 2019-08-06
 */
public class SignalGenerator {

    private static final Logger logger = LogManager.getLogger(SignalGenerator.class);

    private int dayNum;
    private String dataFilePath;
    private double icThreshold;
    private double acThreshold;

    private ExecutorService threadPool;
    private DataFactory factory;

    public SignalGenerator(Properties config) {
        this.dayNum = Integer.valueOf(config.getProperty(Constant.DATA_DATE_NUM, "1"));
        this.dataFilePath = config.getProperty(Constant.DATA_FILE_PATH);
        this.icThreshold = Double.valueOf(config.getProperty(Constant.THRESHOLD_INFORMATION_COEFFICIENT, "0.02"));
        this.acThreshold = Double.valueOf(config.getProperty(Constant.THRESHOLD_AUTO_CORRELATION, "0.8"));

        this.threadPool = Executors.newFixedThreadPool(20);
        this.factory = new DataFactory(initData());
    }

    public void startTask(ExpTree exp) {
        startTasks(Collections.singleton(exp));
    }

    public void startTasks(Set<ExpTree> exps) {
        List<Future<ExpTree>> results = new ArrayList<>();
        for (ExpTree expTree : exps) {
            Future<ExpTree> future = threadPool.submit(new SignalTask(expTree, factory));
            results.add(future);
        }
        this.threadPool.shutdown();

        for (Future<ExpTree> result : results) {
            try {
                ExpTree exp = result.get();
                logger.info(">>>>> Result: ac: {}, ic: {}, expresion: [{}].", exp.getAcThreshold(), exp.getIcThreshold(), exp);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void close() {
        this.threadPool.shutdown();
    }

    private DataModel initData() {
        DataModel dataModel = new DataModel();
        for (ExpVariables var : ExpVariables.values()) {
            Object data;
            if (ExpVariables.day_num == var) {
                data = this.dayNum;
            } else {
                data = readData(var, this.dataFilePath).getData();
            }
            dataModel.putData(var, data);
        }
        return dataModel;
    }

    private DataBean readData(ExpVariables type, String path) {
        String filepath = MessageFormat.format(path, type.name());
        logger.info(">>>>> Start load [{}] data from [{}].", type.name(), filepath);
        List<String> headers = null;
        List<Date> indexes = null;
        INDArray data = FileUtils.readCsv(filepath, headers, indexes);

        DataBean bean = new DataBean();
        bean.setType(type);
        bean.setHeader(headers);
        bean.setIndex(indexes);
        bean.setData(data);
        logger.info(">>>>> Finish load [{}] data from [{}].", type.name(), filepath);
        return bean;
    }

}
