package com.range.stcfactor.common.utils;

import com.range.stcfactor.common.helper.RangeTransform;
import com.range.stcfactor.common.helper.RankAssist;
import com.range.stcfactor.common.helper.Rolling2Array;
import com.range.stcfactor.common.helper.Rolling2Double;
import com.range.stcfactor.common.helper.RollingArray;
import com.range.stcfactor.common.helper.RollingDouble;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.nd4j.linalg.api.buffer.DataType;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.api.ops.impl.reduce.longer.MatchCondition;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.indexing.BooleanIndexing;
import org.nd4j.linalg.indexing.NDArrayIndex;
import org.nd4j.linalg.indexing.conditions.Conditions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author zrj5865@163.com
 * @create 2019-11-20
 */
public class ArrayUtils {

    private static final Logger logger = LogManager.getLogger(ArrayUtils.class);

    public static INDArray rolling(final INDArray array, Integer window, RollingArray transform) {
        long[] shape = {array.rows(), array.columns()};
        INDArray result = Nd4j.valueArrayOf(shape, Double.NaN, DataType.DOUBLE);
        for (int current=0; current < array.rows(); current++) {
            if (current + 1 < window) {
                continue;
            }

            int upper = current + 1;
            int lower = upper - window;
            INDArray temp = array.get(NDArrayIndex.interval(lower, upper), NDArrayIndex.all());

            result.putRow(current, transform.apply(temp));
        }
        return result;
    }

    public static INDArray rolling(final INDArray array, Integer window, Rolling2Array transform) {
        long[] shape = {array.rows(), array.columns()};
        INDArray result = Nd4j.valueArrayOf(shape, Double.NaN, DataType.DOUBLE);
        for (int current=0; current < array.rows(); current++) {
            if (current - window < 0) {
                continue;
            }

            INDArray temp1 = array.getRow(current);
            INDArray temp2 = array.getRow(current - window);

            result.putRow(current, transform.apply(temp1, temp2));
        }
        return result;
    }

    public static INDArray rolling(final INDArray array, Integer window, RollingDouble transform) {
        int rows = array.rows();
        int columns = array.columns();
        double[][] result = new double[rows][columns];

        double[][] matrix = array.toDoubleMatrix();
        for (int row=0; row < matrix.length; row++) {
            if (row + 1 < window) {
                for (int i=0; i<columns; i++) {
                    result[row][i] = Double.NaN;
                }
                continue;
            }

            for (int column=0; column<columns; column++) {
                boolean hasNan = false;
                int last = row + 1 - window;
                double[] columnTemp = new double[window];
                for (int j=0; j<columnTemp.length; j++,last++) {
                    double num = matrix[last][column];
                    if (Double.isNaN(num)) {
                        hasNan = true;
                        break;
                    }
                    columnTemp[j] = num;
                }
                if (hasNan) {
                    result[row][column] = Double.NaN;
                } else {
                    result[row][column] = transform.apply(columnTemp);
                }
            }
        }
        return Nd4j.create(result);
    }

    public static INDArray rolling(final INDArray array1, final INDArray array2, Integer window, Rolling2Double transform) {
        int rows = array1.rows();
        int columns = array1.columns();
        double[][] result = new double[rows][columns];

        double[][] matrix1 = array1.toDoubleMatrix();
        double[][] matrix2 = array2.toDoubleMatrix();
        for (int row=0; row < matrix1.length; row++) {
            if (row + 1 < window) {
                for (int i=0; i<columns; i++) {
                    result[row][i] = Double.NaN;
                }
                continue;
            }

            for (int column=0; column<columns; column++) {
                double[] column1 = new double[window];
                double[] column2 = new double[window];
                int last = row + 1 - window;
                for (int j=0; j<column1.length; j++,last++) {
                    column1[j] = matrix1[last][column];
                    column2[j] = matrix2[last][column];
                }
                result[row][column] = transform.apply(column1, column2);
            }
        }
        return Nd4j.create(result);
    }

    private static boolean hasNan(final INDArray array) {
        boolean hasNan = false;
        for (int i=0; i<array.rows(); i++) {
            INDArray row = array.getRow(i);
            for (int j=0; j<row.columns(); j++) {
                if (Double.isNaN(array.getDouble(i))) {
                    hasNan = true;
                    break;
                }
            }

            if (hasNan) {
                break;
            }
        }
        return hasNan;
    }

    public static boolean isAllNan(final INDArray array) {
        int countNan = Nd4j.getExecutioner().exec(new MatchCondition(array, Conditions.isNan())).getInt(0);
        return countNan == array.rows() * array.columns();
    }

    public static INDArray replaceNan(final INDArray array, Double set) {
        INDArray arrayCopy = array.dup();
        BooleanIndexing.replaceWhere(arrayCopy, set, Conditions.isNan());
        return arrayCopy;
    }

    public static INDArray replaceEquals(final INDArray array, Double equal, Double set) {
        INDArray arrayCopy = array.dup();
        BooleanIndexing.replaceWhere(arrayCopy, set, Conditions.equals(equal));
        return arrayCopy;
    }

