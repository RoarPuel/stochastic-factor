package com.range.stcfactor.expression;

import com.range.stcfactor.common.utils.ArrayUtils;
import com.range.stcfactor.expression.constant.ExpVariables;
import com.range.stcfactor.signal.data.DataModel;
import org.nd4j.linalg.api.buffer.DataType;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.ops.transforms.Transforms;

import java.util.List;

/**
 * 表达式
 *
 * @author zrj5865@163.com
 * @create 2019-07-22
 */
public class ExpFunctions {

    private INDArray open;
    private INDArray high;
    private INDArray low;
    private INDArray close;
    private INDArray vol;
    private INDArray share;
    private INDArray turnover;
    private INDArray ret;

    public ExpFunctions(DataModel model) {
        this.open = (INDArray) model.getData(ExpVariables.OPEN);
        this.high = (INDArray) model.getData(ExpVariables.HIGH);
        this.low = (INDArray) model.getData(ExpVariables.LOW);
        this.close = (INDArray) model.getData(ExpVariables.CLOSE);
        this.vol = (INDArray) model.getData(ExpVariables.VOL);
        this.share = (INDArray) model.getData(ExpVariables.SHARE);
        this.turnover = (INDArray) model.getData(ExpVariables.TURNOVER);
        this.ret = diff(close, delay(close, 1));
    }

    // =========================== 1 array =========================== //

    public INDArray abs(INDArray arr) {
        return Transforms.abs(arr);
    }

    public INDArray relu(INDArray arr) {
        return Transforms.relu(arr);
    }

    public INDArray sigmoid(INDArray arr) {
        return Transforms.sigmoid(arr);
    }

    public INDArray log(INDArray arr) {
        return Transforms.log(Transforms.abs(arr.add(1.0)));
    }

    public INDArray sqrt(INDArray arr) {
        return Transforms.sqrt(Transforms.abs(arr));
    }

    public INDArray square(INDArray arr) {
        return Transforms.pow(arr, 2);
    }

    public INDArray sign(INDArray arr) {
        return Transforms.sign(ArrayUtils.replaceNan(arr, 0.0));
    }

    public INDArray exp(INDArray arr) {
        INDArray array = ArrayUtils.replaceNan(arr, 0.0);
        INDArray arrNorm = arr.sub(array.mean(0)).div(array.std(0).add(1.0));
        return Transforms.exp(arrNorm);
    }

    public INDArray rank(INDArray arr) {
        return ArrayUtils.rank(arr, false, "first", false);
    }

    public INDArray rankPct(INDArray arr) {
        return ArrayUtils.rank(arr, false, "first", true);
    }

    // =========================== 1 array =========================== //

    // =========================== 2 array =========================== //

    public INDArray add(INDArray arr1, INDArray arr2) {
        return arr1.add(arr2);
    }

    public INDArray sub(INDArray arr1, INDArray arr2) {
        return arr1.sub(arr2);
    }

    public INDArray mul(INDArray arr1, INDArray arr2) {
        return arr1.mul(arr2);
    }

    public INDArray div(INDArray arr1, INDArray arr2) {
        return arr1.div(arr2);
    }

    public INDArray diff(INDArray arr1, INDArray arr2) {
        return arr1.sub(arr2).div(arr2);
    }

    public INDArray min(INDArray arr1, INDArray arr2) {
        INDArray arrDelta = arr1.sub(arr2);
        return arr1.sub(ArrayUtils.replaceLess(arrDelta, 0.0,0.0));
    }

    public INDArray max(INDArray arr1, INDArray arr2) {
        INDArray arrDelta = arr1.sub(arr2);
        return arr1.sub(ArrayUtils.replaceGreater(arrDelta, 0.0, 0.0));
    }

    public INDArray signedPower(INDArray arr1, INDArray arr2) {
        // x0 ^ x1
        return Transforms.pow(arr1, arr2);
    }

    public INDArray fPass(INDArray arr1, INDArray arr2) {
        // finite(x0) ? x1 : nan
        return ArrayUtils.compare(arr1, nums -> Double.isFinite(nums[0]), arr2, Double.NaN);
    }

    public INDArray kelly(INDArray arr1, INDArray arr2) {
        // (p * b + p - 1) / b
        return arr1.mul(arr2).add(arr1).sub(1).div(arr2);
    }

    // =========================== 2 array =========================== //

    // =========================== n array =========================== //

    public INDArray avg(INDArray... arrs) {
        // Expr(ExprOp::DIV, res, Expr(divisor))
        // Expr res = e.Arg(0)
        // for (int i = 1; i < e.NArgs(); ++i)
        //     res = Expr(ExprOp::ADD, res, e.Arg(i))
        // int divisor = e.NArgs()
        long[] shape = {arrs[0].rows(), arrs[0].columns()};
        INDArray res = Nd4j.valueArrayOf(shape, 0.0, DataType.DOUBLE);
        for (INDArray arr : arrs) {
            res.addi(arr);
        }
        int divisor = arrs.length;
        return res.div(divisor);
    }

