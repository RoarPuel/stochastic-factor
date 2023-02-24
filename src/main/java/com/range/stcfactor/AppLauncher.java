package com.range.stcfactor;

import com.range.stcfactor.common.Constant;
import com.range.stcfactor.expression.ExpGenerator;
import com.range.stcfactor.expression.tree.ExpTree;
import com.range.stcfactor.signal.SignalGenerator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.nd4j.jita.conf.CudaEnvironment;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
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
        CudaEnvironment.getInstance().getConfiguration().allowMultiGPU(true);

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
        logger.info("     {} = {}", Constant.EXP_SPLIT, config.getProperty(Constant.EXP_SPLIT));
        logger.info("     {} = {}", Constant.EXP_DEPTH_MIN, config.getProperty(Constant.EXP_DEPTH_MIN));
        logger.info("     {} = {}", Constant.EXP_DEPTH_MAX, config.getProperty(Constant.EXP_DEPTH_MAX));
        logger.info("     {} = {}", Constant.EXP_PRINT_FORMAT, config.getProperty(Constant.EXP_PRINT_FORMAT));
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

        ExpGenerator expGenerator = new ExpGenerator(config);
        SignalGenerator signalGenerator = new SignalGenerator(config);

        int index = 0;
        List<Integer> parts = getParts(Integer.parseInt(config.getProperty(Constant.EXP_TOTAL)),
                                        Integer.parseInt(config.getProperty(Constant.EXP_SPLIT)));
        for (int part : parts) {
            logger.info("=================================== Run {} time. ===================================", ++index);

            logger.info("=== Start generate expressions.");
            Set<ExpTree> exps = expGenerator.obtainExpression(part);
            logger.info("=== Finish generate expressions. Actual count:{}.", exps.size());

            logger.info("=== Start signal task.");
            signalGenerator.startTasks(exps);
            logger.info("=== Finish signal task.");
        }
    }

    private static Properties initConfig(String configPath) {
        Properties config = new Properties();
        try {
            InputStream inputStream = new FileInputStream(configPath);
            config.load(inputStream);
        } catch (Exception e) {
            logger.error("load properties error: {}", e.getMessage());
        }
        return config;
    }

    private static List<Integer> getParts(int total, int split) {
        List<Integer> partitions = new ArrayList<>();
        for (int i=0; i<total/split; i++) {
            partitions.add(split);
        }
        if (total % split > 0) {
            partitions.add(total % split);
        }
        return partitions;
    }

}
