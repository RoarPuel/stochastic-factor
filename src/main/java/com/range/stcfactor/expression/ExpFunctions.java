package com.range.stcfactor.expression;

import com.range.stcfactor.common.utils.ArrayUtils;
import com.range.stcfactor.common.helper.RollingArray;
import com.range.stcfactor.common.helper.RollingDouble;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.ops.transforms.Transforms;

import java.util.List;

/**
 * 表达式
 *
 * @author zrj5865@163.com
 * @create 2019-07-22
 */
public class ExpFunctions {

    public static INDArray sum(INDArray arr1, INDArray arr2) {
        return arr1.add(arr2);
    }

    public static INDArray sub(INDArray arr1, INDArray arr2) {
        return arr1.sub(arr2);
    }

    public static INDArray mul(INDArray arr1, INDArray arr2) {
        return arr1.mul(arr2);
    }

    public static INDArray div(INDArray arr1, INDArray arr2) {
        return arr1.div(arr2);
    }

    public static INDArray tsSum(INDArray arr, Integer dayNum) {
        return ArrayUtils.rolling(arr, dayNum, (RollingArray) array -> array.sum(0));
    }

    public static INDArray std(INDArray arr, Integer dayNum) {
        return ArrayUtils.rolling(arr, dayNum, (RollingArray) array -> array.std(0));
    }

    public static INDArray mean(INDArray arr, Integer dayNum) {
        return ArrayUtils.rolling(arr, dayNum, (RollingArray) array -> array.mean(0));
    }

    public static INDArray prod(INDArray arr, Integer dayNum) {
        return ArrayUtils.rolling(arr, dayNum, (RollingArray) array -> array.prod(0));
    }

    public static INDArray tsMin(INDArray arr, Integer dayNum) {
        return ArrayUtils.rolling(arr, dayNum, ArrayUtils::min);
    }

    public static INDArray llv(INDArray arr, Integer dayNum) {
        return tsMin(arr, dayNum);
    }

    public static INDArray tsMax(INDArray arr, Integer dayNum) {
        return ArrayUtils.rolling(arr, dayNum, ArrayUtils::max);
    }

    public static INDArray hhv(INDArray arr, Integer dayNum) {
        return tsMax(arr, dayNum);
    }

    public static INDArray tsRank(INDArray arr, Integer dayNum) {
        return ArrayUtils.rolling(arr, dayNum, (RollingDouble) array -> {
            double n = (double) array.columns();
            Integer[] sort = ArrayUtils.argSort(array);
            List<Double> range = ArrayUtils.range(0, n, 1.0, num -> (num + 1.0) / n);
            return range.get(sort[sort.length-1]);
        });
    }

    public static INDArray cov(INDArray arr1, INDArray arr2, Integer dayNum) {
        return ArrayUtils.rolling(arr1, arr2, dayNum, ArrayUtils::cov);
    }

    public static INDArray corr(INDArray arr1, INDArray arr2, Integer dayNum) {
        return ArrayUtils.rolling(arr1, arr2, dayNum, ArrayUtils::corr);
    }

    public static INDArray abs(INDArray arr) {
        return Transforms.abs(arr);
    }

    public static INDArray relu(INDArray arr) {
        return Transforms.relu(arr);
    }

    public static INDArray sigmoid(INDArray arr) {
        return Transforms.sigmoid(arr);
    }

    public static INDArray log(INDArray arr) {
        return Transforms.log(Transforms.abs(arr.add(1.0)));
    }

    public static INDArray sqrt(INDArray arr) {
        return Transforms.sqrt(Transforms.abs(arr));
    }

    public static INDArray square(INDArray arr) {
        return Transforms.pow(arr, 2);
    }

    public static INDArray sign(INDArray arr) {
        return Transforms.sign(ArrayUtils.replaceNan(arr, 0.0));
    }

    public static INDArray exp(INDArray arr) {
        INDArray array = ArrayUtils.replaceNan(arr, 0.0);
        INDArray arrNorm = arr.sub(array.mean(0)).div(array.std(0).add(1.0));
        return Transforms.exp(arrNorm);
    }

    public static INDArray rank(INDArray arr) {
        return ArrayUtils.rank(arr, false, "first", false);
    }

