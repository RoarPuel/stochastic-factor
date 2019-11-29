package com.range.stcfactor.signal.data;

import com.range.stcfactor.expression.ExpVariables;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 数据工厂
 *
 * @author zrj5865@163.com
 * @create 2019-08-16
 */
public class DataFactory {

    private static final Logger logger = LogManager.getLogger(DataFactory.class);

    private DataModel dataModel;

    public DataFactory(DataModel dataModel) {
        this.dataModel = dataModel;
    }

    public Object obtainData(String variableName) {
        return dataModel.getData(ExpVariables.valueOf(variableName));
    }

    public Object obtainData(ExpVariables variable) {
        return dataModel.getData(variable);
    }

}
