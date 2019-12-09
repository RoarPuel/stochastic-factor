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
        return ArrayUtils.rolling(arr, dayNum, (RollingDouble) array -> {
            double min = Double.NaN;
            for (double current : array) {
                if (Double.isNaN(min) || current < min) {
                    min = current;
                }
            }
            return min;
        });
    }

    public static INDArray llv(INDArray arr, Integer dayNum) {
        return tsMin(arr, dayNum);
    }

    public static INDArray tsMax(INDArray arr, Integer dayNum) {
        return ArrayUtils.rolling(arr, dayNum, (RollingDouble) array -> {
            double max = Double.NaN;
            for (double current : array) {
                if (Double.isNaN(max) || current > max) {
                    max = current;
                }
            }
            return max;
        });
    }

    public static INDArray hhv(INDArray arr, Integer dayNum) {
        return tsMax(arr, dayNum);
    }

    public static INDArray tsRank(INDArray arr, Integer dayNum) {
        return ArrayUtils.rolling(arr, dayNum, (RollingDouble) array -> {
            double n = (double) array.length;
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
        return ArrayUtils.rolling(arr, dayNum, (RollingDouble) array -> {
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

    public static INDArray lowDay(INDArray arr, Integer dayNum) {
        return ArrayUtils.rolling(arr, dayNum, (RollingDouble) array -> {
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

    public static INDArray ret(INDArray arr, Integer dayNum) {
        return ArrayUtils.rolling(arr, dayNum, (array1, array2) -> array1.div(array2).sub(1.0));
    }

    public static INDArray wma(INDArray arr, Integer dayNum) {
        return ArrayUtils.rolling(arr, dayNum, (RollingDouble) array -> {
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

}
