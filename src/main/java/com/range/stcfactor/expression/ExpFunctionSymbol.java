package com.range.stcfactor.expression;

/**
 * 表达式符号
 *
 * @author zrj5865@163.com
 * @create 2020-01-06
 */
public enum ExpFunctionSymbol {

    /**
     * sum
     */
    sum("+"),

    /**
     * sub
     */
    sub("-"),

    /**
     * mul
     */
    mul("*"),

    /**
     * div
     */
    div("/");

    private String symbol;

    ExpFunctionSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getSymbol() {
        return symbol;
    }

}
