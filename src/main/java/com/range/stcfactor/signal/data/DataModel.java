package com.range.stcfactor.signal.data;

import tech.tablesaw.api.Table;

/**
 * 数据
 *
 * @author zrj5865@163.com
 * @create 2019-08-16
 */
public class DataModel {

    private Table openTable;
    private Table highTable;
    private Table lowTable;
    private Table closeTable;

    public Table getOpenTable() {
        return openTable;
    }

    public void setOpenTable(Table openTable) {
        this.openTable = openTable;
    }

    public Table getHighTable() {
        return highTable;
    }

    public void setHighTable(Table highTable) {
        this.highTable = highTable;
    }

    public Table getLowTable() {
        return lowTable;
    }

    public void setLowTable(Table lowTable) {
        this.lowTable = lowTable;
    }

    public Table getCloseTable() {
        return closeTable;
    }

    public void setCloseTable(Table closeTable) {
        this.closeTable = closeTable;
    }

}
