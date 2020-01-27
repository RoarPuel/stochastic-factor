package com.range.stcfactor.signal;

import com.range.stcfactor.common.Constant;
import com.range.stcfactor.common.utils.FileUtils;
import com.range.stcfactor.expression.ExpMode;
import com.range.stcfactor.expression.ExpVariables;
import com.range.stcfactor.expression.tree.ExpTree;
import com.range.stcfactor.signal.data.DataBean;
import com.range.stcfactor.signal.data.DataFactory;
import com.range.stcfactor.signal.data.DataScreen;
import com.range.stcfactor.signal.data.DataModel;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.nd4j.linalg.api.ndarray.INDArray;

import java.io.File;
import java.text.DecimalFormat;
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
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 信号生成
 *
 * @author zrj5865@163.com
 * @create 2019-08-06
 */
public class SignalGenerator {

    private static final Logger logger = LogManager.getLogger(SignalGenerator.class);

    private static final DecimalFormat DOUBLE_DECIMAL_FORMAT = new DecimalFormat("#0.00");
    private static final DecimalFormat INTEGER_DECIMAL_FORMAT = new DecimalFormat("00000000");
    private static final String FACTOR_SUMMARY = "summary";
    private static final char FACTOR_SUMMARY_SEPARATOR = '\t';
    private static final String[] FACTOR_SUMMARY_HEADER = {
            "No.",
            "expression",
            "judgment_result",
            "total_effective_rate",
            "day_effective_rate",
            "total_IC",
            "group_IC",
            "mutual_IC",
            "day_turnover_rate"
    };

    private int taskQueueMax;
    private String dataFilePath;
    private String factorFilePath;

    private ExecutorService threadPool;

    private DataFactory factory;
    private SignalFilter filter;

    private List<String> headers;
    private List<Date> indexes;

    private int totalTask = 0;
    private boolean isRecord = false;

    public SignalGenerator(Properties config) {
        this.taskQueueMax = Integer.valueOf(config.getProperty(Constant.TASK_QUEUE_MAX, Constant.DEFAULT_TASK_QUEUE_MAX));
        this.dataFilePath = config.getProperty(Constant.DATA_FILE_PATH, Constant.DEFAULT_DATA_FILE_PATH);
        this.factorFilePath = config.getProperty(Constant.FACTOR_FILE_PATH, Constant.DEFAULT_FACTOR_FILE_PATH);

        int threadParallel = Integer.valueOf(config.getProperty(Constant.THREAD_PARALLEL));
        if (threadParallel <= 0) {
            threadParallel = Integer.valueOf(Constant.DEFAULT_THREAD_PARALLEL);
        }
        this.threadPool = Executors.newFixedThreadPool(threadParallel);

        this.factory = new DataFactory(initData());
        this.filter = new SignalFilter(readFactor(), config);

        if (ExpMode.valueOf(config.getProperty(Constant.EXP_MODE, Constant.DEFAULT_EXP_MODE)) == ExpMode.auto) {
            isRecord = true;
        }
    }

    public void startTask(ExpTree exp) {
        startTasks(Collections.singleton(exp), false);
    }

    public void startTasks(Set<ExpTree> exps) {
        startTasks(exps, isRecord);
    }

