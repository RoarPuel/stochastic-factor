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

    public static INDArray rolling(INDArray array, Integer window, RollingArray transform) {
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

    public static INDArray rolling(INDArray array, Integer window, Rolling2Array transform) {
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

    public static INDArray rolling(INDArray array, Integer window, RollingDouble transform) {
        long[] shape = {array.rows(), array.columns()};
        INDArray result = Nd4j.valueArrayOf(shape, Double.NaN, DataType.DOUBLE);
        for (int current=0; current < array.rows(); current++) {
            if (current + 1 < window) {
                continue;
            }

            int upper = current + 1;
            int lower = upper - window;
            INDArray temp = array.get(NDArrayIndex.interval(lower, upper), NDArrayIndex.all());

            long[] rowShape = {1, temp.columns()};
            INDArray row = Nd4j.valueArrayOf(rowShape, Double.NaN, DataType.DOUBLE);
            for (int i=0; i<temp.columns(); i++) {
                INDArray columnTemp = temp.getColumn(i);
                if (hasNan(columnTemp)) {
                    continue;
                }

                row.put(0, i, transform.apply(columnTemp));
            }
            result.putRow(current, row);
        }
        return result;
    }

    public static INDArray rolling(INDArray array1, INDArray array2, Integer window, Rolling2Double transform) {
        long[] shape = {array1.rows(), array1.columns()};
        INDArray result = Nd4j.valueArrayOf(shape, Double.NaN, DataType.DOUBLE);
        for (int current=0; current < array1.rows(); current++) {
            if (current + 1 < window) {
                continue;
            }

            int upper = current + 1;
            int lower = upper - window;
            INDArray temp1 = array1.get(NDArrayIndex.interval(lower, upper), NDArrayIndex.all());
            INDArray temp2 = array2.get(NDArrayIndex.interval(lower, upper), NDArrayIndex.all());

            long[] rowShape = {1, temp1.columns()};
            INDArray row = Nd4j.valueArrayOf(rowShape, Double.NaN, DataType.DOUBLE);
            for (int i=0; i<temp1.columns(); i++) {
                row.put(0, i, transform.apply(temp1.getColumn(i), temp2.getColumn(i)));
            }
            result.putRow(current, row);
        }
        return result;
    }

    private static boolean hasNan(INDArray array) {
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

    public static boolean isAllNan(INDArray array) {
        int countNan = Nd4j.getExecutioner().exec(new MatchCondition(array, Conditions.isNan())).getInt(0);
        return countNan == array.rows() * array.columns();
    }

    public static INDArray replaceNan(INDArray array, Double set) {
        INDArray arrayCopy = array.dup();
        BooleanIndexing.replaceWhere(arrayCopy, set, Conditions.isNan());
        return arrayCopy;
    }

    public static INDArray replaceEquals(INDArray array, Double equal, Double set) {
        INDArray arrayCopy = array.dup();
        BooleanIndexing.replaceWhere(arrayCopy, set, Conditions.equals(equal));
        return arrayCopy;
    }

    public static INDArray replaceLess(INDArray array, Double less, Double set) {
        INDArray arrayCopy = array.dup();
        BooleanIndexing.replaceWhere(arrayCopy, set, Conditions.lessThan(less));
        return arrayCopy;
    }

    public static INDArray replaceGreater(INDArray array, Double greater, Double set) {
        INDArray arrayCopy = array.dup();
        BooleanIndexing.replaceWhere(arrayCopy, set, Conditions.greaterThan(greater));
        return arrayCopy;
    }

    public static Integer[] argSort(final INDArray array) {
        return argSort(array, true);
    }

    public static Integer[] argSort(final INDArray array, final boolean ascending) {
        Integer[] indexes = new Integer[array.columns()];
        for (int i = 0; i < indexes.length; i++) {
            indexes[i] = i;
        }
        Arrays.sort(indexes, (i1, i2) -> (ascending ? 1 : -1) * Double.compare(array.getDouble(i1), array.getDouble(i2)));
        return indexes;
    }

    public static INDArray shift(INDArray array, Integer num) {
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

    public static Double min(INDArray array) {
        double min = Double.NaN;
        for (int i=0; i<array.columns(); i++) {
            double current = array.getDouble(i);
            if (Double.isNaN(min) || current < min) {
                min = current;
            }
        }
        return min;
    }

    public static Double max(INDArray array) {
        double max = Double.NaN;
        for (int i=0; i<array.columns(); i++) {
            double current = array.getDouble(i);
            if (Double.isNaN(max) || current > max) {
                max = current;
            }
        }
        return max;
    }

    public static Double argMin(INDArray array) {
        double min = Double.NaN;
        int index = 0;
        for (int i=0; i<array.columns(); i++) {
            double current = array.getDouble(i);
            if (Double.isNaN(min) || current < min) {
                min = current;
                index = i;
            }
        }
        return (double) index;
    }

    public static Double argMax(INDArray array) {
        double max = Double.NaN;
        int index = 0;
        for (int i=0; i<array.columns(); i++) {
            double current = array.getDouble(i);
            if (Double.isNaN(max) || current > max) {
                max = current;
                index = i;
            }
        }
        return (double) index;
    }

    public static Double cov(INDArray array1, INDArray array2) {
//        double ans = (double) array1.sub(array1.meanNumber()).mul(array2.sub(array2.meanNumber())).sumNumber();
//        return ans / (array1.columns() - 1);
        double mean1 = mean(array1);
        double mean2 = mean(array2);
        double ans = 0.0;
        for (int i=0; i<array1.columns(); i++) {
            ans += (array1.getDouble(i) - mean1) * (array2.getDouble(i) - mean2);
        }
        return ans / (array1.columns() - 1);
    }

    public static Double corr(INDArray array1, INDArray array2) {
//        double ans = (double) array1.sub(array1.meanNumber()).mul(array2.sub(array2.meanNumber())).sumNumber();
//        INDArray sub1 = array1.sub(array1.meanNumber());
//        INDArray sub2 = array2.sub(array2.meanNumber());
//        double anx = (double) Transforms.pow(sub1, 2).sumNumber() * (double) Transforms.pow(sub2, 2).sumNumber();
//        return ans / Math.sqrt(anx);
        double mean1 = mean(array1);
        double mean2 = mean(array2);
        double ans = 0.0;
        double anx1 = 0.0;
        double anx2 = 0.0;
        for (int i=0; i<array1.columns(); i++) {
            double sub1 = array1.getDouble(i) - mean1;
            double sub2 = array2.getDouble(i) - mean2;
            ans += sub1 * sub2;
            anx1 += Math.pow(sub1, 2);
            anx2 += Math.pow(sub2, 2);
        }
        return ans / Math.sqrt(anx1 * anx2);
    }

    private static double mean(INDArray array) {
        double sum = 0.0;
        for (int i=0; i<array.columns(); i++) {
            sum += array.getDouble(i);
        }
        return sum / array.columns();
    }

    public static INDArray rank(INDArray arr) {
        return rank(arr, true);
    }

    public static INDArray rank(INDArray arr, boolean isAscending) {
        return rank(arr, isAscending, "average", false);
    }

    /**
     * 秩
     * @param array         数组
     * @param isAscending 是否升序
     * @param method      average/first/min/max
     * @return 秩数组
     */
    public static INDArray rank(INDArray array, boolean isAscending, String method, boolean isPct) {
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
