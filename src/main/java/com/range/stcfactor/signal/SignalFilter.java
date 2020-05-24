package com.range.stcfactor.signal;

import com.range.stcfactor.common.Constant;
import com.range.stcfactor.common.utils.ArrayUtils;
import com.range.stcfactor.common.utils.FormatUtils;
import com.range.stcfactor.expression.constant.ExpMode;
import com.range.stcfactor.expression.tree.ExpTree;
import com.range.stcfactor.signal.data.DataScreen;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.indexing.NDArrayIndex;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.function.BiFunction;

/**
 * @author zrj5865@163.com
 * @create 2019-12-01
 */
public class SignalFilter {

    private static final Logger logger = LogManager.getLogger(SignalFilter.class);

    private List<DataScreen> factorHistory;

    private int groupSetting;
    private int topSetting;

    private double totalEffectiveRateThreshold;
    private double dayEffectiveRateThreshold;
    private double totalMeanMinThreshold;
    private double totalMeanMaxThreshold;
    private double totalStdThreshold;
    private double totalKurtosisThreshold;
    private double totalIcThreshold;
    private double groupIcThreshold;
    private double mutualIcThreshold;
    private double dayTurnoverRateThreshold;

    private boolean isInterrupt = false;

    public SignalFilter(List<DataScreen> factorHistory, Properties config) {
        this.factorHistory = factorHistory;

        this.groupSetting = Integer.valueOf(config.getProperty(Constant.FILTER_GROUP_SETTING, Constant.DEFAULT_FILTER_GROUP_SETTING));
        this.topSetting = Integer.valueOf(config.getProperty(Constant.FILTER_TOP_SETTING, Constant.DEFAULT_FILTER_TOP_SETTING));

        this.totalEffectiveRateThreshold = Double.valueOf(config.getProperty(Constant.THRESHOLD_TOTAL_EFFECTIVE_RATE, Constant.DEFAULT_THRESHOLD_TOTAL_EFFECTIVE_RATE));
        this.dayEffectiveRateThreshold = Double.valueOf(config.getProperty(Constant.THRESHOLD_DAY_EFFECTIVE_RATE, Constant.DEFAULT_THRESHOLD_DAY_EFFECTIVE_RATE));
        this.totalMeanMinThreshold = FormatUtils.parseStringToDouble(config.getProperty(Constant.THRESHOLD_TOTAL_MEAN_MIN, Constant.DEFAULT_THRESHOLD_TOTAL_MEAN_MIN));
        this.totalMeanMaxThreshold = FormatUtils.parseStringToDouble(config.getProperty(Constant.THRESHOLD_TOTAL_MEAN_MAX, Constant.DEFAULT_THRESHOLD_TOTAL_MEAN_MAX));
        this.totalStdThreshold = FormatUtils.parseStringToDouble(config.getProperty(Constant.THRESHOLD_TOTAL_STD, Constant.DEFAULT_THRESHOLD_TOTAL_STD));
        this.totalKurtosisThreshold = Double.valueOf(config.getProperty(Constant.THRESHOLD_TOTAL_KURTOSIS, Constant.DEFAULT_THRESHOLD_TOTAL_KURTOSIS));
        this.totalIcThreshold = Double.valueOf(config.getProperty(Constant.THRESHOLD_TOTAL_INFORMATION_COEFFICIENT, Constant.DEFAULT_THRESHOLD_TOTAL_INFORMATION_COEFFICIENT));
        this.groupIcThreshold = Double.valueOf(config.getProperty(Constant.THRESHOLD_GROUP_INFORMATION_COEFFICIENT, Constant.DEFAULT_THRESHOLD_GROUP_INFORMATION_COEFFICIENT));
        this.mutualIcThreshold = Double.valueOf(config.getProperty(Constant.THRESHOLD_MUTUAL_INFORMATION_COEFFICIENT, Constant.DEFAULT_THRESHOLD_MUTUAL_INFORMATION_COEFFICIENT));
        this.dayTurnoverRateThreshold = Double.valueOf(config.getProperty(Constant.THRESHOLD_DAY_TURNOVER_RATE, Constant.DEFAULT_THRESHOLD_DAY_TURNOVER_RATE));

        if (ExpMode.valueOf(config.getProperty(Constant.EXP_MODE, Constant.DEFAULT_EXP_MODE)) == ExpMode.auto) {
            isInterrupt = true;
        }
    }