    public INDArray gavg(INDArray... arrs) {
        // Expr(ExprOp::POW, res, power)
        // Expr res = e.Arg(0)
        // for (int i = 1; i < e.NArgs(); ++i)
        //     res = Expr(ExprOp::MULTI, res, e.Arg(i))
        // Expr power = Expr(ExprOp::DIV, Expr(1), Expr(e.NArgs()))
        long[] shape = {arrs[0].rows(), arrs[0].columns()};
        INDArray res = Nd4j.valueArrayOf(shape, 1.0, DataType.DOUBLE);
        for (INDArray arr : arrs) {
            res.muli(arr);
        }
        double power = 1.0 / arrs.length;
        return Transforms.pow(res, power);
    }

    // =========================== n array =========================== //

    // =========================== 1 array + 1 num =========================== //

    public INDArray std(INDArray arr, Integer dayNum) {
        return ArrayUtils.rollingRow(arr, dayNum, array -> array.std(0));
    }

    public INDArray mean(INDArray arr, Integer dayNum) {
        return ArrayUtils.rollingRow(arr, dayNum, array -> array.mean(0));
    }

    public INDArray prod(INDArray arr, Integer dayNum) {
        return ArrayUtils.rollingRow(arr, dayNum, array -> array.prod(0));
    }

    public INDArray tsSum(INDArray arr, Integer dayNum) {
        return ArrayUtils.rollingRow(arr, dayNum, array -> array.sum(0));
    }

    public INDArray tsMin(INDArray arr, Integer dayNum) {
        return ArrayUtils.rolling(arr, dayNum, array -> {
            double min = Double.NaN;
            for (double current : array) {
                if (Double.isNaN(min) || current < min) {
                    min = current;
                }
            }
            return min;
        });
    }

    public INDArray tsMax(INDArray arr, Integer dayNum) {
        return ArrayUtils.rolling(arr, dayNum, array -> {
            double max = Double.NaN;
            for (double current : array) {
                if (Double.isNaN(max) || current > max) {
                    max = current;
                }
            }
            return max;
        });
    }

    public INDArray tsRank(INDArray arr, Integer dayNum) {
        return ArrayUtils.rolling(arr, dayNum, array -> {
            double n = (double) array.length;
            Integer[] sort = ArrayUtils.argSort(array);
            List<Double> range = ArrayUtils.range(0, n, 1.0, num -> (num + 1.0) / n);
            return range.get(sort[sort.length-1]);
        });
    }

    public INDArray delay(INDArray arr, Integer dayNum) {
        return ArrayUtils.shift(arr, dayNum);
    }

    public INDArray delta(INDArray arr, Integer dayNum) {
        return arr.sub(delay(arr, dayNum));
    }

    public INDArray decayLinear(INDArray arr, Integer dayNum) {
        return ArrayUtils.rolling(arr, dayNum, array -> {
            final double[] sum = {0.0};
            List<Double> decayWeights = ArrayUtils.rangeClosed(1.0, (double) array.length, 1.0, num -> {
                sum[0] += num;
                return num;
            });
            double total = 0;
            for (int i=0; i<array.length; i++) {
                total += array[i] * decayWeights.get(i) / sum[0];
            }
            return total;
        });
    }

    public INDArray count(INDArray arr, Integer dayNum) {
        INDArray arrCon = Transforms.sign(ArrayUtils.replaceNan(arr, 0.0));
        return tsSum(ArrayUtils.replaceEquals(arrCon, -1.0, 0.0), dayNum);
    }

    public INDArray highDay(INDArray arr, Integer dayNum) {
        return ArrayUtils.rolling(arr, dayNum, array -> {
            double max = Double.NaN;
            int index = 0;
            for (int i=0; i<array.length; i++) {
                double current = array[i];
                if (Double.isNaN(max) || current > max) {
                    max = current;
                    index = i;
                }
            }
            return (double) array.length - 1 - index;
        });
    }

    public INDArray lowDay(INDArray arr, Integer dayNum) {
        return ArrayUtils.rolling(arr, dayNum, array -> {
            double min = Double.NaN;
            int index = 0;
            for (int i=0; i<array.length; i++) {
                double current = array[i];
                if (Double.isNaN(min) || current < min) {
                    min = current;
                    index = i;
                }
            }
            return (double) array.length - 1 - index;
        });
    }

    public INDArray ret(INDArray arr, Integer dayNum) {
        return arr.div(delay(arr, dayNum)).sub(1.0);
    }

    public INDArray retL(INDArray arr, Integer dayNum) {
        return Transforms.log(arr.div(delay(arr, dayNum)));
    }

    public INDArray roc(INDArray arr, Integer dayNum) {
        return ret(arr, dayNum).mul(100);
    }

    public INDArray ma(INDArray arr, Integer dayNum) {
        return ArrayUtils.rolling(arr, dayNum, array -> {
            int n = 0;
            double sum = 0.0;
            for (double current : array) {
                if (Double.isNaN(sum)) {
                    continue;
                }
                n++;
                sum += current;
            }
            n = n == 0 ? 1 : n;
            return sum / n;
        });
    }

