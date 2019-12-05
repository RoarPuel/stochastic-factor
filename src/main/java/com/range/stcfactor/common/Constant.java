package com.range.stcfactor.common;

import org.nd4j.linalg.api.ndarray.INDArray;

/**
 * 配置
 *
 * @author zrj5865@163.com
 * @create 2019-08-20
 */
public class Constant {

    public static final String DEFAULT_CONFIG_PATH = "D:\\Work\\Project\\Java\\stochastic-factor\\data\\config.properties";
    public static final Class DEFAULT_TYPE = INDArray.class;

    public static final String THREAD_PARALLEL = "thread.parallel";
    public static final String DEFAULT_THREAD_PARALLEL = "20";

    public static final String EXP_TOTAL = "exp.total";
    public static final String DEFAULT_EXP_TOTAL = "1";
    public static final String EXP_DEPTH_MIN = "exp.depth.min";
    public static final String DEFAULT_EXP_DEPTH_MIN = "3";
    public static final String EXP_DEPTH_MAX = "exp.depth.max";
    public static final String DEFAULT_EXP_DEPTH_MAX = "5";

    public static final String DATA_FILE_PATH = "data.file.path";
    public static final String DEFAULT_DATA_FILE_PATH = "D:\\Work\\Project\\Java\\stochastic-factor\\data\\{0}.csv";
    public static final String FACTOR_FILE_PATH = "factor.file.path";
    public static final String DEFAULT_FACTOR_FILE_PATH = "D:\\Work\\Project\\Java\\stochastic-factor\\factor\\{0}.csv";

    public static final String FILTER_GROUP_SETTING = "filter.group.setting";
    public static final String DEFAULT_FILTER_GROUP_SETTING = "5";
    public static final String FILTER_TOP_SETTING = "filter.top.setting";
    public static final String DEFAULT_FILTER_TOP_SETTING = "100";

    /**
     * 所有因子有效率
     */
    public static final String THRESHOLD_TOTAL_EFFECTIVE_RATE = "threshold.total.effective.rate";
    public static final String DEFAULT_THRESHOLD_TOTAL_EFFECTIVE_RATE = "0.9";
    /**
     * 每天因子有效率
     */
    public static final String THRESHOLD_DAY_EFFECTIVE_RATE = "threshold.day.effective.rate";
    public static final String DEFAULT_THRESHOLD_DAY_EFFECTIVE_RATE = "0.5";
    /**
     * 所有因子的相关性阈值
     */
    public static final String THRESHOLD_TOTAL_INFORMATION_COEFFICIENT = "threshold.total.information.coefficient";
    public static final String DEFAULT_THRESHOLD_TOTAL_INFORMATION_COEFFICIENT = "0.02";
    /**
     * 分组因子的相关性阈值
     */
    public static final String THRESHOLD_GROUP_INFORMATION_COEFFICIENT = "threshold.group.information.coefficient";
    public static final String DEFAULT_THRESHOLD_GROUP_INFORMATION_COEFFICIENT = "0.1";
    /**
     * 因子互相的相关性阈值
     */
    public static final String THRESHOLD_MUTUAL_INFORMATION_COEFFICIENT = "threshold.mutual.information.coefficient";
    public static final String DEFAULT_THRESHOLD_MUTUAL_INFORMATION_COEFFICIENT = "0.7";
    /**
     * 每天因子换手率
     */
    public static final String THRESHOLD_DAY_TURNOVER_RATE = "threshold.day.turnover.rate";
    public static final String DEFAULT_THRESHOLD_DAY_TURNOVER_RATE = "0.6";

}