    public DataScreen screen(ExpTree exp, INDArray factor, INDArray income) {
        DataScreen screen = new DataScreen(exp.toString(), factor, income);

        if (!calTotalEr(screen)                                 // 总共的有效率
                || !calDayEr(screen)                            // 每天有效率的均值
                || !calTotalMean(screen)                        // 每天所有因子的均值
                || !calTotalStd(screen)                         // 每天所有因子的标准差
                || !calTotalKurtosis(screen)                    // 每天所有因子的峰度
                || !calTotalIC(screen)                          // 总共的IC
//                || !calGroupIC(screen)                          // 每天分组IC的均值
                || !calDayTr(screen)                            // 每天换手率的均值
                || !calMutualIC(screen, this.factorHistory)) {  // 与已有expression的IC
            return screen;
        }

        return screen;
    }

    private boolean calTotalEr(DataScreen screen) {
        INDArray factor = screen.getEffectFactor();
        INDArray income = screen.getEffectIncome();
        screen.setTotalEffectiveRate(effectiveRate(factor, income));

        if (ArrayUtils.isInvalid(screen.getTotalEffectiveRate()) || screen.getTotalEffectiveRate() < this.totalEffectiveRateThreshold) {
            screen.setUseful(false);
            screen.setUselessReason("Total effective rate is " + screen.getTotalEffectiveRate()
                    + " < " + this.totalEffectiveRateThreshold + " (threshold)");
            return !isInterrupt;
        }
        return true;
    }

    private boolean calDayEr(DataScreen screen) {
        INDArray factor = screen.getEffectFactor();
        INDArray income = screen.getEffectIncome();
        List<Double> ers = new ArrayList<>();
        for (int i=0; i<income.rows(); i++) {
            INDArray rowF = factor.getRow(i);
            INDArray rowC = income.getRow(i);
            double er = effectiveRate(rowF, rowC);
            if (ArrayUtils.isValid(er) && er > 0) {
                ers.add(er);
            }
        }
        double min = Double.NaN;
        if (CollectionUtils.isNotEmpty(ers)) {
            min = (double) Nd4j.create(ers).minNumber();
        }
        screen.setDayEffectiveRate(min);

        if (ArrayUtils.isInvalid(screen.getDayEffectiveRate()) || screen.getDayEffectiveRate() < this.dayEffectiveRateThreshold) {
            screen.setUseful(false);
            screen.setUselessReason("Day effective rate is " + screen.getDayEffectiveRate()
                    + " < " + this.dayEffectiveRateThreshold + " (threshold)");
            return !isInterrupt;
        }
        return true;
    }

    private boolean calTotalMean(DataScreen screen) {
        INDArray factor = screen.getEffectFactor();
        screen.setTotalMean(ArrayUtils.meanWithoutNan(factor));

        if (ArrayUtils.isInvalid(screen.getTotalMean()) ||
                screen.getTotalMean() < this.totalMeanMinThreshold ||
                screen.getTotalMean() > this.totalMeanMaxThreshold) {
            screen.setUseful(false);
            screen.setUselessReason("Total mean is " + screen.getTotalMean() + ", is not in ("
                    + this.totalMeanMinThreshold + " - " + this.totalMeanMaxThreshold + ") (threshold)");
            return !isInterrupt;
        }
        return true;
    }

    private boolean calTotalStd(DataScreen screen) {
        INDArray factor = screen.getEffectFactor();
        screen.setTotalStd(ArrayUtils.stdWithoutNan(factor));

        if (ArrayUtils.isInvalid(screen.getTotalStd()) || screen.getTotalStd() < this.totalStdThreshold) {
            screen.setUseful(false);
            screen.setUselessReason("Total std is " + screen.getTotalStd() + " < " +
                    this.totalStdThreshold + " (threshold)");
            return !isInterrupt;
        }
        return true;
    }

