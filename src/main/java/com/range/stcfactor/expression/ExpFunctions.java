package com.range.stcfactor.expression;

import tech.tablesaw.api.DoubleColumn;

/**
 * 表达式
 *
 * @author zrj5865@163.com
 * @create 2019-07-22
 */
public class ExpFunctions {

    public DoubleColumn add(DoubleColumn num1, DoubleColumn num2) {
        return num1.add(num2).setName("add(" + num1.name() + "," + num2.name() + ")");
    }

    public DoubleColumn subtract(DoubleColumn num1, DoubleColumn num2) {
        return num1.subtract(num2).setName("subtract(" + num1.name() + "," + num2.name() + ")");
    }

    public DoubleColumn multiply(DoubleColumn num1, DoubleColumn num2) {
        return num1.multiply(num2).setName("multiply(" + num1.name() + "," + num2.name() + ")");
    }

    public DoubleColumn divide(DoubleColumn num1, DoubleColumn num2) {
        return num1.divide(num2).setName("divide(" + num1.name() + "," + num2.name() + ")");
    }

}