    public static INDArray rankPct(INDArray arr) {
        return ArrayUtils.rank(arr, false, "first", true);
    }

    public static INDArray delay(INDArray arr, Integer dayNum) {
        return ArrayUtils.shift(arr, dayNum);
    }

    public static INDArray delta(INDArray arr, Integer dayNum) {
        return arr.sub(ArrayUtils.shift(arr, dayNum));
    }

    public static INDArray decayLinear(INDArray arr, Integer dayNum) {
        return ArrayUtils.rolling(arr, dayNum, (RollingDouble) array -> {
            final double[] sum = {0.0};
            List<Double> decayWeights = ArrayUtils.rangeClosed(1.0, (double) array.columns(), 1.0, num -> {
                sum[0] += num;
                return num;
            });
            double total = 0;
            for (int i=0; i<array.columns(); i++) {
                total += array.getDouble(i) * decayWeights.get(i) / sum[0];
            }
            return total;
        });
    }

    public static INDArray min(INDArray arr1, INDArray arr2) {
        INDArray arrDelta = arr1.sub(arr2);
        return arr1.sub(ArrayUtils.replaceLess(arrDelta, 0.0,0.0));
    }

    public static INDArray max(INDArray arr1, INDArray arr2) {
        INDArray arrDelta = arr1.sub(arr2);
        return arr1.sub(ArrayUtils.replaceGreater(arrDelta, 0.0, 0.0));
    }

    public static INDArray count(INDArray arr, Integer dayNum) {
        INDArray arrCon = Transforms.sign(ArrayUtils.replaceNan(arr, 0.0));
        return tsSum(ArrayUtils.replaceEquals(arrCon, -1.0, 0.0), dayNum);
    }

    public static INDArray sumIf(INDArray arr, Integer dayNum) {
        INDArray arrCon = Transforms.sign(ArrayUtils.replaceNan(arr, 0.0));
        INDArray arrSelect = arr.mul(arrCon);
        return tsSum(arrSelect, dayNum);
    }

    public static INDArray sma(INDArray arr, Integer dayNum1, Integer dayNum2) {
        return mean(arr, dayNum1>=dayNum2 ? dayNum1/dayNum2 : dayNum2/dayNum1);
    }

    public static INDArray highDay(INDArray arr, Integer dayNum) {
        return ArrayUtils.rolling(arr, dayNum, (RollingDouble) array -> array.columns() - 1 - ArrayUtils.argMax(array));
    }

    public static INDArray lowDay(INDArray arr, Integer dayNum) {
        return ArrayUtils.rolling(arr, dayNum, (RollingDouble) array -> array.columns() - 1 - ArrayUtils.argMin(array));
    }

    public static INDArray ret(INDArray arr, Integer dayNum) {
        return ArrayUtils.rolling(arr, dayNum, (array1, array2) -> array1.div(array2).sub(1.0));
    }

    public static INDArray wma(INDArray arr, Integer dayNum) {
        return ArrayUtils.rolling(arr, dayNum, (RollingDouble) array -> {
            final int[] index = {array.columns() - 1};
            final double[] sum = {0.0};
            ArrayUtils.rangeClosed(1.0, (double) array.columns(), 1.0, num -> {
                double mul = Math.pow(0.9, num) * array.getDouble(index[0]);
                index[0]--;
                sum[0] += mul;
                return mul;
            });
            return sum[0];
        });
    }

    public static INDArray regBeta(INDArray arr1, INDArray arr2, Integer dayNum) {
        INDArray arrCorr = corr(arr1, arr2, dayNum);
        INDArray arrStd1 = std(arr1, dayNum);
        INDArray arrStd2 = std(arr2, dayNum);
        return arrCorr.mul(arrStd2).div(arrStd1);
    }

