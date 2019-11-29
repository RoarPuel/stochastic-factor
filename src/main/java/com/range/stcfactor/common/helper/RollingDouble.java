package com.range.stcfactor.common.helper;

import org.nd4j.linalg.api.ndarray.INDArray;

/**
 * @author renjie.zhu@woqutech.com
 * @create 2019-11-28
 */
public interface RollingDouble {

    Double apply(INDArray array);

}
