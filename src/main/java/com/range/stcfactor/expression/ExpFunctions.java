package com.range.stcfactor.expression;

import com.range.stcfactor.common.utils.ArrayUtils;
import com.range.stcfactor.common.helper.RollingArray;
import com.range.stcfactor.common.helper.RollingDouble;
import com.range.stcfactor.signal.data.DataFactory;
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

    private DataFactory factory;

    public ExpFunctions() {

    }

    public ExpFunctions(DataFactory factory) {
        this.factory = factory;
    }

    public INDArray sum(INDArray arr1, INDArray arr2) {
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

    public INDArray tsSum(INDArray arr, Integer dayNum) {
        return ArrayUtils.rolling(arr, dayNum, (RollingArray) array -> array.sum(0));
    }

    public INDArray std(INDArray arr, Integer dayNum) {
        return ArrayUtils.rolling(arr, dayNum, (RollingArray) array -> array.std(0));
    }

    public INDArray mean(INDArray arr, Integer dayNum) {
        return ArrayUtils.rolling(arr, dayNum, (RollingArray) array -> array.mean(0));
    }

    public INDArray prod(INDArray arr, Integer dayNum) {
        return ArrayUtils.rolling(arr, dayNum, (RollingArray) array -> array.prod(0));
    }

    public INDArray tsMin(INDArray arr, Integer dayNum) {
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

    public INDArray llv(INDArray arr, Integer dayNum) {
        return tsMin(arr, dayNum);
    }

    public INDArray tsMax(INDArray arr, Integer dayNum) {
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

    public INDArray hhv(INDArray arr, Integer dayNum) {
        return tsMax(arr, dayNum);
    }

    public INDArray tsRank(INDArray arr, Integer dayNum) {
        return ArrayUtils.rolling(arr, dayNum, (RollingDouble) array -> {
            double n = (double) array.length;
            Integer[] sort = ArrayUtils.argSort(array);
            List<Double> range = ArrayUtils.range(0, n, 1.0, num -> (num + 1.0) / n);
            return range.get(sort[sort.length-1]);
        });
    }

    public INDArray cov(INDArray arr1, INDArray arr2, Integer dayNum) {
        return ArrayUtils.rolling(arr1, arr2, dayNum, ArrayUtils::cov);
    }

    public INDArray corr(INDArray arr1, INDArray arr2, Integer dayNum) {
        return ArrayUtils.rolling(arr1, arr2, dayNum, ArrayUtils::corr);
    }

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

    public INDArray delay(INDArray arr, Integer dayNum) {
        return ArrayUtils.shift(arr, dayNum);
    }

    public INDArray delta(INDArray arr, Integer dayNum) {
        return arr.sub(ArrayUtils.shift(arr, dayNum));
    }

    public INDArray decayLinear(INDArray arr, Integer dayNum) {
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

    public INDArray min(INDArray arr1, INDArray arr2) {
        INDArray arrDelta = arr1.sub(arr2);
        return arr1.sub(ArrayUtils.replaceLess(arrDelta, 0.0,0.0));
    }

    public INDArray max(INDArray arr1, INDArray arr2) {
        INDArray arrDelta = arr1.sub(arr2);
        return arr1.sub(ArrayUtils.replaceGreater(arrDelta, 0.0, 0.0));
    }

    public INDArray count(INDArray arr, Integer dayNum) {
        INDArray arrCon = Transforms.sign(ArrayUtils.replaceNan(arr, 0.0));
        return tsSum(ArrayUtils.replaceEquals(arrCon, -1.0, 0.0), dayNum);
    }

    public INDArray sumIf(INDArray arr, Integer dayNum) {
        INDArray arrCon = Transforms.sign(ArrayUtils.replaceNan(arr, 0.0));
        INDArray arrSelect = arr.mul(arrCon);
        return tsSum(arrSelect, dayNum);
    }

    public INDArray sma(INDArray arr, Integer dayNum1, Integer dayNum2) {
        return mean(arr, dayNum1>=dayNum2 ? dayNum1/dayNum2 : dayNum2/dayNum1);
    }

    public INDArray highDay(INDArray arr, Integer dayNum) {
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

    public INDArray lowDay(INDArray arr, Integer dayNum) {
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

    public INDArray ret(INDArray arr, Integer dayNum) {
        return ArrayUtils.rolling(arr, dayNum, (array1, array2) -> array1.div(array2).sub(1.0));
    }

    public INDArray wma(INDArray arr, Integer dayNum) {
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

    public INDArray regBeta(INDArray arr1, INDArray arr2, Integer dayNum) {
        INDArray arrCorr = corr(arr1, arr2, dayNum);
        INDArray arrStd1 = std(arr1, dayNum);
        INDArray arrStd2 = std(arr2, dayNum);
        return arrCorr.mul(arrStd2).div(arrStd1);
    }

    public INDArray regResi(INDArray arr1, INDArray arr2, Integer dayNum) {
        INDArray arrBeta = regBeta(arr1, arr2, dayNum);
        return arr2.sub(arr1.mul(arrBeta));
    }

}
