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
    open,

    /**
     * high
     */
    high,

    /**
     * low
     */
    low,

    /**
     * close
     */
    close,

    /**
     * volume
     */
    vol,

    /**
     * share
     */
    share,

    /**
     * turnover
     */
    turnover,

    /**
     * day_num
     */
    day_num;

    public Class getType() {
        if (this == day_num) {
            return Integer.class;
        } else {
            return Constant.DEFAULT_TYPE;
        }
    }

}