    public INDArray wma(INDArray arr, Integer dayNum) {
        return ArrayUtils.rolling(arr, dayNum, array -> {
            final int[] index = {array.length - 1};
            final double[] sum = {0.0};
            ArrayUtils.rangeClosed(1.0, (double) array.length, 1.0, num -> {
                double mul = Math.pow(0.9, num) * array[index[0]];
                index[0]--;
                sum[0] += mul;
                return mul;
            });
            return sum[0];
        });
    }

    public INDArray sumSign(INDArray arr, Integer dayNum) {
        INDArray arrCon = Transforms.sign(ArrayUtils.replaceNan(arr, 0.0));
        return tsSum(arr.mul(arrCon), dayNum);
    }

    public INDArray sum(INDArray arr, Integer dayNum) {
        // ma(x, p) * p
        return ma(arr, dayNum).mul(dayNum);
    }

    public INDArray bias(INDArray arr, Integer dayNum) {
        // diff(x, ma(x, p))
        return diff(arr, ma(arr, dayNum));
    }

    public INDArray cv(INDArray arr, Integer dayNum) {
        // std(x, p) / ma(x, p)
        return std(arr, dayNum).div(ma(arr, dayNum));
    }

    public INDArray cv2(INDArray arr, Integer dayNum) {
        // ma(x, p) / std(x, p)
        return ma(arr, dayNum).div(std(arr, dayNum));
    }

    public INDArray mcs(INDArray arr, Integer dayNum) {
        // (1 - scale(x, p)) * scale(x, p)
        INDArray scale = scale(arr, dayNum);
        return scale.sub(1).mul(-1).mul(scale);
    }

    public INDArray rs(INDArray arr, Integer dayNum) {
        // x / ma(x, p)
        return arr.div(ma(arr, dayNum));
    }

    public INDArray rsi(INDArray arr, Integer dayNum) {
        // ema(relu(delta(x, 1)), p) / ema(relu(-delta(x, 1)), p)
        INDArray delta = delta(arr, 1);
        INDArray ema1 = ema(relu(delta), dayNum);
        INDArray ema = ema(relu(delta).mul(-1), dayNum);
        return ema1.div(ema);
    }

    public INDArray tsRng(INDArray arr, Integer dayNum) {
        // tsMax(x, p) - tsMin(x, p)
        return tsMax(arr, dayNum).sub(tsMin(arr, dayNum));
    }

    public INDArray scale(INDArray arr, Integer dayNum) {
        // (x - tsMin(x, p)) / tsRng(x, p)
        return arr.sub(tsMin(arr, dayNum)).div(tsRng(arr, dayNum));
    }

    public INDArray ema(INDArray arr, Integer dayNum) {
        return ArrayUtils.rolling(arr, dayNum, nums -> ArrayUtils.ema(nums, nums.length));
    }

    public INDArray zScore(INDArray arr, Integer dayNum) {
        // (x - ma(x, p)) / std(x, p)
        return arr.sub(ma(arr, dayNum)).div(std(arr, dayNum));
    }

    public INDArray meanS(INDArray arr, Integer dayNum) {
        // TODO mean_s
        return mean(arr, dayNum);
    }

    public INDArray divS(INDArray arr, Integer dayNum) {
        return arr.div(meanS(arr, dayNum));
    }

    public INDArray subS(INDArray arr, Integer dayNum) {
        return arr.sub(meanS(arr, dayNum));
    }

    public INDArray meanC(INDArray arr, Integer dayNum) {
        // TODO mean_c
        return mean(arr, dayNum);
    }

    public INDArray stdC(INDArray arr, Integer dayNum) {
        // TODO std_c
        return std(arr, dayNum);
    }

    public INDArray zScoreC(INDArray arr, Integer dayNum) {
        return arr.sub(meanC(arr, dayNum)).div(stdC(arr, dayNum));
    }

    // =========================== 1 array + 1 num =========================== //

    // =========================== 1 array + 2 num =========================== //

    public INDArray sma(INDArray arr, Integer dayNum1, Integer dayNum2) {
        return ma(arr, dayNum1>=dayNum2 ? dayNum1/dayNum2 : dayNum2/dayNum1);
    }

    public INDArray upper(INDArray arr, Integer dayNum1, Integer number) {
        // ma(x, p) + N * std(x, p)
        return ma(arr, dayNum1).add(std(arr, dayNum1).mul(number));
    }

    public INDArray lower(INDArray arr, Integer dayNum1, Integer number) {
        // ma(x, p) - N * std(x, p)
        return ma(arr, dayNum1).sub(std(arr, dayNum1).mul(number));
    }

