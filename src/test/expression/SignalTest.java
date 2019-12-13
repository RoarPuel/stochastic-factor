package expression;

import com.range.stcfactor.common.Constant;
import com.range.stcfactor.expression.ExpResolver;
import com.range.stcfactor.signal.SignalGenerator;

import java.util.Properties;

/**
 * @author zrj5865@163.com
 * @create 2019-12-13
 */
public class SignalTest {

    public static void main(String[] args) {
        SignalGenerator signalGenerator = new SignalGenerator(obtainConfig());
        signalGenerator.startTask(ExpResolver.analysis("wma(close,65)"));
    }

    private static Properties obtainConfig() {
        Properties config = new Properties();
        config.setProperty(Constant.THREAD_PARALLEL, Constant.DEFAULT_THREAD_PARALLEL);
        config.setProperty(Constant.TASK_QUEUE_MAX, Constant.DEFAULT_TASK_QUEUE_MAX);
        config.setProperty(Constant.EXP_MODE, Constant.DEFAULT_EXP_MODE);
        config.setProperty(Constant.EXP_TOTAL, Constant.DEFAULT_EXP_TOTAL);
        config.setProperty(Constant.EXP_DEPTH_MIN, Constant.DEFAULT_EXP_DEPTH_MIN);
        config.setProperty(Constant.EXP_DEPTH_MAX, Constant.DEFAULT_EXP_DEPTH_MAX);
        config.setProperty(Constant.DATA_FILE_PATH, Constant.DEFAULT_DATA_FILE_PATH);
        config.setProperty(Constant.FACTOR_FILE_PATH, Constant.DEFAULT_FACTOR_FILE_PATH);
        config.setProperty(Constant.FILTER_GROUP_SETTING, Constant.DEFAULT_FILTER_GROUP_SETTING);
        config.setProperty(Constant.FILTER_TOP_SETTING, Constant.DEFAULT_FILTER_TOP_SETTING);
        config.setProperty(Constant.THRESHOLD_TOTAL_EFFECTIVE_RATE, Constant.DEFAULT_THRESHOLD_TOTAL_EFFECTIVE_RATE);
        config.setProperty(Constant.THRESHOLD_DAY_EFFECTIVE_RATE, Constant.DEFAULT_THRESHOLD_DAY_EFFECTIVE_RATE);
        config.setProperty(Constant.THRESHOLD_TOTAL_INFORMATION_COEFFICIENT, Constant.DEFAULT_THRESHOLD_TOTAL_INFORMATION_COEFFICIENT);
        config.setProperty(Constant.THRESHOLD_GROUP_INFORMATION_COEFFICIENT, Constant.DEFAULT_THRESHOLD_GROUP_INFORMATION_COEFFICIENT);
        config.setProperty(Constant.THRESHOLD_MUTUAL_INFORMATION_COEFFICIENT, Constant.DEFAULT_THRESHOLD_MUTUAL_INFORMATION_COEFFICIENT);
        config.setProperty(Constant.THRESHOLD_DAY_TURNOVER_RATE, Constant.DEFAULT_THRESHOLD_DAY_TURNOVER_RATE);
        return config;
    }

}
