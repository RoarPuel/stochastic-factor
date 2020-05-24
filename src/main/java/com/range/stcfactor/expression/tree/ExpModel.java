package com.range.stcfactor.expression.tree;

import com.range.stcfactor.common.Constant;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Method;

/**
 * 函数信息
 *
 * @author zrj5865@163.com
 * @create 2019-08-05
 */
public class ExpModel implements Cloneable {

    private static final Logger logger = LogManager.getLogger(ExpModel.class);

    private boolean isFunction;
    private boolean isArrays = false;
    private boolean isEmpty = false;
    private Object model;

    public ExpModel(Object model) {
        this.isFunction = model instanceof Method;
        if (model instanceof Method) {
            Class[] parameterTypes = ((Method) model).getParameterTypes();
            this.isArrays = parameterTypes.length == 1 && parameterTypes[0] == Constant.DEFAULT_TYPES;
            this.isEmpty = parameterTypes.length == 0;
        }
        this.model = model;
    }

    public boolean isFunction() {
        return isFunction;
    }

    public boolean isArrays() {
        return isArrays;
    }

    public boolean isEmpty() {
        return isEmpty;
    }

    public Object getModel() {
        return model;
    }

    public void setModel(Object model) {
        this.model = model;
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
        ExpModel expModel = (ExpModel) obj;
        if (expModel.model instanceof Method) {
            Method current = (Method) this.model;
            Method compare = (Method) expModel.model;
            return current.getName().equals(compare.getName())
                    && current.getParameterTypes().length == compare.getParameterTypes().length;
        } else {
            return expModel.model.equals(this.model);
        }
    }

    @Override
    public String toString() {
        if (this.model instanceof Method) {
            return ((Method) this.model).getName();
        }
        return String.valueOf(this.model);
    }

    @Override
    public Object clone() {
        ExpModel model = null;
        try {
            model = (ExpModel) super.clone();
        } catch (CloneNotSupportedException e) {
            logger.error("ExpModel clone error.", e);
        }
        return model;
    }

}