    public INDArray odds(INDArray arr, Integer dayNum1, Integer dayNum2) {
        // sum(relu(delta(x, p0)), p1) * count(delta(x, p0) < 0, p1) / sum(relu(-delta(x, p0)), p1) / count(delta(x, p0) > 0, p1)
        INDArray delta = delta(arr, dayNum1);
        INDArray sum1 = sum(relu(delta), dayNum2);
        INDArray sum2 = sum(relu(delta.mul(-1)), dayNum2);
        INDArray count1 = count(ArrayUtils.condition(delta, nums -> nums[0] < 0), dayNum1).div(dayNum1);
        INDArray count2 = count(ArrayUtils.condition(delta, nums -> nums[0] > 0), dayNum1).div(dayNum1);
        return sum1.mul(count1).div(sum2).div(count2);
    }

    public INDArray sumRatio(INDArray arr, Integer dayNum1, Double number) {
        // sum(x, p0) / sum(x, p1)
        // p0 = std::max(round(e.Arg(1).IntValue() * e.Arg(2).Value()), 1.0)
        int dayNum = (int) Math.round(Math.max(dayNum1 * number, 1.0));
        return sum(arr, dayNum).div(sum(arr, dayNum1));
    }

    // =========================== 1 array + 2 num =========================== //

    // =========================== 1 array + 3 num =========================== //

    public INDArray macd(INDArray arr, Integer dayNum1, Integer dayNum2, Integer dayNum3) {
        // ema(ema(x, p1) - ema(x, p2), p3)
        INDArray ema1 = ema(arr, dayNum1);
        INDArray ema = ema(arr, dayNum2);
        return ema(ema1.sub(ema), dayNum3);
    }

    // =========================== 1 array + 3 num =========================== //

    // =========================== 2 array + 1 num =========================== //

    public INDArray cov(INDArray arr1, INDArray arr2, Integer dayNum) {
        return ArrayUtils.rolling(arr1, arr2, dayNum, ArrayUtils::cov);
    }

    public INDArray corr(INDArray arr1, INDArray arr2, Integer dayNum) {
        return ArrayUtils.rolling(arr1, arr2, dayNum, ArrayUtils::corr);
    }

    public INDArray crr(INDArray arr1, INDArray arr2, Integer dayNum) {
        // corr(rank(x0), rank(x1), p)
        return corr(rank(arr1), rank(arr2), dayNum);
    }

    public INDArray regBeta(INDArray arr1, INDArray arr2, Integer dayNum) {
        INDArray arrCorr = corr(arr1, arr2, dayNum);
        INDArray arrStd1 = std(arr1, dayNum);
        INDArray arrstd = std(arr2, dayNum);
        return arrCorr.mul(arrstd).div(arrStd1);
    }

    public INDArray regResi(INDArray arr1, INDArray arr2, Integer dayNum) {
        INDArray arrBeta = regBeta(arr1, arr2, dayNum);
        return arr2.sub(arr1.mul(arrBeta));
    }

    public INDArray sumIf(INDArray arr, Integer dayNum, INDArray condition) {
        INDArray compare = ArrayUtils.compare(condition, arr, 0.0);
        return sum(compare, dayNum);
    }

    public INDArray wavg(INDArray arr1, INDArray arr2, Double number) {
        // Expr(ExprOp::ADD, e0, e1)
        // e0 = Expr(ExprOp::MULTI, e.Arg(0), Expr(n))
        // e1 = Expr(ExprOp::MULTI, e.Arg(1), Expr(1 - n))
        // n = e.Arg(2).Value(), 0 < n < 1
        INDArray e0 = arr1.mul(number);
        INDArray e1 = arr2.mul(1 - number);
        return e0.add(e1);
    }

    public INDArray ite(INDArray arr1, INDArray arr2, Double number) {
        // Expr(args[0].Value() == 0 ? args[2].Value() : args[1].Value())
        return ArrayUtils.compare(arr1, nums -> nums[0] == 0, number, arr2);
    }

    public INDArray ter(INDArray arr1, INDArray arr2, Double number) {
        // ite(e.Arg(0), e.Arg(1), e.Arg(2))
        return ite(arr1, arr2, number);
    }

    // =========================== 2 array + 1 num =========================== //

    // =========================== none =========================== //

    public INDArray retGap() {
        // diff(open, delay(close, 1))
        return diff(open, delay(close, 1));
    }

    public INDArray retIntra() {
        // diff(close, open)
        return diff(close, open);
    }

    public INDArray ad() {
        // ((close - low) - (high-close)) / (high - low) * vol
        return close.sub(low).sub(high.sub(close)).div(high.sub(low)).mul(vol);
    }

    public INDArray avgp() {
        // (high + low + close) / 3
        return high.add(low).add(close).div(3);
    }

    public INDArray mf() {
        // avgp * volume
        return avgp().mul(vol);
    }

    public INDArray mfm() {
        // ((close - low) - (high - close)) / (high - low)
        return close.sub(low).sub(high.sub(close)).div(high.sub(low));
    }

    public INDArray mfv() {
        // mfm * volume
        return mfm().mul(vol);
    }

