package com.range.stcfactor.signal.data;

import com.range.stcfactor.expression.ExpVariables;
import org.nd4j.linalg.api.ndarray.INDArray;

import java.util.Date;
import java.util.List;

/**
 * 数据
 *
 * @author zrj5865@163.com
 * @create 2019-09-06
 */
public class DataBean {

    private ExpVariables type;
    private List<String> headers;
    private List<Date> indexes;
    private INDArray data;

    public ExpVariables getType() {
        return type;
    }

    public void setType(ExpVariables type) {
        this.type = type;
    }

    public List<String> getHeaders() {
        return headers;
    }

    public void setHeaders(List<String> headers) {
        this.headers = headers;
    }

    public List<Date> getIndexes() {
        return indexes;
    }

    public void setIndexes(List<Date> indexes) {
        this.indexes = indexes;
    }

    public INDArray getData() {
        return data;
    }

    public void setData(INDArray data) {
        this.data = data;
    }

}
