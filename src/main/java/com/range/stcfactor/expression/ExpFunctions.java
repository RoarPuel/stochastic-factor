package com.range.stcfactor.expression;

import com.range.stcfactor.common.RankAssist;
import org.nd4j.linalg.api.buffer.DataType;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.indexing.BooleanIndexing;
import org.nd4j.linalg.indexing.NDArrayIndex;
import org.nd4j.linalg.indexing.conditions.Conditions;
import org.nd4j.linalg.ops.transforms.Transforms;

import java.util.ArrayList;
import java.util.Comparator;
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
        INDArray result = Nd4j.create(DataType.DOUBLE, arr.rows(), arr.columns());
        for (int current=0; current < arr.rows(); current++) {
            int upper = current + 1;
            int lower = upper < dayNum ? 0 : upper - dayNum;
            result.putRow(current, arr.get(NDArrayIndex.interval(lower, upper), NDArrayIndex.all()).sum(0));
        }
        return result;
    }

    public static INDArray std(INDArray arr, Integer dayNum) {
        INDArray result = Nd4j.create(DataType.DOUBLE, arr.rows(), arr.columns());
        for (int current=0; current < arr.rows(); current++) {
            int upper = current + 1;
            int lower = upper < dayNum ? 0 : upper - dayNum;
            result.putRow(current, arr.get(NDArrayIndex.interval(lower, upper), NDArrayIndex.all()).std(0));
        }
        return result;
    }

    public static INDArray mean(INDArray arr, Integer dayNum) {
        INDArray result = Nd4j.create(DataType.DOUBLE, arr.rows(), arr.columns());
        for (int current=0; current < arr.rows(); current++) {
            int upper = current + 1;
            int lower = upper < dayNum ? 0 : upper - dayNum;
            result.putRow(current, arr.get(NDArrayIndex.interval(lower, upper), NDArrayIndex.all()).mean(0));
        }
        return result;
    }

    public static INDArray tsMin(INDArray arr, Integer dayNum) {
        INDArray result = Nd4j.create(DataType.DOUBLE, arr.rows(), arr.columns());
        for (int current=0; current < arr.rows(); current++) {
            int upper = current + 1;
            int lower = upper < dayNum ? 0 : upper - dayNum;
            result.putRow(current, arr.get(NDArrayIndex.interval(lower, upper), NDArrayIndex.all()).min(0));
        }
        return result;
    }

    public static INDArray tsMax(INDArray arr, Integer dayNum) {
        INDArray result = Nd4j.create(DataType.DOUBLE, arr.rows(), arr.columns());
        for (int current=0; current < arr.rows(); current++) {
            int upper = current + 1;
            int lower = upper < dayNum ? 0 : upper - dayNum;
            result.putRow(current, arr.get(NDArrayIndex.interval(lower, upper), NDArrayIndex.all()).max(0));
        }
        return result;
    }

    public static INDArray relu(INDArray arr) {
        return Transforms.relu(arr);
    }

    public static INDArray abs(INDArray arr) {
        return Transforms.abs(arr);
    }

    public static INDArray log(INDArray arr) {
        return Transforms.log(Transforms.abs(arr.addi(1.0)));
    }

    public static INDArray sign(INDArray arr) {
        BooleanIndexing.replaceWhere(arr, 0.0, Conditions.isNan());
        return Transforms.sign(arr);
    }

    public static INDArray rank(INDArray arr) {
        INDArray result = Nd4j.create(DataType.DOUBLE, arr.rows(), arr.columns());
        for (int current=0; current<arr.rows(); current++) {
            List<RankAssist> assists = new ArrayList<>();
            List<RankAssist> nanAssists = new ArrayList<>();
            INDArray row = arr.getRow(current);
            for (int index=0; index<row.columns(); index++) {
                Double data = row.getDouble(index);
                if (Double.isNaN(data)) {
                    nanAssists.add(new RankAssist(data, index+1, Double.NaN));
                } else {
                    assists.add(new RankAssist(data, index+1, null));
                }
            }
            // 倒序
            assists.sort(Comparator.comparing(RankAssist::getData).reversed());
            for (int i=0; i<assists.size(); i++) {
                assists.get(i).setNewIndex((double) i+1);
            }
            assists.addAll(nanAssists);
            assists.sort(Comparator.comparing(RankAssist::getOldIndex));
            List<Double> indexes = new ArrayList<>();
            for (RankAssist assist : assists) {
                indexes.add(assist.getNewIndex());
            }
            result.putRow(current, Nd4j.create(indexes));
        }
        return result;
    }

    public static INDArray delay(INDArray arr, Integer dayNum) {
        long[] shape = {arr.rows(), arr.columns()};
        INDArray result = Nd4j.valueArrayOf(shape, Double.NaN, DataType.DOUBLE);
        int target;
        for (int current=0; current < arr.rows(); current++) {
            target = current + dayNum;
            if (target >= arr.rows()) {
                continue;
            }
            result.putRow(target, arr.getRow(current));
        }
        return result;
    }

    public static INDArray delta(INDArray arr, Integer dayNum) {
        long[] shape = {arr.rows(), arr.columns()};
        INDArray result = Nd4j.valueArrayOf(shape, Double.NaN, DataType.DOUBLE);
        int target;
        for (int current=0; current < arr.rows(); current++) {
            target = current - dayNum;
            if (target < 0) {
                continue;
            }
            result.putRow(current, arr.getRow(current).sub(arr.getRow(target)));
        }
        return result;
    }

//    public static void main(String[] args) {
//        INDArray arr1 = Nd4j.create(new double[][]{{Double.NaN,-2.0,3.0},{4.0,Double.NaN,-6.0},{-7.0,8.0,Double.NaN}});
//        INDArray arr2 = Nd4j.create(new double[][]{{2.0,3.0,4.0},{5.0,6.0,7.0},{8.0,9.0,10.0}});
//        System.out.println(sum(arr1, arr2));
//        System.out.println(sub(arr1, arr2));
//        System.out.println(mul(arr1, arr2));
//        System.out.println(div(arr1, arr2));
//        System.out.println(tsSum(arr1, 2));
//        System.out.println(std(arr1, 2));
//        System.out.println(mean(arr1, 2));
//        System.out.println(tsMin(arr1, 2));
//        System.out.println(tsMax(arr1, 2));
//        System.out.println(relu(arr1));
//        System.out.println(abs(arr1));
//        System.out.println(log(arr1));
//        System.out.println(sign(arr1));
//        System.out.println(rank(arr2));
//        System.out.println(delay(arr1, 1));
//        System.out.println(delta(arr1, 2));
//    }

}