    private boolean calTotalKurtosis(DataScreen screen) {
        INDArray factor = screen.getEffectFactor();
        screen.setTotalKurtosis(ArrayUtils.kurtosisWithoutNan(factor));

        if (ArrayUtils.isInvalid(screen.getTotalKurtosis()) ||
                screen.getTotalKurtosis() > this.totalKurtosisThreshold) {
            screen.setUseful(false);
            screen.setUselessReason("Total kurtosis is " + screen.getTotalKurtosis()
                    + " > " + this.totalKurtosisThreshold + " (threshold)");
            return !isInterrupt;
        }
        return true;
    }

    private boolean calTotalIC(DataScreen screen) {
        INDArray factor = screen.getEffectFactor();
        INDArray income = screen.getEffectIncome();
        screen.setTotalIC(calculateIC(factor, income, ArrayUtils::corr));

        if (ArrayUtils.isInvalid(screen.getTotalIC()) || Math.abs(screen.getTotalIC()) < this.totalIcThreshold) {
            screen.setUseful(false);
            screen.setUselessReason("Total information coefficient is " + screen.getTotalIC()
                    + " < " + this.totalIcThreshold + " (threshold)");
            return !isInterrupt;
        }
        return true;
    }

    private boolean calGroupIC(DataScreen screen) {
        INDArray factor = screen.getEffectFactor();
        INDArray income = screen.getEffectIncome();
        screen.setGroupIC(calculateIC(factor, income, (effectiveRowF, effectiveRowC) -> {
            // 排序, 分组
            Integer[] sorts = ArrayUtils.argSort(effectiveRowF, false);
            int[] groupPers = new int[groupSetting];
            for (int j=0; j<groupPers.length; j++) {
                if (j < sorts.length % groupSetting) {
                    groupPers[j] = sorts.length / groupSetting + 1;
                } else {
                    groupPers[j] = sorts.length / groupSetting;
                }
            }

            // 求每组IC
            List<Double> groupIcs = new ArrayList<>();
            int lower = 0;
            for (int j=0; j<groupSetting; j++) {
                int upper = lower + groupPers[j];
                List<Double> groupRowF = new ArrayList<>();
                List<Double> groupRowC = new ArrayList<>();
                for (int k=lower; k<upper; k++) {
                    groupRowF.add(effectiveRowF.getDouble(sorts[k]));
                    groupRowC.add(effectiveRowC.getDouble(sorts[k]));
                }
                double ic = ArrayUtils.corr(Nd4j.create(groupRowF), Nd4j.create(groupRowC));
                if (ArrayUtils.isValid(ic)) {
                    groupIcs.add(ic);
                }
                lower = upper;
            }
            double ic = Double.NaN;
            if (CollectionUtils.isNotEmpty(groupIcs)) {
                ic = (double) Nd4j.create(groupIcs).meanNumber();
            }
            return ic;
        }));

        if (ArrayUtils.isInvalid(screen.getGroupIC()) || Math.abs(screen.getGroupIC()) < this.groupIcThreshold) {
            screen.setUseful(false);
            screen.setUselessReason("Group information coefficient is " + screen.getGroupIC()
                    + " < " + this.groupIcThreshold + " (threshold)");
            return !isInterrupt;
        }
        return true;
    }

    private boolean calDayTr(DataScreen screen) {
        INDArray factor = screen.getEffectFactor();
        List<Double> turnoverRates = new ArrayList<>();
        Set<Integer> lastTops = null;
        for (int i=0; i<factor.rows(); i++) {
            INDArray row = factor.getRow(i);
            if (ArrayUtils.isAllNan(row)) {
                continue;
            }

            Integer[] sorts = ArrayUtils.argSort(row, false);
            Set<Integer> currentTops = new HashSet<>(Arrays.asList(Arrays.copyOfRange(sorts, 0, topSetting)));
            if (lastTops == null) {
                lastTops = currentTops;
                continue;
            }
            Set<Integer> diff = new HashSet<>(currentTops);
            diff.removeAll(lastTops);
            turnoverRates.add(diff.size() / (double) currentTops.size());
        }
        double tr = Double.NaN;
        if (CollectionUtils.isNotEmpty(turnoverRates)) {
            tr = (double) Nd4j.create(turnoverRates).meanNumber();
        }
        screen.setDayTurnoverRate(tr);

        if (ArrayUtils.isInvalid(screen.getDayTurnoverRate()) || screen.getDayTurnoverRate() > this.dayTurnoverRateThreshold) {
            screen.setUseful(false);
            screen.setUselessReason("Day turnover rate is " + screen.getDayTurnoverRate()
                    + " > " + this.dayTurnoverRateThreshold + " (threshold)");
            return !isInterrupt;
        }
        return true;
    }