    public INDArray dif() {
        // ret == 0.0 ? 0.0 : close - (ret > 0.0 ? min(low, delay(close, 1) : max(high, delay(close, 1))))
        INDArray min = min(low, delay(close, 1));
        INDArray max = max(high, delay(close, 1));
        INDArray fault = close.sub(ArrayUtils.compare(ret, nums -> nums[0] > 0.0, min, max));
        return ArrayUtils.compare(ret, nums -> nums[0] == 0.0, 0.0, fault);
    }

    public INDArray dbm() {
        // open <= delay(open, 1) ? 0.0 : max((high - open), (open - delay(open, 1)))
        INDArray[] sources = {open, delay(open, 1)};
        INDArray fault = max(high.sub(open), open.sub(delay(open, 1)));
        return ArrayUtils.compare(sources, nums -> nums[0] <= nums[1], 0.0, fault);
    }

    public INDArray dtm() {
        // open >= delay(open, 1) ? 0.0 : max((open - low), (open - delay(open ,1)))
        INDArray[] sources = {open, delay(open, 1)};
        INDArray fault = max(open.sub(low), open.sub(delay(open, 1)));
        return ArrayUtils.compare(sources, nums -> nums[0] >= nums[1], 0.0, fault);
    }

    // =========================== none =========================== //

    // =========================== 1 num =========================== //

    public INDArray adtm(Integer dayNum) {
        // (stm - sbm) / max(stm, sbm)
        // stm = sum(dtm, p)
        // sbm = sum(dbm, p)
        INDArray stm = sum(dtm(), dayNum);
        INDArray sbm = sum(dbm(), dayNum);
        return stm.sub(sbm).div(max(stm, sbm));
    }

    public INDArray adx(Integer dayNum) {
        // ma(abs(mdi(p) - pdi(p)) / (mdi(p) + pdi(p)), p)
        INDArray abs = abs(mdi(dayNum).sub(pdi(dayNum)));
        INDArray add = mdi(dayNum).add(pdi(dayNum));
        return ma(abs.div(add), dayNum);
    }

    public INDArray ar(Integer dayNum) {
        // sum(high - open, p) / sum(open - low, p)
        return sum(high.sub(open), dayNum).div(sum(open.sub(low), dayNum));
    }

    public INDArray aroonUp(Integer dayNum) {
        // highDay(high, p) / p
        return highDay(high, dayNum).div(dayNum);
    }

    public INDArray aroonDown(Integer dayNum) {
        // lowDay(low, p) / p
        return lowDay(low, dayNum).div(dayNum);
    }

    public INDArray aroonOsc(Integer dayNum) {
        // aroonUp - aroonDown
        return aroonUp(dayNum).sub(aroonDown(dayNum));
    }

    public INDArray asi(Integer dayNum) {
        // sum(16 * x / r * max(a, b), p)
        // a = abs(high - delay(close, 1))
        // b = abs(low - delay(close, 1))
        // c = abs(high - delay(low, 1))
        // d = abs(delay(close, 1) - delay(open, 1))
        // r = a > b && a > c ? a + b / 2 + d / 4 : (b > c && b > a ? b + a / 2 + d / 4 : c + d / 4)
        // x = delta(close, 1) + (close - open) / 2 + delay(close - open, 1)
        INDArray a = abs(high.sub(delay(close, 1)));
        INDArray b = abs(low.sub(delay(close, 1)));
        INDArray c = abs(high.sub(delay(low, 1)));
        INDArray d = abs(delay(close, 1).sub(delay(open, 1)));
        INDArray[] sources = {a, b, c};
        INDArray fault = ArrayUtils.compare(sources, nums -> nums[1] > nums[2] && nums[1] > nums[0], b.add(a.div(2)).add(d.div(4)), c.add(d.div(4)));
        INDArray r = ArrayUtils.compare(sources, nums -> nums[0] > nums[1] && nums[0] > nums[2], a.add(b.div(2).add(d.div(4))), fault);
        INDArray x = delta(close, 1).add(close.sub(open).div(2)).add(delay(close.sub(open), 1));
        return sum(x.mul(16).div(r).mul(max(a, b)), dayNum);
    }

    public INDArray tr(Integer dayNum) {
        // sum(max(high, delay(close)) - min(low, delay(close)), p)
        INDArray delay = delay(close, 1);
        return sum(max(high, delay).sub(min(low, delay)), dayNum);
    }

    public INDArray atr(Integer dayNum) {
        // tr(p) / p
        return tr(dayNum).div(dayNum);
    }

    public INDArray str(Integer dayNum) {
        // std(max(high, delay(close)) - min(low, delay(close)), p)
        INDArray delay = delay(close, 1);
        return std(max(high, delay).sub(min(low, delay)), dayNum);
    }

    public INDArray bp(Integer dayNum) {
        // sum(close - min(low, delay(close)), p)
        return sum(close.sub(min(low, delay(close, 1))), dayNum);
    }

