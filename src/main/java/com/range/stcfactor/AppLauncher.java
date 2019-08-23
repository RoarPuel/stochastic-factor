package com.range.stcfactor;

import com.range.stcfactor.expression.ExpGenerator;
import com.range.stcfactor.expression.tree.ExpTree;
import com.range.stcfactor.signal.SignalGenerator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tech.tablesaw.api.ColumnType;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.Column;
import tech.tablesaw.columns.strings.StringColumnType;
import tech.tablesaw.io.csv.CsvReadOptions;

import java.io.IOException;
import java.util.Properties;
import java.util.Set;

/**
 * 总入口
 *
 * @author zrj5865@163.com
 * @create 2019-07-22
 */
public class AppLauncher {

    private static final Logger logger = LogManager.getLogger(AppLauncher.class);

    public static void main(String[] args) {
        Properties config = initConfig();

        logger.info("=== Start generate expressions.");
        ExpGenerator expGenerator = new ExpGenerator(config);
        Set<ExpTree> exps = expGenerator.generateRandomExpression();
        logger.info("=== Finish generate expressions. Actual count:{}.", exps.size());

        logger.info("=== Start generate signal.");
        SignalGenerator signalGenerator = new SignalGenerator(config);
        for (ExpTree exp : exps) {
            signalGenerator.startTask(exp);
        }
        logger.info("=== Finish push signal task.");
    }

    private static Properties initConfig() {
        Properties config = new Properties();
        config.put(ConfigConstant.EXP_TOTAL, "10");
        config.put(ConfigConstant.EXP_DEPTH_MIN, "2");
        config.put(ConfigConstant.EXP_DEPTH_MAX, "3");

        config.put(ConfigConstant.DATA_DATE_START, "2019-01-01");
        config.put(ConfigConstant.DATA_DATE_END, "2019-12-31");

        config.put(ConfigConstant.DATA_PATH_OPEN, "D:\\Work\\Project\\Java\\stochastic-factor\\data\\open.csv");
        config.put(ConfigConstant.DATA_PATH_HIGH, "D:\\Work\\Project\\Java\\stochastic-factor\\data\\high.csv");
        config.put(ConfigConstant.DATA_PATH_LOW, "D:\\Work\\Project\\Java\\stochastic-factor\\data\\low.csv");
        config.put(ConfigConstant.DATA_PATH_CLOSE, "D:\\Work\\Project\\Java\\stochastic-factor\\data\\close.csv");
        return config;
    }

}
