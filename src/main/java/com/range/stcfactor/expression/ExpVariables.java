package com.range.stcfactor.expression;

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
    TURNOVER,

    /**
     * day_num
     */
    DAY_NUM;

    public Class getType() {
        if (this == DAY_NUM) {
            return Integer.class;
        } else {
            return Constant.DEFAULT_TYPE;
        }
    }

}
