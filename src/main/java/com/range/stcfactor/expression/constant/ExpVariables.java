package com.range.stcfactor.expression.constant;

import com.range.stcfactor.common.Constant;

/**
 * 通用变量
 *
 * @author zrj5865@163.com
 * @create 2019-08-06
 */
public enum ExpVariables {

    /**
     * open
     */
    OPEN,

    /**
     * high
     */
    HIGH,

    /**
     * low
     */
    LOW,

    /**
     * close
     */
    CLOSE,

    /**
     * volume
     */
    VOL,

    /**
     * share
     */
    SHARE,

    /**
     * turnover
     */
    TURNOVER;

    public Class getType() {
        return Constant.DEFAULT_TYPE;
    }

}