    public boolean calMutualIC(DataScreen screen, List<DataScreen> histories) {
        for (DataScreen comparer : histories) {
            int cut = Math.max(ArrayUtils.getNanCut(screen.getSourceFactor()), ArrayUtils.getNanCut(comparer.getSourceFactor()));
            INDArray effectFactor = screen.getSourceFactor().get(NDArrayIndex.interval(cut, screen.getSourceFactor().rows()), NDArrayIndex.all());
            INDArray effectComparer = comparer.getSourceFactor().get(NDArrayIndex.interval(cut, comparer.getSourceFactor().rows()), NDArrayIndex.all());
            screen.setMutualIC(calculateIC(effectFactor, effectComparer, ArrayUtils::corr));

            if (ArrayUtils.isValid(screen.getMutualIC()) && Math.abs(screen.getMutualIC()) > this.mutualIcThreshold) {
                screen.setUseful(false);
                screen.setUselessReason("Mutual information coefficient is " + screen.getMutualIC()
                        + " > " + this.mutualIcThreshold + " (threshold) with: [" + comparer.getExpression() + "]");
                if (isInterrupt) {
                    return false;
                }
            }
        }
        return true;
    }

    private double effectiveRate(INDArray factor, INDArray comparer) {
        double effect = 0.0;
        double invalid = 0.0;
        for (int i=0; i<comparer.rows(); i++) {
            INDArray rowF = factor.getRow(i);
            INDArray rowC = comparer.getRow(i);
            for (int j = 0; j<rowC.columns(); j++) {
                if (isInvalid(rowC.getDouble(j))) {
                    invalid++;
                    continue;
                }
                if (ArrayUtils.isValid(rowF.getDouble(j))) {
                    effect++;
                }
            }
        }
        return effect / (factor.rows() * factor.columns() - invalid);
    }

    private double calculateIC(INDArray factor, INDArray comparer, BiFunction<INDArray, INDArray, Double> transform) {
        List<Double> ics = new ArrayList<>();
        int columns = factor.columns();
        int factorRows = factor.rows();
        int comparerRows = comparer.rows();
        int rowGap = Math.abs(factorRows - comparerRows);
        int rowSize = Math.min(factorRows, comparerRows);

        double[][] factorArray = factor.toDoubleMatrix();
        double[][] comparerArray = comparer.toDoubleMatrix();
        for (int row=0; row < rowSize; row++) {
            int factorRow;
            int comparerRow;
            if (factorRows >= comparerRows) {
                factorRow = row + rowGap;
                comparerRow = row;
            } else {
                factorRow = row;
                comparerRow = row + rowGap;
            }

            List<Double> effectiveRowF = new ArrayList<>();
            List<Double> effectiveRowC = new ArrayList<>();
            for (int column=0; column<columns; column++) {
                double valueF = factorArray[factorRow][column];
                double valueC = comparerArray[comparerRow][column];
                // 过滤有效值
                if (isInvalid(valueC) || ArrayUtils.isInvalid(valueF)) {
                    continue;
                }
                effectiveRowF.add(valueF);
                effectiveRowC.add(valueC);
            }
            if (CollectionUtils.isEmpty(effectiveRowF) || CollectionUtils.isEmpty(effectiveRowC)) {
                continue;
            }
            double corr = transform.apply(Nd4j.create(effectiveRowF), Nd4j.create(effectiveRowC));
            if (ArrayUtils.isValid(corr)) {
                ics.add(corr);
            }
        }

        double mean = Double.NaN;
        if (CollectionUtils.isNotEmpty(ics)) {
            mean = (double) Nd4j.create(ics).meanNumber();
        }
        return mean;
    }

    private boolean isInvalid(double num) {
        return ArrayUtils.isInvalid(num) || num >= 0.098 || num <= -0.098;
    }

}