    public static INDArray replaceLess(final INDArray array, Double less, Double set) {
        INDArray arrayCopy = array.dup();
        BooleanIndexing.replaceWhere(arrayCopy, set, Conditions.lessThan(less));
        return arrayCopy;
    }

    public static INDArray replaceGreater(final INDArray array, Double greater, Double set) {
        INDArray arrayCopy = array.dup();
        BooleanIndexing.replaceWhere(arrayCopy, set, Conditions.greaterThan(greater));
        return arrayCopy;
    }

    public static int getNanCut(final INDArray array) {
        int cut = 0;
        for (int i=0; i<array.rows(); i++) {
            if (ArrayUtils.isAllNan(array.getRow(i))) {
                continue;
            }
            cut = i;
            break;
        }
        return cut;
    }

    public static Integer[] argSort(final INDArray array) {
        return argSort(array.toDoubleVector(), true);
    }

    public static Integer[] argSort(final INDArray array, final boolean ascending) {
        return argSort(array.toDoubleVector(), ascending);
    }

    public static Integer[] argSort(final double[] array) {
        return argSort(array, true);
    }

    public static Integer[] argSort(final double[] array, final boolean ascending) {
        Integer[] indexes = new Integer[array.length];
        for (int i = 0; i < indexes.length; i++) {
            indexes[i] = i;
        }
        Arrays.sort(indexes, (i1, i2) -> (ascending ? 1 : -1) * Double.compare(array[i1], array[i2]));
        return indexes;
    }

    public static INDArray shift(final INDArray array, Integer num) {
        long[] shape = {array.rows(), array.columns()};
        INDArray result = Nd4j.valueArrayOf(shape, Double.NaN, DataType.DOUBLE);
        for (int current=0; current < array.rows(); current++) {
            if (num >= 0) {
                if (current - num < 0) {
                    continue;
                }
            } else {
                if (current - num >= array.rows()) {
                    continue;
                }
            }

            result.putRow(current, array.getRow(current - num));
        }
        return result;
    }

    public static List<Double> range(double endExclusive) {
        return range(0.0, endExclusive, 1.0);
    }

    public static List<Double> range(double startInclusive, double endExclusive, double step) {
        return range(startInclusive, endExclusive, step, num -> num);
    }

    public static List<Double> range(double startInclusive, double endExclusive, double step, RangeTransform transform) {
        List<Double> range = new ArrayList<>();
        IntStream.range(0, (int) ((endExclusive-startInclusive)/step))
                .mapToDouble(x -> transform.apply(x * step + startInclusive)).forEach(range::add);
        Nd4j.setDefaultDataTypes(DataType.DOUBLE, DataType.DOUBLE);
        return range;
    }

    public static List<Double> rangeClosed(double endInclusive) {
        return rangeClosed(0.0, endInclusive, 1.0);
    }

    public static List<Double> rangeClosed(double startInclusive, double endExclusive, double step) {
        return rangeClosed(startInclusive, endExclusive, step, num -> num);
    }

    public static List<Double> rangeClosed(double startInclusive, double endInclusive, double step, RangeTransform transform) {
        List<Double> range = new ArrayList<>();
        IntStream.rangeClosed(0, (int) ((endInclusive-startInclusive)/step))
                .mapToDouble(x -> transform.apply(x * step + startInclusive)).forEach(range::add);
        Nd4j.setDefaultDataTypes(DataType.DOUBLE, DataType.DOUBLE);
        return range;
    }

    public static Double cov(final INDArray array1, final INDArray array2) {
//        double ans = (double) array1.sub(array1.meanNumber()).mul(array2.sub(array2.meanNumber())).sumNumber();
//        return ans / (array1.columns() - 1);
        return cov(array1.toDoubleVector(), array2.toDoubleVector());
    }

    public static Double cov(double[] array1, double[] array2) {
        double mean1 = mean(array1);
        double mean2 = mean(array2);
        double ans = 0.0;
        for (int i=0; i<array1.length; i++) {
            ans += (array1[i] - mean1) * (array2[i] - mean2);
        }
        return ans / (array1.length - 1);
    }

    public static Double corr(final INDArray array1, final INDArray array2) {
//        double ans = (double) array1.sub(array1.meanNumber()).mul(array2.sub(array2.meanNumber())).sumNumber();
//        INDArray sub1 = array1.sub(array1.meanNumber());
//        INDArray sub2 = array2.sub(array2.meanNumber());
//        double anx = (double) Transforms.pow(sub1, 2).sumNumber() * (double) Transforms.pow(sub2, 2).sumNumber();
//        return ans / Math.sqrt(anx);
        return corr(array1.toDoubleVector(), array2.toDoubleVector());
    }

    public static Double corr(final double[] array1, final double[] array2) {
        double mean1 = ArrayUtils.mean(array1);
        double mean2 = ArrayUtils.mean(array2);
        double ans = 0.0;
        double anx1 = 0.0;
        double anx2 = 0.0;
        for (int j=0; j<array1.length; j++) {
            double sub1 = array1[j] - mean1;
            double sub2 = array2[j] - mean2;
            ans += sub1 * sub2;
            anx1 += Math.pow(sub1, 2);
            anx2 += Math.pow(sub2, 2);
        }
        return ans / Math.sqrt(anx1 * anx2);
    }

