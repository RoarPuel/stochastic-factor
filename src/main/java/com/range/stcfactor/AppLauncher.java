package com.range.stcfactor;

import com.range.stcfactor.common.Constant;
import com.range.stcfactor.expression.ExpGenerator;
import com.range.stcfactor.expression.tree.ExpTree;
import com.range.stcfactor.signal.SignalGenerator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
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
        String configPath = Constant.DEFAULT_CONFIG_PATH;
        if (args.length > 0) {
            configPath = args[0];
        }
        Properties config = initConfig(configPath);
        logger.info("Load properties from: {}.", configPath);
        logger.info("     {} = {}", Constant.THREAD_PARALLEL, config.getProperty(Constant.THREAD_PARALLEL));
        logger.info("     {} = {}", Constant.TASK_QUEUE_MAX, config.getProperty(Constant.TASK_QUEUE_MAX));
        logger.info("     {} = {}", Constant.EXP_MODE, config.getProperty(Constant.EXP_MODE));
        logger.info("     {} = {}", Constant.EXP_TOTAL, config.getProperty(Constant.EXP_TOTAL));
        logger.info("     {} = {}", Constant.EXP_DEPTH_MIN, config.getProperty(Constant.EXP_DEPTH_MIN));
        logger.info("     {} = {}", Constant.EXP_DEPTH_MAX, config.getProperty(Constant.EXP_DEPTH_MAX));
        logger.info("     {} = {}", Constant.DATA_FILE_PATH, config.getProperty(Constant.DATA_FILE_PATH));
        logger.info("     {} = {}", Constant.FACTOR_FILE_PATH, config.getProperty(Constant.FACTOR_FILE_PATH));
        logger.info("     {} = {}", Constant.FILTER_GROUP_SETTING, config.getProperty(Constant.FILTER_GROUP_SETTING));
        logger.info("     {} = {}", Constant.FILTER_TOP_SETTING, config.getProperty(Constant.FILTER_TOP_SETTING));
        logger.info("     {} = {}", Constant.THRESHOLD_TOTAL_EFFECTIVE_RATE, config.getProperty(Constant.THRESHOLD_TOTAL_EFFECTIVE_RATE));
        logger.info("     {} = {}", Constant.THRESHOLD_DAY_EFFECTIVE_RATE, config.getProperty(Constant.THRESHOLD_DAY_EFFECTIVE_RATE));
        logger.info("     {} = {}", Constant.THRESHOLD_TOTAL_INFORMATION_COEFFICIENT, config.getProperty(Constant.THRESHOLD_TOTAL_INFORMATION_COEFFICIENT));
        logger.info("     {} = {}", Constant.THRESHOLD_GROUP_INFORMATION_COEFFICIENT, config.getProperty(Constant.THRESHOLD_GROUP_INFORMATION_COEFFICIENT));
        logger.info("     {} = {}", Constant.THRESHOLD_MUTUAL_INFORMATION_COEFFICIENT, config.getProperty(Constant.THRESHOLD_MUTUAL_INFORMATION_COEFFICIENT));
        logger.info("     {} = {}", Constant.THRESHOLD_DAY_TURNOVER_RATE, config.getProperty(Constant.THRESHOLD_DAY_TURNOVER_RATE));

        logger.info("=== Start generate expressions.");
        ExpGenerator expGenerator = new ExpGenerator(config);
        Set<ExpTree> exps = expGenerator.obtainExpression();
        logger.info("=== Finish generate expressions. Actual count:{}.", exps.size());

        logger.info("=== Start signal task.");
        SignalGenerator signalGenerator = new SignalGenerator(config);
        signalGenerator.startTasks(exps);
        logger.info("=== Finish signal task.");
    }

    private static Properties initConfig(String configPath) {
        Properties config = new Properties();
        try {
            InputStream inputStream = new FileInputStream(new File(configPath));
            config.load(inputStream);
        } catch (Exception e) {
            logger.error("load properties error: {}", e.getMessage());
        }
        return config;
    }

}
