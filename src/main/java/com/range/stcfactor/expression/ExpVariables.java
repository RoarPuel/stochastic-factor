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
    open {
        @Override
        public Class getParameterType() {
            return Constant.DEFAULT_TYPE;
        }
    },

    /**
     * high
     */
    high {
        @Override
        public Class getParameterType() {
            return Constant.DEFAULT_TYPE;
        }
    },

    /**
     * low
     */
    low {
        @Override
        public Class getParameterType() {
            return Constant.DEFAULT_TYPE;
        }
    },

    /**
     * close
     */
    close {
        @Override
        public Class getParameterType() {
            return Constant.DEFAULT_TYPE;
        }
    },

    /**
     * vol
     */
    vol {
        @Override
        public Class getParameterType() {
            return Constant.DEFAULT_TYPE;
        }
    },

    /**
     * share
     */
    share {
        @Override
        public Class getParameterType() {
            return Constant.DEFAULT_TYPE;
        }
    },

    /**
     * turnover
     */
    turnover {
        @Override
        public Class getParameterType() {
            return Constant.DEFAULT_TYPE;
        }
    },

    /**
     * day_num
     */
    day_num {
        @Override
        public Class getParameterType() {
            return Integer.class;
        }
    };

    public abstract Class getParameterType();

}