    private static double mean(final double[] array) {
        double sum = 0.0;
        for (double v : array) {
            sum += v;
        }
        return sum / array.length;
    }

    public static double meanWithoutNan(final INDArray array) {
        return meanWithoutNan(array.toDoubleMatrix());
    }

    public static double meanWithoutNan(final double[][] array) {
        double sum = 0.0;
        long count = 0;
        for (double[] row : array) {
            for (double col : row) {
                if (isInvalid(col)) {
                    continue;
                }
                sum += col;
                count++;
            }
        }
        return sum / count;
    }

    public static double stdWithoutNan(final INDArray array) {
        return stdWithoutNan(array.toDoubleMatrix());
    }

    public static double stdWithoutNan(final double[][] array) {
        final double mean = meanWithoutNan(array);
        double sum = 0.0;
        long count = 0;
        for (double[] row : array) {
            for (double col : row) {
                if (isInvalid(col)) {
                    continue;
                }
                sum += Math.pow(col - mean, 2);
                count++;
            }
        }
        return Math.sqrt(sum / count);
    }

    public static double kurtosisWithoutNan(final INDArray array) {
        return kurtosisWithoutNan(array.toDoubleMatrix());
    }

    public static double kurtosisWithoutNan(final double[][] array) {
        final double mean = meanWithoutNan(array);
        double numerator = 0.0;
        double denominator = 0.0;
        long count = 0;
        for (double[] row : array) {
            for (double col : row) {
                if (isInvalid(col)) {
                    continue;
                }
                numerator += Math.pow(col - mean, 4);
                denominator += Math.pow(col - mean, 2);
                count++;
            }
        }
        return (numerator / count) / Math.pow(denominator / count, 2) - 3;
    }

    public static boolean isValid(double number) {
        return !Double.isNaN(number) && Double.isFinite(number);
    }

    public static boolean isInvalid(double number) {
        return Double.isNaN(number) || Double.isInfinite(number);
    }

    public static INDArray rank(final INDArray arr) {
        return rank(arr, true);
    }

    public static INDArray rank(final INDArray arr, boolean isAscending) {
        return rank(arr, isAscending, "average", false);
    }

    /**
     * 秩
     * @param array         数组
     * @param isAscending 是否升序
     * @param method      average/first/min/max
     * @return 秩数组
     */
    public static INDArray rank(final INDArray array, boolean isAscending, String method, boolean isPct) {
        long[] shape = {array.rows(), array.columns()};
        INDArray result = Nd4j.valueArrayOf(shape, Double.NaN, DataType.DOUBLE);
        INDArray row;
        for (int current=0; current<array.rows(); current++) {
            row = array.getRow(current);

            // 区分NAN值
            List<RankAssist> assists = new ArrayList<>();
            List<RankAssist> nanAssists = new ArrayList<>();
            for (int index=0; index<row.columns(); index++) {
                Double data = row.getDouble(index);
                if (Double.isNaN(data)) {
                    nanAssists.add(new RankAssist(data, index+1, Double.NaN));
                } else {
                    assists.add(new RankAssist(data, index+1, null));
                }
            }

            // 根据data排序
            if (isAscending) {
                assists.sort(Comparator.comparing(RankAssist::getData));
            } else {
                assists.sort(Comparator.comparing(RankAssist::getData).reversed());
            }

            // 根据method求秩
            List<RankAssist> temp = new ArrayList<>();
            for (int i=0; i<=assists.size(); i++) {
                RankAssist assist = null;
                if (i != assists.size()) {
                    assist = assists.get(i);
                    assist.setNewIndex((double) i + 1);
                }
                if ("first".equalsIgnoreCase(method)) {
                    continue;
                }

                // method处理
                if (temp.size() > 0 && (assist == null || !assist.getData().equals(temp.get(temp.size()-1).getData()))) {
                    if (temp.size() == 1) {
                        temp.clear();
                        temp.add(assist);
                        continue;
                    }

                    INDArray ranks = Nd4j.create(temp.stream().map(RankAssist::getNewIndex).collect(Collectors.toList()));
                    Double rank;
                    switch (method) {
                        case "average":
                            rank = (Double) ranks.meanNumber();
                            break;
                        case "max":
                            rank = (Double) ranks.maxNumber();
                            break;
                        case "min":
                            rank = (Double) ranks.minNumber();
                            break;
                        default:
                            throw new RuntimeException("Error method: " + method);
                    }
                    temp.forEach(a -> a.setNewIndex(rank));
                } else {
                    temp.add(assist);
                }
            }

            // 整合nan后根据前置index排序
            int notNanSize = assists.size();
            assists.addAll(nanAssists);
            assists.sort(Comparator.comparing(RankAssist::getOldIndex));
            INDArray rank = Nd4j.create(assists.stream().map(RankAssist::getNewIndex).collect(Collectors.toList()));
            if (isPct) {
                result.putRow(current, rank.mul(1.0 / notNanSize));
            } else {
                result.putRow(current, rank);
            }
        }
        return result;
    }

}
