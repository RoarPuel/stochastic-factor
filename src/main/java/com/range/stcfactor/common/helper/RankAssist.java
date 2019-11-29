package com.range.stcfactor.common.helper;

/**
 * @author renjie.zhu@woqutech.com
 * @create 2019-09-29
 */
public class RankAssist {

    private Double data;
    private Integer oldIndex;
    private Double newIndex;

    public RankAssist(Double data, Integer oldIndex, Double newIndex) {
        this.data = data;
        this.oldIndex = oldIndex;
        this.newIndex = newIndex;
    }

    public Double getData() {
        return data;
    }

    public void setData(Double data) {
        this.data = data;
    }

    public Integer getOldIndex() {
        return oldIndex;
    }

    public void setOldIndex(Integer oldIndex) {
        this.oldIndex = oldIndex;
    }

    public Double getNewIndex() {
        return newIndex;
    }

    public void setNewIndex(Double newIndex) {
        this.newIndex = newIndex;
    }

}