    private void startTasks(Set<ExpTree> exps, boolean isWrite) {
        totalTask = exps.size();
        monitor();

        List<Future<DataScreen>> results = new ArrayList<>();
        ThreadPoolExecutor pool = (ThreadPoolExecutor) threadPool;
        for (ExpTree expTree : exps) {
            while (pool.getQueue().size() >= taskQueueMax) {
                try {
                    Thread.sleep(10 * 1000);
                } catch (InterruptedException e) {
                    logger.error("Submit thread sleep error.", e);
                }
            }

            Future<DataScreen> future = threadPool.submit(new SignalTask(expTree, factory, filter));
            results.add(future);
        }

        List<DataScreen> screens = new ArrayList<>();
        for (Future<DataScreen> result : results) {
            try {
                screens.add(result.get());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        close();

        if (isWrite) {
            List<DataScreen> usefulScreens = new ArrayList<>();
            List<DataScreen> uselessScreens = new ArrayList<>();
            filterUseful(screens, usefulScreens, uselessScreens);
            writeFactor(usefulScreens, uselessScreens);
        } else {
            screens.forEach(screen -> logger.info("Expression filter result: {}.", screen));
        }
    }

    private void filterUseful(List<DataScreen> screens,
                              List<DataScreen> usefulScreens,
                              List<DataScreen> uselessScreens) {
        for (DataScreen screen : screens) {
            if (!screen.isUseful()) {
                uselessScreens.add(screen);
                continue;
            }

            if (CollectionUtils.isEmpty(usefulScreens)) {
                usefulScreens.add(screen);
                continue;
            }

            if (filter.calMutualIC(screen, usefulScreens)) {
                usefulScreens.add(screen);
            } else {
                uselessScreens.add(screen);
            }
        }
    }

    private void monitor() {
        Thread monitor = new Thread(() -> {
            ThreadPoolExecutor pool = (ThreadPoolExecutor) threadPool;

            while (!pool.isShutdown()) {
                double progress = pool.getCompletedTaskCount() / (double) totalTask * 100;
                if (Double.isNaN(progress)) {
                    progress = 0.0;
                }
                logger.info("******************* Progress: {}% => (Total:{}, Submit:{}, Finish:{}, Active:{}, Queue:{}) *******************",
                        DOUBLE_DECIMAL_FORMAT.format(progress),
                        totalTask,
                        pool.getTaskCount(),
                        pool.getCompletedTaskCount(),
                        pool.getActiveCount(),
                        pool.getQueue().size());

                try {
                    Thread.sleep(10 * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        monitor.start();
    }

    private void close() {
        this.threadPool.shutdown();
    }

    private DataModel initData() {
        DataModel dataModel = new DataModel();
        for (ExpVariables var : ExpVariables.values()) {
            if (ExpVariables.DAY_NUM == var) {
                continue;
            }
            DataBean bean = readData(var);
            dataModel.putData(var, bean.getData());
            if (headers == null || indexes == null) {
                headers = bean.getHeaders();
                indexes = bean.getIndexes();
            }
        }
        return dataModel;
    }

    private DataBean readData(ExpVariables type) {
        String filepath = MessageFormat.format(this.dataFilePath, type.name().toLowerCase());
        logger.info(">>>>> Start load [{}] data from [{}].", type.name(), filepath);
        List<String> headers = new ArrayList<>();
        List<Date> indexes = new ArrayList<>();
        INDArray data = FileUtils.readData(filepath, headers, indexes);

        DataBean bean = new DataBean();
        bean.setType(type);
        bean.setHeaders(headers);
        bean.setIndexes(indexes);
        bean.setData(data);
        logger.info(">>>>> Finish load [{}] data from [{}].", type.name(), filepath);
        return bean;
    }

    private List<DataScreen> readFactor() {
        String summaryFilepath = MessageFormat.format(this.factorFilePath, FACTOR_SUMMARY);
        logger.info(">>>>> Start load [factor history data from [{}].", summaryFilepath);
        List<String[]> indicators = FileUtils.readCsv(summaryFilepath, FACTOR_SUMMARY_SEPARATOR, 1);
        List<DataScreen> screens = new ArrayList<>();
        long start = System.currentTimeMillis();
        int index = 0;
        for (String[] indicator : indicators) {
            INDArray factor = FileUtils.readData(MessageFormat.format(this.factorFilePath, indicator[0]), false);
            DataScreen screen = new DataScreen(indicator[1], factor);
            screen.setTotalEffectiveRate(Double.valueOf(indicator[3]));
            screen.setDayEffectiveRate(Double.valueOf(indicator[4]));
            screen.setTotalIC(Double.valueOf(indicator[5]));
            screen.setGroupIC(Double.valueOf(indicator[6]));
            screen.setMutualIC(Double.valueOf(indicator[7]));
            screen.setDayTurnoverRate(Double.valueOf(indicator[8]));
            screens.add(screen);
            index++;

            if (System.currentTimeMillis() - start > 1000 || index == indicators.size()) {
                logger.info("........... loading ........... {}%",
                        DOUBLE_DECIMAL_FORMAT.format((double) index / indicators.size() * 100));
                start = System.currentTimeMillis();
            }
        }
        logger.info(">>>>> Finish load [factor history] data from [{}].", summaryFilepath);
        return screens;
    }

    private void writeFactor(List<DataScreen> usefulScreens, List<DataScreen> uselessScreens) {
        logger.info(">>>>> Start write factors.");
        // 初始化因子保存文件
        String summaryFilepath = MessageFormat.format(this.factorFilePath, FACTOR_SUMMARY);
        File usefulFile = new File(summaryFilepath);
        if (usefulFile.getParentFile() != null && !usefulFile.getParentFile().exists()) {
            usefulFile.getParentFile().mkdirs();
        }
        if (!usefulFile.exists()) {
            FileUtils.writeCsvNew(summaryFilepath, FACTOR_SUMMARY_SEPARATOR, FACTOR_SUMMARY_HEADER);
        }

        // 保存有效因子信息，获取历史最大No.续写
        List<String[]> factors = FileUtils.readCsv(summaryFilepath, FACTOR_SUMMARY_SEPARATOR, 0);
        String lastNoStr = factors.get(factors.size() - 1)[0];
        int lastNo = 1;
        if (!FACTOR_SUMMARY_HEADER[0].equalsIgnoreCase(lastNoStr)) {
            lastNo += Integer.parseInt(lastNoStr);
        }
        long start = System.currentTimeMillis();
        int index = 0;
        for (DataScreen screen : usefulScreens) {
            // Write summary
            String[] indicator = getIndicator(screen, lastNo++);
            FileUtils.writeCsvAppend(summaryFilepath, FACTOR_SUMMARY_SEPARATOR, indicator);
            // Write factor
            String filepath = MessageFormat.format(this.factorFilePath, indicator[0]);
            List<Date> newIndexes = new ArrayList<>(indexes);
            List<String> newHeaders = new ArrayList<>(headers);
            newHeaders.add(0, "");
            FileUtils.writeData(filepath, newHeaders, newIndexes, screen.getSourceFactor());

            index++;
            if (System.currentTimeMillis() - start > 1000 || index == usefulScreens.size()) {
                logger.info("........... writing ........... {}%",
                        DOUBLE_DECIMAL_FORMAT.format((double) index / usefulScreens.size() * 100));
                start = System.currentTimeMillis();
            }
        }

        // 保存无效因子信息
        String uselessFilepath = MessageFormat.format(this.factorFilePath, FACTOR_SUMMARY + "-useless");
        List<String[]> uselessIndicators = new ArrayList<>();
        uselessIndicators.add(FACTOR_SUMMARY_HEADER);
        lastNo = 1;
        for (DataScreen screen : uselessScreens) {
            uselessIndicators.add(getIndicator(screen, lastNo++));
        }
        FileUtils.writeCsvNew(uselessFilepath, FACTOR_SUMMARY_SEPARATOR, uselessIndicators);

        logger.info(">>>>> Finish write factors.");
    }

    private String[] getIndicator(DataScreen screen, int no) {
        return new String[] {
                INTEGER_DECIMAL_FORMAT.format(no),
                screen.getExpression(),
                screen.getUselessReason(),
                String.valueOf(screen.getTotalEffectiveRate()),
                String.valueOf(screen.getDayEffectiveRate()),
                String.valueOf(screen.getTotalIC()),
                String.valueOf(screen.getGroupIC()),
                String.valueOf(screen.getMutualIC()),
                String.valueOf(screen.getDayTurnoverRate())
        };
    }

}