    public static INDArray regResi(INDArray arr1, INDArray arr2, Integer dayNum) {
        INDArray arrBeta = regBeta(arr1, arr2, dayNum);
        return arr2.sub(arr1.mul(arrBeta));
    }

//    public static void main(String[] args) {
//        INDArray open = FileUtils.readData("D:\\Work\\Project\\Java\\stochastic-factor\\data\\open.csv");
//        INDArray high = FileUtils.readData("D:\\Work\\Project\\Java\\stochastic-factor\\data\\high.csv");
//        INDArray low = FileUtils.readData("D:\\Work\\Project\\Java\\stochastic-factor\\data\\low.csv");
//        INDArray close = FileUtils.readData("D:\\Work\\Project\\Java\\stochastic-factor\\data\\close.csv");
//        INDArray vol = FileUtils.readData("D:\\Work\\Project\\Java\\stochastic-factor\\data\\vol.csv");
//        INDArray share = FileUtils.readData("D:\\Work\\Project\\Java\\stochastic-factor\\data\\share.csv");
//        INDArray turnover = FileUtils.readData("D:\\Work\\Project\\Java\\stochastic-factor\\data\\turnover.csv");
//        System.out.println(corr(abs(sigmoid(vol)),mul(div(low,turnover),exp(low)),119));
//
//        INDArray arr = Nd4j.create(new double[][]{{Double.NaN,-2.0,3.0},{4.0,Double.NaN,-6.0},{4.0,-8.0,Double.NaN},{12.0,12.0,25.0}});
//        INDArray arr1 = Nd4j.create(new double[][]{{4.0,-2.0,33.0},{24.0,2.0,-6.0},{-7.0,18.0,25.0},{12.0,58.0,32.0}});
//        INDArray arr = FileUtils.readData("D:\\Work\\Project\\Java\\stochastic-factor\\data\\open.csv");
//        INDArray arr1 = FileUtils.readData("D:\\Work\\Project\\Java\\stochastic-factor\\data\\close.csv");
//
//        System.out.println();
//        System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
//        System.out.println("arr: " + arr);
//        System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
//        System.out.println("arr1: " + arr1);
//        System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
//        System.out.println();
//
//        long startTime = System.currentTimeMillis();
//        System.out.println("sum: " + sum(arr, arr1));
//        System.out.println("sub: " + sub(arr, arr1));
//        System.out.println("mul: " + mul(arr, arr1));
//        System.out.println("div: " + div(arr, arr1));
//        System.out.println("tsSum: " + tsSum(arr, 2));
//        System.out.println("std: " + std(arr, 2));
//        System.out.println("mean: " + mean(arr, 2));
//        System.out.println("prod: " + prod(arr, 2));
//        System.out.println("tsMin: " + tsMin(arr, 2));
//        System.out.println("llv: " + llv(arr, 2));
//        System.out.println("tsMax: " + tsMax(arr, 2));
//        System.out.println("hhv: " + hhv(arr, 2));
//        System.out.println("tsRank: " + tsRank(arr, 2));
//        System.out.println("cov: " + cov(arr, arr1, 3));
//        System.out.println("corr: " + corr(arr, arr1, 3));
//        System.out.println("abs: " + abs(arr));
//        System.out.println("relu: " + relu(arr));
//        System.out.println("sigmoid: " + sigmoid(arr));
//        System.out.println("log: " + log(arr));
//        System.out.println("sqrt: " + sqrt(arr));
//        System.out.println("square: " + square(arr));
//        System.out.println("sign: " + sign(arr));
//        System.out.println("exp: " + exp(arr));
//        System.out.println("rank: " + rank(arr));
//        System.out.println("rankPct: " + rankPct(arr));
//        System.out.println("delay: " + delay(arr, 2));
//        System.out.println("delta: " + delta(arr, 2));
//        System.out.println("decayLinear: " + decayLinear(arr, 2));
//        System.out.println("min: " + min(arr, arr1));
//        System.out.println("max: " + max(arr, arr1));
//        System.out.println("count: " + count(arr, 2));
//        System.out.println("sumIf: " + sumIf(arr, 2));
//        System.out.println("sma: " + sma(arr, 4, 3));
//        System.out.println("highDay: " + highDay(arr, 2));
//        System.out.println("lowDay: " + lowDay(arr, 2));
//        System.out.println("ret: " + ret(arr, 2));
//        System.out.println("wma: " + wma(arr, 2));
//        System.out.println("regBeta: " + regBeta(arr, arr1, 2));
//        System.out.println("regResi: " + regResi(arr, arr1, 2));
//        System.out.println("==================================================================================> cost "
//                + (System.currentTimeMillis() - startTime) / 1000 + "s");
//    }

}
