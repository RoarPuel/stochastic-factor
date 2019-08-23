package com.range.stcfactor.signal.data;

import com.range.stcfactor.expression.ExpVariables;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * 数据工厂
 *
 * @author zrj5865@163.com
 * @create 2019-08-16
 */
public class DataFactory {

    private static final Logger logger = LogManager.getLogger(DataFactory.class);

    private DateFormat format = new SimpleDateFormat("yyyy-MM-dd");

    private DataModel dataModel;
    private Set<Date> dates;
    private StringColumn index;

    public DataFactory(DataModel dataModel) {
        this.dataModel = dataModel;
        initDate();
    }

    private void initDate() {
        this.index = dataModel.getOpenTable().stringColumn("code");

        Set<Date> openDates = getColumnsName(dataModel.getOpenTable());
        Set<Date> highDates = getColumnsName(dataModel.getHighTable());
        Set<Date> lowDates = getColumnsName(dataModel.getLowTable());
        Set<Date> closeDates = getColumnsName(dataModel.getCloseTable());

        this.dates = new TreeSet<>();
        this.dates.addAll(openDates);
        this.dates.retainAll(highDates);
        this.dates.retainAll(lowDates);
        this.dates.retainAll(closeDates);
    }

    private Set<Date> getColumnsName(Table table) {
        List<String> columnsName = table.columnNames();
        columnsName.remove("code");

        Set<Date> dates = new TreeSet<>();
        columnsName.forEach(head -> dates.add(parseStrToDate(head)));
        return dates;
    }

    public Date parseStrToDate(String date) {
        try {
            return format.parse(date);
        } catch (Exception e) {
            logger.error("Parse date error: {}", e.getMessage());
            return null;
        }
    }

    public String parseDateToStr(Date date) {
        return format.format(date);
    }

    public Object obtainData(ExpVariables var, Class type, String columnName) {
        // TODO 返回值类型
        if (type == Integer.class) {
            return 3;
        }

        DoubleColumn column = null;
        switch (var) {
            case open:
                column = dataModel.getOpenTable().doubleColumn(columnName);
                break;
            case high:
                column = dataModel.getHighTable().doubleColumn(columnName);
                break;
            case low:
                column = dataModel.getLowTable().doubleColumn(columnName);
                break;
            case close:
                column = dataModel.getCloseTable().doubleColumn(columnName);
                break;
            default:
                logger.error("Obtain data error type: {}", var);
                break;
        }
        return column;
    }

    public DataModel getDataModel() {
        return dataModel;
    }

    public void setDataModel(DataModel dataModel) {
        this.dataModel = dataModel;
    }

    public Set<Date> getDates() {
        return dates;
    }

    public void setDates(Set<Date> dates) {
        this.dates = dates;
    }

    public StringColumn getIndex() {
        return index;
    }

    public void setIndex(StringColumn index) {
        this.index = index;
    }

}