    public INDArray br(Integer dayNum) {
        // sum(relu2(high - delay(close, 1)), p) / sum(relu2(delay(close, 1) - low), p)
        INDArray a = sum(relu(high.sub(delay(close, 1))), dayNum);
        INDArray b = sum(relu(delay(close, 1).sub(low)), dayNum);
        return a.div(b);
    }

    public INDArray cci(Integer dayNum) {
        // (avgp - ma(avgp, p)) / (0.15 * std(avgp, p))
        INDArray a = avgp().sub(ma(avgp(), dayNum));
        INDArray b = std(avgp(), dayNum).mul(0.15);
        return a.div(b);
    }

    public INDArray cmo(Integer dayNum) {
        // (su - sd) / (su + sd)
        // su = sum(relu2(delta(close, 1)), p)
        // sd = sum(relu2(-delta(close, 1)), p)
        INDArray deltaClose = delta(close, 1);
        INDArray su = sum(relu(deltaClose), dayNum);
        INDArray sd = sum(relu(deltaClose.mul(-1)), dayNum);
        return su.sub(sd).div(su.add(sd));
    }

    public INDArray cr(Integer dayNum) {
        // sum(relu2(high - delay(avgp, 1)), p) / sum(relu2(delay(avgp, 1) - low), p)
        INDArray delayAvgp = delay(avgp(), 1);
        INDArray a = sum(relu(high.sub(delayAvgp)), dayNum);
        INDArray b = sum(relu(delayAvgp.sub(low)), dayNum);
        return a.div(b);
    }

    public INDArray ddi(Integer dayNum) {
        // (sum(dmz, p) - sum(dmf, p)) / (sum(dmz, p) + sum(dmf, p))
        // dmz = (high + low) <= delay(high + low, 1) ? 0.0 : max(abs(delta(high, 1)), abs(delta(low, 1)))
        // dmf = (high + low) >= delay(high + low, 1) ? 0.0 : max(abs(delta(high, 1)), abs(delta(low, 1)))
        INDArray con1 = high.add(low);
        INDArray con2 = delay(con1, 1);
        INDArray[] cons = {con1, con2};
        INDArray fault = max(abs(delta(high, 1)), abs(delta(low, 1)));
        INDArray dmz = ArrayUtils.compare(cons, nums -> nums[0] <= nums[1], 0.0, fault);
        INDArray dmf = ArrayUtils.compare(cons, nums -> nums[0] >= nums[1], 0.0, fault);
        INDArray a = sum(dmz, dayNum);
        INDArray b = sum(dmf, dayNum);
        return a.sub(b).div(a.add(b));
    }

    public INDArray dmm(Integer dayNum) {
        // sumIf(ld > relu2(hd), p, ld)
        // hd = high - delay(high, 1)
        // ld = delay(low, 1) - low
        INDArray hd = high.sub(delay(high, 1));
        INDArray ld = delay(low, 1).sub(low);
        INDArray[] sources = {ld, relu(hd)};
        INDArray condition = ArrayUtils.condition(sources, nums -> nums[0] > nums[1]);
        return sumIf(condition, dayNum, ld);
    }

    public INDArray dmm2(Integer dayNum) {
        // sumIf(ld, p, ld > relu2(hd))
        // hd = high - delay(high, 1)
        // ld = delay(low, 1) - low
        INDArray hd = high.sub(delay(high, 1));
        INDArray ld = delay(low, 1).sub(low);
        INDArray[] sources = {ld, relu(hd)};
        INDArray condition = ArrayUtils.condition(sources, nums -> nums[0] > nums[1]);
        return sumIf(ld, dayNum, condition);
    }

    public INDArray dmp(Integer dayNum) {
        // sumIf(hd > relu2(ld), p, hd)
        // hd = high - delay(high, 1)
        // ld = delay(low, 1) - low
        INDArray hd = high.sub(delay(high, 1));
        INDArray ld = delay(low, 1).sub(low);
        INDArray[] sources = {hd, relu(ld)};
        INDArray condition = ArrayUtils.condition(sources, nums -> nums[0] > nums[1]);
        return sumIf(condition, dayNum, hd);
    }

    public INDArray dmp2(Integer dayNum) {
        // sumIf(hd, p, hd > relu2(ld))
        // hd = high - delay(high, 1)
        // ld = delay(low, 1) - low
        INDArray hd = high.sub(delay(high, 1));
        INDArray ld = delay(low, 1).sub(low);
        INDArray[] sources = {hd, relu(ld)};
        INDArray condition = ArrayUtils.condition(sources, nums -> nums[0] > nums[1]);
        return sumIf(hd, dayNum, condition);
    }

    public INDArray eom(Integer dayNum) {
        // ma(delta(avg(high, low), 1) * (high - low) / volume, p)
        return ma(delta(avg(high, low), 1).mul(high.sub(low)).div(vol), dayNum);
    }

    public INDArray er(Integer dayNum) {
        // abs(delta(close, p)) / sum(abs(delta(close, 1)), p)
        return abs(delta(close, dayNum)).div(sum(abs(delta(close, 1)), dayNum));
    }

