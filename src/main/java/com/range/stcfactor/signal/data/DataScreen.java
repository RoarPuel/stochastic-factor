package com.range.stcfactor.signal.data;

import com.range.stcfactor.common.utils.ArrayUtils;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.indexing.NDArrayIndex;

/**
 * @author zrj5865@163.com
 * @create 2019-12-01
 */
public class DataScreen {

    private String expression;
    private INDArray sourceFactor;
    private INDArray effectFactor;
    private INDArray sourceIncome;
    private INDArray effectIncome;
    private boolean useful;
    private String uselessReason;

    private double totalEffectiveRate;
    private double dayEffectiveRate;
    private double totalMean;
    private double totalStd;
    private double totalKurtosis;
    private double totalIC;
    private double groupIC;
    private double mutualIC;
    private double dayTurnoverRate;

    public DataScreen(String expression, INDArray sourceFactor) {
        this.expression = expression;
        this.sourceFactor = sourceFactor;
        this.effectFactor = sourceFactor.get(NDArrayIndex.interval(ArrayUtils.getNanCut(sourceFactor), sourceFactor.rows()), NDArrayIndex.all());
        this.useful = true;
        this.uselessReason = "useful";
    }

    public DataScreen(String expression, INDArray sourceFactor, INDArray sourceIncome) {
        int cut = ArrayUtils.getNanCut(sourceFactor);
        this.expression = expression;
        this.sourceFactor = sourceFactor;
        this.effectFactor = sourceFactor.get(NDArrayIndex.interval(cut, sourceFactor.rows()), NDArrayIndex.all());
        this.sourceIncome = sourceIncome;
        this.effectIncome = sourceIncome.get(NDArrayIndex.interval(cut, sourceIncome.rows()), NDArrayIndex.all());
        this.useful = true;
        this.uselessReason = "useful";
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    public INDArray getSourceFactor() {
        return sourceFactor;
    }

    public void setSourceFactor(INDArray sourceFactor) {
        this.sourceFactor = sourceFactor;
    }

    public INDArray getEffectFactor() {
        return effectFactor;
    }

    public void setEffectFactor(INDArray effectFactor) {
        this.effectFactor = effectFactor;
    }

    public INDArray getSourceIncome() {
        return sourceIncome;
    }

    public void setSourceIncome(INDArray sourceIncome) {
        this.sourceIncome = sourceIncome;
    }

    public INDArray getEffectIncome() {
        return effectIncome;
    }

    public void setEffectIncome(INDArray effectIncome) {
        this.effectIncome = effectIncome;
    }

    public boolean isUseful() {
        return useful;
    }

    public void setUseful(boolean useful) {
        this.useful = useful;
    }

    public String getUselessReason() {
        return uselessReason;
    }

    public void setUselessReason(String uselessReason) {
        this.uselessReason = uselessReason;
    }

    public double getTotalEffectiveRate() {
        return totalEffectiveRate;
    }

    public void setTotalEffectiveRate(double totalEffectiveRate) {
        this.totalEffectiveRate = totalEffectiveRate;
    }

    public double getDayEffectiveRate() {
        return dayEffectiveRate;
    }

    public void setDayEffectiveRate(double dayEffectiveRate) {
        this.dayEffectiveRate = dayEffectiveRate;
    }

    public double getTotalMean() {
        return totalMean;
    }

    public void setTotalMean(double totalMean) {
        this.totalMean = totalMean;
    }

    public double getTotalStd() {
        return totalStd;
    }

    public void setTotalStd(double totalStd) {
        this.totalStd = totalStd;
    }

    public double getTotalKurtosis() {
        return totalKurtosis;
    }

    public void setTotalKurtosis(double totalKurtosis) {
        this.totalKurtosis = totalKurtosis;
    }

    public double getTotalIC() {
        return totalIC;
    }

    public void setTotalIC(double totalIC) {
        this.totalIC = totalIC;
    }

    public double getGroupIC() {
        return groupIC;
    }

    public void setGroupIC(double groupIC) {
        this.groupIC = groupIC;
    }

    public double getMutualIC() {
        return mutualIC;
    }

    public void setMutualIC(double mutualIC) {
        this.mutualIC = mutualIC;
    }

    public double getDayTurnoverRate() {
        return dayTurnoverRate;
    }

    public void setDayTurnoverRate(double dayTurnoverRate) {
        this.dayTurnoverRate = dayTurnoverRate;
    }

    @Override
    public String toString() {
        return "Expression:" + expression
                + ", isUseful:" + useful
                + ", totalEffectiveRate:" + totalEffectiveRate
                + ", dayEffectiveRate:" + dayEffectiveRate
                + ", totalMean:" + totalMean
                + ", totalStd:" + totalStd
                + ", totalKurtosis:" + totalKurtosis
                + ", totalIC:" + totalIC
                + ", groupIC:" + groupIC
                + ", mutualIC:" + mutualIC
                + ", dayTurnoverRate:" + dayTurnoverRate
                + ".";
    }

}
