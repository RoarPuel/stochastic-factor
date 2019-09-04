package com.range.stcfactor.expression;

import org.nd4j.linalg.api.buffer.DataType;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.indexing.NDArrayIndex;

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
        INDArray temp;
        for (int current=0; current < arr.rows(); current++) {
            int upper = current + 1;
            int lower = upper < dayNum ? 0 : upper - dayNum;
            temp = arr.get(NDArrayIndex.interval(lower, upper), NDArrayIndex.all()).sum(0);
            result.putRow(current, temp);
        }
        return result;
    }

//    public static void main(String[] args) {
////        INDArray arr1 = Nd4j.create(new double[][]{{1.0,2.0,3.0},{4.0,5.0,6.0},{7.0,8.0,9.0}});
////        INDArray arr2 = Nd4j.create(new double[][]{{2.0,3.0,4.0},{5.0,6.0,7.0},{8.0,9.0,10.0}});
////        System.out.println(sum(arr1, arr2));
////        System.out.println(sub(arr1, arr2));
////        System.out.println(mul(arr1, arr2));
////        System.out.println(div(arr1, arr2));
////        System.out.println(sum(arr1, 2));
//    }

}
