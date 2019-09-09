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
    private List<String> header;
    private List<Date> index;
    private INDArray data;

    public ExpVariables getType() {
        return type;
    }

    public void setType(ExpVariables type) {
        this.type = type;
    }

    public List<String> getHeader() {
        return header;
    }

    public void setHeader(List<String> header) {
        this.header = header;
    }

    public List<Date> getIndex() {
        return index;
    }

    public void setIndex(List<Date> index) {
        this.index = index;
    }

    public INDArray getData() {
        return data;
    }

    public void setData(INDArray data) {
        this.data = data;
    }

}
