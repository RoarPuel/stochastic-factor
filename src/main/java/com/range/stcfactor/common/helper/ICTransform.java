package com.range.stcfactor.common.helper;

import org.nd4j.linalg.api.ndarray.INDArray;

/**
 * @author zrj5865@163.com
 * @create 2019-12-03
 */
public interface ICTransform {

    double apply(INDArray array1, INDArray array2);

}
