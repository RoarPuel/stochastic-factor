package com.range.stcfactor.expression.tree;

/**
 * 函数信息
 *
 * @author renjie.zhu@woqutech.com
 * @create 2019-08-05
 */
public class FunctionInfo {

    private String functionName;
    private Class[] variablesType;
    private Class returnType;

    public FunctionInfo() {

    }

    public FunctionInfo(String functionName, Class[] variablesType, Class returnType) {
        this.functionName = functionName;
        this.variablesType = variablesType;
        this.returnType = returnType;
    }

    public String getFunctionName() {
        return functionName;
    }

    public void setFunctionName(String functionName) {
        this.functionName = functionName;
    }

    public Class[] getVariablesType() {
        return variablesType;
    }

    public void setVariablesType(Class[] variablesType) {
        this.variablesType = variablesType;
    }

    public Class getReturnType() {
        return returnType;
    }

    public void setReturnType(Class returnType) {
        this.returnType = returnType;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj.getClass() != this.getClass()) {
            return false;
        }
        FunctionInfo functionInfo = (FunctionInfo) obj;
        if (this.functionName.equals(functionInfo.getFunctionName())
            && this.variablesType.length == functionInfo.variablesType.length) {
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return "name:[" + functionName + "], parameters count:[" + variablesType.length + "], return type:[" + returnType + "]";
    }
}
