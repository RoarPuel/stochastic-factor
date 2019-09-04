package com.range.stcfactor.signal;

import com.range.stcfactor.Constant;
import com.range.stcfactor.expression.ExpVariables;
import com.range.stcfactor.expression.tree.ExpTree;
import com.range.stcfactor.signal.data.DataFactory;
import com.range.stcfactor.signal.data.DataModel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.nd4j.linalg.factory.Nd4j;

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
        try {
            int dateNum = Integer.valueOf(config.getProperty(Constant.DATA_DATE_NUM, "5"));
            dataModel.setData(ExpVariables.day_num, dateNum);

            // TODO
            dataModel.setData(ExpVariables.open, Nd4j.create(new double[][]{{1.0,2.0,3.0},{4.0,5.0,6.0},{7.0,8.0,9.0}}));
            dataModel.setData(ExpVariables.high, Nd4j.create(new double[][]{{2.0,3.0,4.0},{5.0,6.0,7.0},{8.0,9.0,10.0}}));
            dataModel.setData(ExpVariables.low, Nd4j.create(new double[][]{{3.0,4.0,5.0},{6.0,7.0,8.0},{9.0,10.0,11.0}}));
            dataModel.setData(ExpVariables.close, Nd4j.create(new double[][]{{4.0,5.0,6.0},{7.0,8.0,9.0},{10.0,11.0,12.0}}));
        } catch (Exception e) {
            logger.error("Init data error: {}", e.getMessage());
        }
        return dataModel;
    }

    public void startTask(ExpTree expTree) {
        SignalTask signalTask = new SignalTask(expTree, factory);
        threadPool.submit(signalTask);
    }

}
