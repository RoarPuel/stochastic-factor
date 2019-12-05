package com.range.stcfactor.signal.data;

import org.nd4j.linalg.api.ndarray.INDArray;

/**
 * @author zrj5865@163.com
 * @create 2019-12-01
 */
public class DataScreen {

    private String expression;
    private INDArray factor;
    private boolean useful;
    private String uselessReason;

    private double totalEffectiveRate;
    private double dayEffectiveRate;
    private double totalIC;
    private double groupIC;
    private double mutualIC;
    private double dayTurnoverRate;

    public DataScreen(String expression, INDArray factor) {
        this.expression = expression;
        this.factor = factor;
        this.useful = true;
        this.uselessReason = "useful";
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    public INDArray getFactor() {
        return factor;
    }

    public void setFactor(INDArray factor) {
        this.factor = factor;
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
                + ", totalIC:" + totalIC
                + ", groupIC:" + groupIC
                + ", mutualIC:" + mutualIC
                + ", totalEffectiveRate:" + totalEffectiveRate
                + ", dayEffectiveRate:" + dayEffectiveRate
                + ", dayTurnoverRate:" + dayTurnoverRate;
    }

}