    public INDArray mdi(Integer dayNum) {
        // dmm(p) / tr(p)
        return dmm(dayNum).div(tr(dayNum));
    }

    public INDArray mdi2(Integer dayNum) {
        // dmm2(p) / tr(p)
        return dmm2(dayNum).div(tr(dayNum));
    }

    public INDArray mfi(Integer dayNum) {
        // 100 - 100 / (1 + V1)
        // V1 = sum(avgp > delay(avgp, 1), p) / sum(avgp < delay(avgp, 1), p)
        INDArray avgp = avgp();
        INDArray[] sources = {avgp, delay(avgp, 1)};
        INDArray con1 = ArrayUtils.condition(sources, nums -> nums[0] > nums[1]);
        INDArray con2 = ArrayUtils.condition(sources, nums -> nums[0] < nums[1]);
        INDArray v1 = sum(con1, dayNum).div(sum(con2, dayNum));
        return Transforms.pow(v1.add(1), -1).mul(-100).add(100);
    }

    public INDArray mfi2(Integer dayNum) {
        // 100 - 100 / (1 +V1)
        // V1 = sumIf(avgp * vol, p, avgp > delay(avgp, 1)) / sumIf(avgp * vol, p, avgp < delay(avgp, 1))
        INDArray avgp = avgp();
        INDArray[] sources = {avgp, delay(avgp, 1)};
        INDArray con1 = ArrayUtils.condition(sources, nums -> nums[0] > nums[1]);
        INDArray con2 = ArrayUtils.condition(sources, nums -> nums[0] < nums[1]);
        INDArray arr = avgp.mul(vol);
        INDArray v1 = sumIf(arr, dayNum, con1).div(sumIf(arr, dayNum, con2));
        return Transforms.pow(v1.add(1), -1).mul(-100).add(100);
    }

    public INDArray obv(Integer dayNum) {
        // sum(ret > 0 ? volume : (ret < 0 ? -volume : 0), p)
        INDArray less = ArrayUtils.compare(ret, nums -> nums[0] < 0, vol.mul(-1), 0.0);
        INDArray more = ArrayUtils.compare(ret, nums -> nums[0] > 0, vol, less);
        return sum(more, dayNum);
    }

    public INDArray pdi(Integer dayNum) {
        // dmp(p) / tr(p)
        return dmp(dayNum).div(tr(dayNum));
    }

    public INDArray pdi2(Integer dayNum) {
        // dmp(p) / tr(p)
        return dmp2(dayNum).div(tr(dayNum));
    }

    public INDArray psy(Integer dayNum) {
        // count(ret > 0, p) / p
        return count(ArrayUtils.condition(ret, nums -> nums[0] > 0), dayNum).div(dayNum);
    }

    public INDArray nvi(Integer dayNum) {
        // sumIf(ret, p, volume < delay(volume, 1))
        INDArray[] sources = {vol, delay(vol, 1)};
        return sumIf(ret, dayNum, ArrayUtils.condition(sources, nums -> nums[0] < nums[1]));
    }

    public INDArray pvi(Integer dayNum) {
        // sumIf(ret, p, volume > delay(volume, 1))
        INDArray[] sources = {vol, delay(vol, 1)};
        return sumIf(ret, dayNum, ArrayUtils.condition(sources, nums -> nums[0] > nums[1]));
    }

    public INDArray rvi(Integer dayNum) {
        // aup / (aup + adown)
        // up = ret > 0 ? std(close, p) : 0.0
        // aUp = ma(up, p)
        // down = ret <= 0 ? std(close, p) : 0.0
        // aDown = ma(down, p)
        INDArray std = std(close, dayNum);
        INDArray up = ArrayUtils.compare(ret, nums -> nums[0] > 0, std, 0.0);
        INDArray aUp = ma(up, dayNum);
        INDArray down = ArrayUtils.compare(ret, nums -> nums[0] <= 0, std, 0.0);
        INDArray aDown = ma(down, dayNum);
        return aUp.div(aUp.add(aDown));
    }

    public INDArray stochK(Integer dayNum) {
        // (close - tsMin(low, p)) / tsRngP(p)
        return close.sub(tsMin(low, dayNum)).div(tsRngP(dayNum));
    }

    public INDArray stochD(Integer dayNum) {
        // ma(stoch_k(p), 3)
        return ma(stochK(dayNum), 3);
    }

    public INDArray stochRsi(Integer dayNum) {
        // scale(rsi(close, p), p)
        return scale(rsi(close, dayNum), dayNum);
    }

    public INDArray trix(Integer dayNum) {
        // ret(ma(ma(ma(close, p), p), p), 1)
        return ret(ma(ma(ma(close, dayNum), dayNum), dayNum), 1);
    }

    public INDArray tsRngP(Integer dayNum) {
        // tsMax(high, p) - tsMin(low, p)
        return tsMax(high, dayNum).sub(tsMin(low, dayNum));
    }

