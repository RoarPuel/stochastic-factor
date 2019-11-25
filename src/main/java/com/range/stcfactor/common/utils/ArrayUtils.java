package com.range.stcfactor.common.utils;

import org.nd4j.linalg.api.buffer.DataType;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.indexing.NDArrayIndex;
import org.nd4j.linalg.ops.transforms.Transforms;

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

    public static INDArray range(int endExclusive) {
        return range(0, endExclusive, 1);
    }

    public static INDArray range(int startInclusive, int endExclusive, int step) {
        List<Integer> range = new ArrayList<>();
        IntStream.range(0, (endExclusive-startInclusive)/step)
                .map(x -> x*step + startInclusive).forEach(range::add);
        Nd4j.setDefaultDataTypes(DataType.DOUBLE, DataType.DOUBLE);
        return Nd4j.create(range);
    }

    public static INDArray rangeClosed(int endInclusive) {
        return rangeClosed(0, endInclusive, 1);
    }

    public static INDArray rangeClosed(int startInclusive, int endInclusive, int step) {
        List<Integer> range = new ArrayList<>();
        IntStream.rangeClosed(0, (endInclusive-startInclusive)/step)
                .map(x -> x*step + startInclusive).forEach(range::add);
        Nd4j.setDefaultDataTypes(DataType.DOUBLE, DataType.DOUBLE);
        return Nd4j.create(range);
    }

    public static INDArray arange(double endExclusive) {
        return arange(0.0, endExclusive, 1.0);
    }

    public static INDArray arange(double startInclusive, double endExclusive, double step) {
        List<Double> arange = new ArrayList<>();
        IntStream.range(0, (int) ((endExclusive-startInclusive)/step))
                .mapToDouble(x -> x*step + startInclusive).forEach(arange::add);
        Nd4j.setDefaultDataTypes(DataType.DOUBLE, DataType.DOUBLE);
        return Nd4j.create(arange);
    }

    public static INDArray arangeClosed(double endInclusive) {
        return arangeClosed(0.0, endInclusive, 1.0);
    }

    public static INDArray arangeClosed(double startInclusive, double endInclusive, double step) {
        List<Double> arange = new ArrayList<>();
        IntStream.rangeClosed(0, (int) ((endInclusive-startInclusive)/step))
                .mapToDouble(x -> x*step + startInclusive).forEach(arange::add);
        Nd4j.setDefaultDataTypes(DataType.DOUBLE, DataType.DOUBLE);
        return Nd4j.create(arange);
    }

    public static INDArray rank(INDArray arr) {
        return rank(arr, true);
    }

    public static INDArray rank(INDArray arr, boolean isAscending) {
        return rank(arr, isAscending, "average");
    }

    /**
     * 秩
     * @param arr         数组
     * @param isAscending 是否升序
     * @param method      average/first/min/max
     * @return 秩数组
     */
    public static INDArray rank(INDArray arr, boolean isAscending, String method) {
        // 区分NAN值
        List<RankAssist> assists = new ArrayList<>();
        List<RankAssist> nanAssists = new ArrayList<>();
        for (int index=0; index<arr.columns(); index++) {
            Double data = arr.getDouble(index);
            if (Double.isNaN(data)) {
                nanAssists.add(new RankAssist(data, index+1, Double.NaN));
            } else {
                assists.add(new RankAssist(data, index+1, null));
            }
        }

        // 倒序根据method求秩
        if (isAscending) {
            assists.sort(Comparator.comparing(RankAssist::getData));
        } else {
            assists.sort(Comparator.comparing(RankAssist::getData).reversed());
        }
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

        // 整合排序获取秩
        assists.addAll(nanAssists);
        assists.sort(Comparator.comparing(RankAssist::getOldIndex));
        return Nd4j.create(assists.stream().map(RankAssist::getNewIndex).collect(Collectors.toList()));
    }

}
