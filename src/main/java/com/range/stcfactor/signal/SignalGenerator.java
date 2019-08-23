package com.range.stcfactor.signal;

import com.range.stcfactor.ConfigConstant;
import com.range.stcfactor.expression.tree.ExpTree;
import com.range.stcfactor.signal.data.DataFactory;
import com.range.stcfactor.signal.data.DataModel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tech.tablesaw.api.Table;
import tech.tablesaw.io.csv.CsvReadOptions;

import java.io.IOException;
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
        threadPool = Executors.newFixedThreadPool(20);
        factory = new DataFactory(initData());
    }

    private DataModel initData() {
        DataModel dataModel = new DataModel();
        try {
            dataModel.setOpenTable(Table.read().csv(
                    CsvReadOptions.builder(config.getProperty(ConfigConstant.DATA_PATH_OPEN))));
            dataModel.setHighTable(Table.read().csv(
                    CsvReadOptions.builder(config.getProperty(ConfigConstant.DATA_PATH_HIGH))));
            dataModel.setLowTable(Table.read().csv(
                    CsvReadOptions.builder(config.getProperty(ConfigConstant.DATA_PATH_LOW))));
            dataModel.setCloseTable(Table.read().csv(
                    CsvReadOptions.builder(config.getProperty(ConfigConstant.DATA_PATH_CLOSE))));
        } catch (IOException e) {
            logger.error("Init read data error: {}", e.getMessage());
        }
        return dataModel;
    }

    public void startTask(ExpTree expTree) {
        String startDate = config.getProperty(ConfigConstant.DATA_DATE_START);
        String endDate = config.getProperty(ConfigConstant.DATA_DATE_END);
        SignalTask signalTask = new SignalTask(expTree, factory, startDate, endDate);
        threadPool.submit(signalTask);
    }

}
