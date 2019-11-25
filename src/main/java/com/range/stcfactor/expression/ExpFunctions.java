package com.range.stcfactor.expression;

import com.range.stcfactor.common.utils.ArrayUtils;
import org.nd4j.linalg.api.buffer.DataType;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.api.ops.impl.reduce.longer.MatchCondition;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.indexing.BooleanIndexing;
import org.nd4j.linalg.indexing.NDArrayIndex;
import org.nd4j.linalg.indexing.conditions.Conditions;
import org.nd4j.linalg.ops.transforms.Transforms;

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
        long[] shape = {arr.rows(), arr.columns()};
        INDArray result = Nd4j.valueArrayOf(shape, Double.NaN, DataType.DOUBLE);
        for (int current=0; current < arr.rows(); current++) {
            if (current + 1 < dayNum) {
                continue;
            }

            int upper = current + 1;
            int lower = upper - dayNum;
            result.putRow(current, arr.get(NDArrayIndex.interval(lower, upper), NDArrayIndex.all()).sum(0));
        }
        return result;
    }

    public static INDArray std(INDArray arr, Integer dayNum) {
        long[] shape = {arr.rows(), arr.columns()};
        INDArray result = Nd4j.valueArrayOf(shape, Double.NaN, DataType.DOUBLE);
        for (int current=0; current < arr.rows(); current++) {
            if (current + 1 < dayNum) {
                continue;
            }

            int upper = current + 1;
            int lower = upper - dayNum;
            result.putRow(current, arr.get(NDArrayIndex.interval(lower, upper), NDArrayIndex.all()).std(0));
        }
        return result;
    }

    public static INDArray mean(INDArray arr, Integer dayNum) {
        long[] shape = {arr.rows(), arr.columns()};
        INDArray result = Nd4j.valueArrayOf(shape, Double.NaN, DataType.DOUBLE);
        for (int current=0; current < arr.rows(); current++) {
            if (current + 1 < dayNum) {
                continue;
            }

            int upper = current + 1;
            int lower = upper - dayNum;
            result.putRow(current, arr.get(NDArrayIndex.interval(lower, upper), NDArrayIndex.all()).mean(0));
        }
        return result;
    }

    public static INDArray tsMin(INDArray arr, Integer dayNum) {
        long[] shape = {arr.rows(), arr.columns()};
        INDArray result = Nd4j.valueArrayOf(shape, Double.NaN, DataType.DOUBLE);
        for (int current=0; current < arr.rows(); current++) {
            if (current + 1 < dayNum) {
                continue;
            }

            int upper = current + 1;
            int lower = upper - dayNum;
            INDArray temp = arr.get(NDArrayIndex.interval(lower, upper), NDArrayIndex.all());
            long[] rowShape = {1, temp.columns()};
            INDArray row = Nd4j.valueArrayOf(rowShape, Double.NaN, DataType.DOUBLE);
            for (int i=0; i<temp.columns(); i++) {
                INDArray column = temp.getColumn(i);
                int countNan = Nd4j.getExecutioner().exec(new MatchCondition(column, Conditions.isNan())).getInt(0);
                if (countNan != 0) {
                    continue;
                }
                row.putColumn(i, column.min(0));
            }
            result.putRow(current, row);
        }
        return result;
    }

    public static INDArray llv(INDArray arr, Integer dayNum) {
        return tsMin(arr, dayNum);
    }

    public static INDArray tsMax(INDArray arr, Integer dayNum) {
        long[] shape = {arr.rows(), arr.columns()};
        INDArray result = Nd4j.valueArrayOf(shape, Double.NaN, DataType.DOUBLE);
        for (int current=0; current < arr.rows(); current++) {
            if (current + 1 < dayNum) {
                continue;
            }

            int upper = current + 1;
            int lower = upper - dayNum;
            INDArray temp = arr.get(NDArrayIndex.interval(lower, upper), NDArrayIndex.all());
            long[] rowShape = {1, temp.columns()};
            INDArray row = Nd4j.valueArrayOf(rowShape, Double.NaN, DataType.DOUBLE);
            for (int i=0; i<temp.columns(); i++) {
                INDArray column = temp.getColumn(i);
                int countNan = Nd4j.getExecutioner().exec(new MatchCondition(column, Conditions.isNan())).getInt(0);
                if (countNan != 0) {
                    continue;
                }
                row.putColumn(i, column.max(0));
            }
            result.putRow(current, row);
        }
        return result;
    }

    public static INDArray hhv(INDArray arr, Integer dayNum) {
        return tsMax(arr, dayNum);
    }

    public static INDArray tsRank(INDArray arr, Integer dayNum) {
        long[] shape = {arr.rows(), arr.columns()};
        INDArray result = Nd4j.valueArrayOf(shape, Double.NaN, DataType.DOUBLE);
        for (int current=0; current < arr.rows(); current++) {
            if (current + 1 < dayNum) {
                continue;
            }

            int upper = current + 1;
            int lower = upper - dayNum;
            INDArray temp = arr.get(NDArrayIndex.interval(lower, upper), NDArrayIndex.all());
            long[] rowShape = {1, temp.columns()};
            INDArray row = Nd4j.valueArrayOf(rowShape, Double.NaN, DataType.DOUBLE);
            for (int i=0; i<temp.columns(); i++) {
                INDArray column = temp.getColumn(i);
                int countNan = Nd4j.getExecutioner().exec(new MatchCondition(column, Conditions.isNan())).getInt(0);
                if (countNan != 0) {
                    continue;
                }
                int n = column.columns();
                Integer[] sort = ArrayUtils.argSort(column);
                INDArray arange = ArrayUtils.arange((double) n).add(1).div(n);
                row.put(0, i, arange.getDouble(sort[sort.length-1]));
            }
            result.putRow(current, row);
        }
        return result;
    }

    public static INDArray cov(INDArray arr1, INDArray arr2, Integer dayNum) {
        long[] shape = {arr1.rows(), arr1.columns()};
        INDArray result = Nd4j.valueArrayOf(shape, Double.NaN, DataType.DOUBLE);
        for (int current=0; current < arr1.rows(); current++) {
            if (current + 1 < dayNum) {
                continue;
            }

            int upper = current + 1;
            int lower = upper - dayNum;
            INDArray temp1 = arr1.get(NDArrayIndex.interval(lower, upper), NDArrayIndex.all());
            INDArray temp2 = arr2.get(NDArrayIndex.interval(lower, upper), NDArrayIndex.all());
            long[] rowShape = {1, temp1.columns()};
            INDArray row = Nd4j.valueArrayOf(rowShape, Double.NaN, DataType.DOUBLE);
            for (int i=0; i<temp1.columns(); i++) {
                INDArray column1 = temp1.getColumn(i);
                INDArray column2 = temp2.getColumn(i);
                double ans = (double) column1.sub(column1.meanNumber()).mul(column2.sub(column2.meanNumber())).sumNumber();
                row.put(0, i, ans / (column1.columns() - 1));
            }
            result.putRow(current, row);
        }
        return result;
    }

    public static INDArray corr(INDArray arr1, INDArray arr2, Integer dayNum) {
        long[] shape = {arr1.rows(), arr1.columns()};
        INDArray result = Nd4j.valueArrayOf(shape, Double.NaN, DataType.DOUBLE);
        for (int current=0; current < arr1.rows(); current++) {
            if (current + 1 < dayNum) {
                continue;
            }

            int upper = current + 1;
            int lower = upper - dayNum;
            INDArray temp1 = arr1.get(NDArrayIndex.interval(lower, upper), NDArrayIndex.all());
            INDArray temp2 = arr2.get(NDArrayIndex.interval(lower, upper), NDArrayIndex.all());
            long[] rowShape = {1, temp1.columns()};
            INDArray row = Nd4j.valueArrayOf(rowShape, Double.NaN, DataType.DOUBLE);
            for (int i=0; i<temp1.columns(); i++) {
                INDArray column1 = temp1.getColumn(i);
                INDArray column2 = temp2.getColumn(i);
                double ans = (double) column1.sub(column1.meanNumber()).mul(column2.sub(column2.meanNumber())).sumNumber();
                INDArray columnSub1 = column1.sub(column1.meanNumber());
                INDArray columnSub2 = column2.sub(column2.meanNumber());
                double anx = (double) Transforms.pow(columnSub1, 2).sumNumber() * (double) Transforms.pow(columnSub2, 2).sumNumber();
                row.put(0, i, ans / Math.sqrt(anx));
            }
            result.putRow(current, row);
        }
        return result;
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
        return Transforms.log(Transforms.abs(arr.addi(1.0)));
    }

    public static INDArray sqrt(INDArray arr) {
        return Transforms.sqrt(Transforms.abs(arr));
    }

    public static INDArray square(INDArray arr) {
        return Transforms.pow(arr, 2);
    }

    public static INDArray sign(INDArray arr) {
        BooleanIndexing.replaceWhere(arr, 0.0, Conditions.isNan());
        return Transforms.sign(arr);
    }

    public static INDArray exp(INDArray arr) {
        BooleanIndexing.replaceWhere(arr, 0.0, Conditions.isNan());
        INDArray arrNorm = arr.sub(arr.mean(0)).div(arr.std(0).add(1.0));
        return Transforms.exp(arrNorm);
    }

    public static INDArray rank(INDArray arr) {
        long[] shape = {arr.rows(), arr.columns()};
        INDArray result = Nd4j.valueArrayOf(shape, Double.NaN, DataType.DOUBLE);
        for (int current=0; current<arr.rows(); current++) {
            INDArray row = arr.getRow(current);
            result.putRow(current, ArrayUtils.rank(row, false, "first"));
        }
        return result;
    }

    public static INDArray delay(INDArray arr, Integer dayNum) {
        long[] shape = {arr.rows(), arr.columns()};
        INDArray result = Nd4j.valueArrayOf(shape, Double.NaN, DataType.DOUBLE);
        for (int current=0; current < arr.rows(); current++) {
            if (current - dayNum < 0) {
                continue;
            }

            result.putRow(current, arr.getRow(current - dayNum));
        }
        return result;
    }

    public static INDArray delta(INDArray arr, Integer dayNum) {
        long[] shape = {arr.rows(), arr.columns()};
        INDArray result = Nd4j.valueArrayOf(shape, Double.NaN, DataType.DOUBLE);
        for (int current=0; current < arr.rows(); current++) {
            if (current - dayNum < 0) {
                continue;
            }

            result.putRow(current, arr.getRow(current).sub(arr.getRow(current - dayNum)));
        }
        return result;
    }

    public static INDArray decayLinear(INDArray arr, Integer dayNum) {
        long[] shape = {arr.rows(), arr.columns()};
        INDArray result = Nd4j.valueArrayOf(shape, Double.NaN, DataType.DOUBLE);
        for (int current=0; current < arr.rows(); current++) {
            if (current + 1 < dayNum) {
                continue;
            }

            int upper = current + 1;
            int lower = upper - dayNum;
            INDArray temp = arr.get(NDArrayIndex.interval(lower, upper), NDArrayIndex.all());
            long[] rowShape = {1, temp.columns()};
            INDArray row = Nd4j.valueArrayOf(rowShape, Double.NaN, DataType.DOUBLE);
            for (int i=0; i<temp.columns(); i++) {
                INDArray column = temp.getColumn(i);
                int countNan = Nd4j.getExecutioner().exec(new MatchCondition(column, Conditions.isNan())).getInt(0);
                if (countNan != 0) {
                    continue;
                }
                int n = column.columns();
                INDArray decayWeights = ArrayUtils.arange(1.0, (double) (n+1), 1.0);
                decayWeights = decayWeights.div(decayWeights.sum(0));
                row.putColumn(i, column.mul(decayWeights).sum(0));
            }
            result.putRow(current, row);
        }
        return result;
    }

    public static INDArray prod(INDArray arr, Integer dayNum) {
        long[] shape = {arr.rows(), arr.columns()};
        INDArray result = Nd4j.valueArrayOf(shape, Double.NaN, DataType.DOUBLE);
        for (int current=0; current < arr.rows(); current++) {
            if (current + 1 < dayNum) {
                continue;
            }

            int upper = current + 1;
            int lower = upper - dayNum;
            result.putRow(current, arr.get(NDArrayIndex.interval(lower, upper), NDArrayIndex.all()).prod(0));
        }
        return result;
    }

    public static INDArray min(INDArray arr1, INDArray arr2) {
        INDArray arr_delta = arr1.sub(arr2);
        BooleanIndexing.replaceWhere(arr_delta, 0.0, Conditions.lessThan(0.0));
        return arr1.sub(arr_delta);
    }

    public static INDArray max(INDArray arr1, INDArray arr2) {
        INDArray arr_delta = arr1.sub(arr2);
        BooleanIndexing.replaceWhere(arr_delta, 0.0, Conditions.greaterThan(0.0));
        return arr1.sub(arr_delta);
    }

    public static INDArray count(INDArray arr, Integer dayNum) {
        BooleanIndexing.replaceWhere(arr, 0.0, Conditions.isNan());
        INDArray arrCon = Transforms.sign(arr);
        BooleanIndexing.replaceWhere(arrCon, 0.0, Conditions.equals(-1));

        long[] shape = {arrCon.rows(), arrCon.columns()};
        INDArray result = Nd4j.valueArrayOf(shape, Double.NaN, DataType.DOUBLE);
        for (int current=0; current < arrCon.rows(); current++) {
            if (current + 1 < dayNum) {
                continue;
            }

            int upper = current + 1;
            int lower = upper - dayNum;
            result.putRow(current, arrCon.get(NDArrayIndex.interval(lower, upper), NDArrayIndex.all()).sum(0));
        }
        return result;
    }

    public static INDArray sumIf(INDArray arr, Integer dayNum) {
        INDArray arrCopy = arr.dup();
        BooleanIndexing.replaceWhere(arrCopy, 0.0, Conditions.isNan());
        INDArray arrCon = Transforms.sign(arrCopy);
        INDArray arrSelect = arr.mul(arrCon);

        long[] shape = {arrSelect.rows(), arrSelect.columns()};
        INDArray result = Nd4j.valueArrayOf(shape, Double.NaN, DataType.DOUBLE);
        for (int current=0; current < arrSelect.rows(); current++) {
            if (current + 1 < dayNum) {
                continue;
            }

            int upper = current + 1;
            int lower = upper - dayNum;
            result.putRow(current, arrSelect.get(NDArrayIndex.interval(lower, upper), NDArrayIndex.all()).sum(0));
        }
        return result;
    }

    public static INDArray sma(INDArray arr, Integer dayNum1, Integer dayNum2) {
        long[] shape = {arr.rows(), arr.columns()};
        INDArray result = Nd4j.valueArrayOf(shape, Double.NaN, DataType.DOUBLE);
        Integer dayNum = dayNum1 / dayNum2;
        for (int current=0; current < arr.rows(); current++) {
            if (current + 1 < dayNum) {
                continue;
            }

            int upper = current + 1;
            int lower = upper - dayNum;
            result.putRow(current, arr.get(NDArrayIndex.interval(lower, upper), NDArrayIndex.all()).mean(0));
        }
        return result;
    }

    public static INDArray highDay(INDArray arr, Integer dayNum) {
            long[] shape = {arr.rows(), arr.columns()};
            INDArray result = Nd4j.valueArrayOf(shape, Double.NaN, DataType.DOUBLE);
            for (int current=0; current < arr.rows(); current++) {
                if (current + 1 < dayNum) {
                    continue;
                }

                int upper = current + 1;
                int lower = upper - dayNum;
                INDArray temp = arr.get(NDArrayIndex.interval(lower, upper), NDArrayIndex.all());
                long[] rowShape = {1, temp.columns()};
                INDArray row = Nd4j.valueArrayOf(rowShape, Double.NaN, DataType.DOUBLE);
                for (int i=0; i<temp.columns(); i++) {
                    INDArray column = temp.getColumn(i);
                    int countNan = Nd4j.getExecutioner().exec(new MatchCondition(column, Conditions.isNan())).getInt(0);
                    if (countNan != 0) {
                        continue;
                    }
                    INDArray ranks = ArrayUtils.rank(column);
                    row.put(0, i, (double) (ranks.columns() - 1) - ranks.argMax(0).getDouble(0));
                }
                result.putRow(current, row);
            }
            return result;
        }

    public static INDArray lowDay(INDArray arr, Integer dayNum) {
        long[] shape = {arr.rows(), arr.columns()};
        INDArray result = Nd4j.valueArrayOf(shape, Double.NaN, DataType.DOUBLE);
        for (int current=0; current < arr.rows(); current++) {
            if (current + 1 < dayNum) {
                continue;
            }

            int upper = current + 1;
            int lower = upper - dayNum;
            INDArray temp = arr.get(NDArrayIndex.interval(lower, upper), NDArrayIndex.all());
            long[] rowShape = {1, temp.columns()};
            INDArray row = Nd4j.valueArrayOf(rowShape, Double.NaN, DataType.DOUBLE);
            for (int i=0; i<temp.columns(); i++) {
                INDArray column = temp.getColumn(i);
                int countNan = Nd4j.getExecutioner().exec(new MatchCondition(column, Conditions.isNan())).getInt(0);
                if (countNan != 0) {
                    continue;
                }
                INDArray ranks = ArrayUtils.rank(column);
                row.put(0, i, (double) (ranks.columns() - 1) - Nd4j.argMin(ranks, 0).getDouble(0));
            }
            result.putRow(current, row);
        }
        return result;
    }

    public static INDArray ret(INDArray arr, Integer dayNum) {
        long[] shape = {arr.rows(), arr.columns()};
        INDArray result = Nd4j.valueArrayOf(shape, Double.NaN, DataType.DOUBLE);
        for (int current=0; current < arr.rows(); current++) {
            if (current - dayNum < 0) {
                continue;
            }

            // TODO fill_method=None/Pad?
            result.putRow(current, arr.getRow(current).div(arr.getRow(current-dayNum)).sub(1.0));
        }
        return result;
    }

    public static INDArray wma(INDArray arr, Integer dayNum) {
        long[] shape = {arr.rows(), arr.columns()};
        INDArray result = Nd4j.valueArrayOf(shape, Double.NaN, DataType.DOUBLE);
        for (int current=0; current < arr.rows(); current++) {
            if (current + 1 < dayNum) {
                continue;
            }

            int upper = current + 1;
            int lower = upper - dayNum;
            INDArray temp = arr.get(NDArrayIndex.interval(lower, upper), NDArrayIndex.all());
            long[] rowShape = {1, temp.columns()};
            INDArray row = Nd4j.valueArrayOf(rowShape, Double.NaN, DataType.DOUBLE);
            for (int i=0; i<temp.columns(); i++) {
                INDArray column = temp.getColumn(i);
                int countNan = Nd4j.getExecutioner().exec(new MatchCondition(column, Conditions.isNan())).getInt(0);
                if (countNan != 0) {
                    continue;
                }
                long[] baseShape = {column.rows(), column.columns()};
                INDArray base = Nd4j.valueArrayOf(baseShape, 0.9, DataType.DOUBLE);
                INDArray pow = Transforms.reverse(ArrayUtils.arange(1.0, (double) (column.columns()+1), 1.0), false);
                row.putColumn(i, Transforms.dot(column, Transforms.pow(base, pow)));
            }
            result.putRow(current, row);
        }
        return result;
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
//        INDArray arr = Nd4j.create(new double[][]{{Double.NaN,-2.0,3.0},{4.0,Double.NaN,-6.0},{4.0,-8.0,Double.NaN},{12.0,12.0,25.0}});
//        INDArray arr1 = Nd4j.create(new double[][]{{4.0,-2.0,33.0},{24.0,2.0,-6.0},{-7.0,18.0,25.0},{12.0,58.0,32.0}});
//        System.out.println(arr);
//        System.out.println();
//        System.out.println(arr1);
//        System.out.println();
//        System.out.println(sum(arr, arr1));
//        System.out.println(sub(arr, arr1));
//        System.out.println(mul(arr, arr1));
//        System.out.println(div(arr, arr1));
//        System.out.println(tsSum(arr, 2));
//        System.out.println(std(arr, 2));
//        System.out.println(mean(arr, 2));
//        System.out.println(tsMin(arr, 2));
//        System.out.println(llv(arr, 2));
//        System.out.println(tsMax(arr, 2));
//        System.out.println(hhv(arr, 2));
//        System.out.println(tsRank(arr, 2));
//        System.out.println(cov(arr, arr1, 2));
//        System.out.println(corr(arr, arr1, 2));
//        System.out.println(abs(arr));
//        System.out.println(relu(arr));
//        System.out.println(sigmoid(arr));
//        System.out.println(log(arr));
//        System.out.println(sqrt(arr));
//        System.out.println(square(arr));
//        System.out.println(sign(arr));
//        System.out.println(exp(arr));
//        System.out.println(rank(arr));
//        System.out.println(delay(arr, 2));
//        System.out.println(delta(arr, 2));
//        System.out.println(decayLinear(arr, 2));
//        System.out.println(prod(arr, 2));
//        System.out.println(min(arr, arr1));
//        System.out.println(max(arr, arr1));
//        System.out.println(count(arr, 2));
//        System.out.println(sumIf(arr, 2));
//        System.out.println(sma(arr, 4, 3));
//        System.out.println(highDay(arr, 2));
//        System.out.println(lowDay(arr, 2));
//        System.out.println(ret(arr, 2));
//        System.out.println(wma(arr, 2));
//        System.out.println(regBeta(arr, arr1, 2));
//        System.out.println(regResi(arr, arr1, 2));
//    }

}
