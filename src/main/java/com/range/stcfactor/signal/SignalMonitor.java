package com.range.stcfactor.signal;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.DecimalFormat;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author zrj5865@163.com
 * @create 2020-04-08
 */
public class SignalMonitor implements Runnable {

    private static final Logger logger = LogManager.getLogger(SignalMonitor.class);

    private static final DecimalFormat DOUBLE_DECIMAL_FORMAT = new DecimalFormat("#0.00");

    private int totalTask;
    private ThreadPoolExecutor threadPool;

    private boolean flag = true;

    public SignalMonitor(int totalTask, ThreadPoolExecutor threadPool) {
        this.totalTask = totalTask;
        this.threadPool = threadPool;
    }

    @Override
    public void run() {
        while (flag && !threadPool.isShutdown()) {
            double progress = threadPool.getCompletedTaskCount() / (double) totalTask * 100;
            if (Double.isNaN(progress)) {
                progress = 0.0;
            }
            logger.info("******************* Progress: {}% => (Total:{}, Submit:{}, Finish:{}, Active:{}, Queue:{}) *******************",
                    DOUBLE_DECIMAL_FORMAT.format(progress),
                    totalTask,
                    threadPool.getTaskCount(),
                    threadPool.getCompletedTaskCount(),
                    threadPool.getActiveCount(),
                    threadPool.getQueue().size());

            try {
                Thread.sleep(10 * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        logger.debug(">>> Monitor finish.");
    }

    public void close() {
        flag = false;
    }

}
