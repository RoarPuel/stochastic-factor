package com.range.stcfactor.signal.data;

import com.range.stcfactor.expression.ExpVariables;

import java.util.HashMap;
import java.util.Map;

/**
 * 数据
 *
 * @author zrj5865@163.com
 * @create 2019-08-16
 */
public class DataModel {

    private Map<ExpVariables, Object> datas;

    public DataModel() {
        datas = new HashMap<>();
    }

    public void putData(ExpVariables var, Object data) {
        this.datas.put(var, data);
    }

    public Object getData(ExpVariables var) {
        return this.datas.get(var);
    }


}
