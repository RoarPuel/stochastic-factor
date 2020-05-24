package com.range.stcfactor.expression.constant;

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
    SUM("+"),

    /**
     * sub
     */
    SUB("-"),

    /**
     * mul
     */
    MUL("*"),

    /**
     * div
     */
    DIV("/");

    private String symbol;

    ExpFunctionSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getSymbol() {
        return symbol;
    }

}