    public INDArray ulcer(Integer dayNum) {
        // (sum(diff(close, tsMax(close, p)) ^ 2, p) / p) ^ 0.5
        INDArray pow = Transforms.pow(diff(close, tsMax(close, dayNum)), 2);
        return Transforms.pow(sum(pow, dayNum).div(dayNum), 0.5);
    }

    public INDArray uo(Integer dayNum) {
        // (4 * av1 + 2 * av2 + av4) / 7
        // av1 = bp(p) / tr(p)
        // av2 = bp(2 * p) / tr(2 * p)
        // av4 = bp(4 * p) / tr(4 * p)
        INDArray av1 = bp(dayNum).div(tr(dayNum));
        INDArray av2 = bp(2 * dayNum).div(tr(2 * dayNum));
        INDArray av4 = bp(4 * dayNum).div(tr(4 * dayNum));
        return av1.mul(4).add(av2.mul(2).add(av4)).div(7);
    }

    public INDArray vrsi(Integer dayNum) {
        // ma(relu2(delta(volume, 1)), p) / ma(abs(delta(volume, 1)), p)
        INDArray delta = delta(vol, 1);
        return ma(relu(delta), dayNum).div(ma(abs(delta), dayNum));
    }

    public INDArray vtxMinus(Integer dayNum) {
        // sum(abs(low - delay(high)), p) / tr(p)
        return sum(abs(low.sub(delay(high, 1))), dayNum).div(tr(dayNum));
    }

    public INDArray vtxPlus(Integer dayNum) {
        // sum(abs(high - delay(low)), p) / tr(p)
        return sum(abs(high.sub(delay(low, 1))), dayNum).div(tr(dayNum));
    }

    public INDArray volatility(Integer dayNum) {
        return std(ret, dayNum);
    }

    public INDArray williams(Integer dayNum) {
        // (tsMax(high, p) - close) / tsRngP(p)
        return tsMax(high, dayNum).sub(close).div(tsRngP(dayNum));
    }

    // =========================== 1 num =========================== //

    // =========================== 2 num =========================== //

    public INDArray ppo(Integer dayNum1, Integer dayNum2) {
        // diff(ma(close, p0), ma(close, p1))
        return diff(ma(close, dayNum1), ma(close, dayNum2));
    }

    public INDArray pvo(Integer dayNum1, Integer dayNum2) {
        // diff(ma(volume, p0), ma(volume, p1))
        return diff(ma(vol, dayNum1), ma(vol, dayNum2));
    }

    public INDArray tsi(Integer dayNum1, Integer dayNum2) {
        // ma(sm1, p1) / ma(abs_sm1, p1)
        // sm1 = ma(delta(close, 1), p0)
        // abs_sm1 = ma(abs(delta(close, 1)), p0)
        INDArray delta = delta(close, 1);
        INDArray sm1 = ma(delta, dayNum1);
        INDArray abs_sm1 = ma(abs(delta), dayNum1);
        return ma(sm1, dayNum2).div(ma(abs_sm1, dayNum2));
    }

    // =========================== 2 num =========================== //

    // =========================== 3 num =========================== //

    public INDArray copPock(Integer dayNum1, Integer dayNum2, Integer dayNum3) {
        // ma(ret(close, p0) + ret(close, p1), p2)
        return ma(ret(close, dayNum1).add(ret(close, dayNum2)), dayNum3);
    }

    public INDArray dbcd(Integer dayNum1, Integer dayNum2, Integer dayNum3) {
        // ma(delta(bias(close, p0), p1), p2)
        return ma(delta(bias(close, dayNum1), dayNum2), dayNum3);
    }

    public INDArray kdjK(Integer dayNum1, Integer dayNum2, Integer dayNum3) {
        // ma(stoch_k(p0), p1)
        return ma(stochK(dayNum1), dayNum2);
    }

    public INDArray kdjD(Integer dayNum1, Integer dayNum2, Integer dayNum3) {
        // ma(kdj_k(p0, p1, p2), p2)
        return ma(kdjK(dayNum1, dayNum2, dayNum3), dayNum3);
    }

    public INDArray kdjJ(Integer dayNum1, Integer dayNum2, Integer dayNum3) {
        // 3 * kdj_k(p0, p1, p2) - 2 * kdj_d(p0, p1, p2)
        return kdjK(dayNum1, dayNum2, dayNum3).mul(3).sub(kdjD(dayNum1, dayNum2, dayNum3));
    }

    // =========================== 3 num =========================== //

    // =========================== 4 num =========================== //

    public INDArray bbi(Integer dayNum1, Integer dayNum2, Integer dayNum3, Integer dayNum4) {
        // (ma(close, p1) + ma(close, p2) + ma(close, p4) + ma(close, p8)) / 4
        INDArray ma1 = ma(close, dayNum1);
        INDArray ma = ma(close, dayNum2);
        INDArray ma3 = ma(close, dayNum3);
        INDArray ma4 = ma(close, dayNum4);
        return ma1.add(ma).add(ma3).add(ma4).div(4);
    }

    // =========================== 4 num =========================== //

}
