package com.range.stcfactor.signal;

import com.opencsv.CSVReader;
import com.range.stcfactor.Constant;
import com.range.stcfactor.expression.ExpVariables;
import com.range.stcfactor.expression.tree.ExpTree;
import com.range.stcfactor.signal.data.DataBean;
import com.range.stcfactor.signal.data.DataFactory;
import com.range.stcfactor.signal.data.DataModel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.nd4j.linalg.api.buffer.DataType;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import java.io.FileReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 信号生成
 *
 * @author renjie.zhu@woqutech.com
 * @create 2019-08-06
 */
public class SignalGenerator {

    private static final Logger logger = LogManager.getLogger(SignalGenerator.class);

    private DateFormat format = new SimpleDateFormat("yyyy-MM-dd");

    private Properties config;
    private ExecutorService threadPool;
    private DataFactory factory;

    public SignalGenerator(Properties config) {
        this.config = config;
        this.threadPool = Executors.newFixedThreadPool(20);

        this.factory = new DataFactory(initData());
    }

    private DataModel initData() {
        DataModel dataModel = new DataModel();
        dataModel.putData(ExpVariables.day_num, Integer.valueOf(config.getProperty(Constant.DATA_DATE_NUM, "5")));
        dataModel.putData(ExpVariables.open, readCsv(ExpVariables.open, config.getProperty(Constant.DATA_PATH_OPEN)).getData());
        dataModel.putData(ExpVariables.high, readCsv(ExpVariables.high, config.getProperty(Constant.DATA_PATH_HIGH)).getData());
        dataModel.putData(ExpVariables.low, readCsv(ExpVariables.low, config.getProperty(Constant.DATA_PATH_LOW)).getData());
        dataModel.putData(ExpVariables.close, readCsv(ExpVariables.close, config.getProperty(Constant.DATA_PATH_CLOSE)).getData());
        return dataModel;
    }

    private DataBean readCsv(ExpVariables type, String filepath) {
        List<String> header = new ArrayList<>();
        List<String[]> lines = new ArrayList<>();
        try {
            CSVReader csv = new CSVReader(new FileReader(filepath));
            String[] head = csv.readNext();
            header = Arrays.asList(Arrays.copyOfRange(head, 1, head.length));
            lines = csv.readAll();
        } catch (Exception e) {
            logger.error("read csv from [{}] error.", filepath, e);
        }

        List<Date> index = new ArrayList<>();
        INDArray data = Nd4j.create(DataType.DOUBLE, lines.size(), header.size());
        for (int i=0; i<lines.size(); i++) {
            String[] line = lines.get(i);
            index.add(parseStrToDate(line[0]));

            List<Double> items = new ArrayList<>();
            for (int j=1; j<line.length; j++) {
                String item = line[j];
                if (!"NaN".equalsIgnoreCase(item)) {
                    items.add(Double.parseDouble(item));
                } else {
                    items.add(Double.NaN);
                }
            }
            data.putRow(i, Nd4j.create(items));
        }

        DataBean bean = new DataBean();
        bean.setType(type);
        bean.setHeader(header);
        bean.setIndex(index);
        bean.setData(data);
        logger.info("load [{}] data from [{}] success.", type, filepath);
        return bean;
    }

    private Date parseStrToDate(String date) {
        try {
            return format.parse(date);
        } catch (Exception e) {
            logger.error("Parse date error.", e);
            return null;
        }
    }

    private String parseDateToStr(Date date) {
        return format.format(date);
    }

    public void startTask(ExpTree expTree) {
        SignalTask signalTask = new SignalTask(expTree, factory);
        threadPool.submit(signalTask);
    }

}
