package com.range.stcfactor;

import com.range.stcfactor.common.Constant;
import com.range.stcfactor.expression.ExpGenerator;
import com.range.stcfactor.expression.tree.ExpTree;
import com.range.stcfactor.signal.SignalGenerator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
        config.put(Constant.EXP_TOTAL, "1");
        config.put(Constant.EXP_DEPTH_MIN, "3");
        config.put(Constant.EXP_DEPTH_MAX, "5");

        config.put(Constant.DATA_DATE_NUM, "2");
        config.put(Constant.DATA_FILE_PATH, "D:\\Work\\Project\\Java\\stochastic-factor\\data\\{0}.csv");
        return config;
    }

}
