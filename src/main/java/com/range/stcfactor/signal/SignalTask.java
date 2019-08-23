package com.range.stcfactor.signal;

import com.range.stcfactor.expression.ExpFunctions;
import com.range.stcfactor.expression.ExpVariables;
import com.range.stcfactor.expression.tree.ExpTree;
import com.range.stcfactor.expression.tree.ExpTreeNode;
import com.range.stcfactor.expression.tree.FunctionInfo;
import com.range.stcfactor.signal.data.DataFactory;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.Table;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 计算任务
 *
 * @author zrj5865@163.com
 * @create 2019-08-08
 */
public class SignalTask implements Runnable {

    private static final Logger logger = LogManager.getLogger(SignalTask.class);

    private ExpTree expTree;
    private DataFactory dataFactory;
    private Date startDate;
    private Date endDate;

    public SignalTask(ExpTree expTree, DataFactory dataFactory, String startDate, String endDate) {
        this.expTree = expTree;
        this.dataFactory = dataFactory;
        this.startDate = dataFactory.parseStrToDate(startDate);
        this.endDate = dataFactory.parseStrToDate(endDate);
    }

    @Override
    public void run() {
        Table table = Table.create(expTree.toString());
        table.addColumns(dataFactory.getIndex());
        for (Date date : dataFactory.getDates()) {
            if (startDate.after(date) || endDate.before(date)) {
                continue;
            }
            String dateStr = dataFactory.parseDateToStr(date);
            DoubleColumn column = (DoubleColumn) calculate(expTree.getRoot(), dateStr);
            table.addColumns(column.setName(dateStr));
        }
        System.out.println(table);
    }

    private Object calculate(ExpTreeNode<FunctionInfo> node, String columnName) {
        if (CollectionUtils.isNotEmpty(node.getChildNodes())) {
            Object result = null;
            try {
                List<Class> paraList = new ArrayList<>();
                List<Object> dataList = new ArrayList<>();
                for (ExpTreeNode<FunctionInfo> n : node.getChildNodes()) {
                    paraList.add(n.getData().getReturnType());
                    dataList.add(calculate(n, columnName));
                }

                Class[] paras = new Class[paraList.size()];
                paraList.toArray(paras);
                Object[] datas = new Object[dataList.size()];
                dataList.toArray(datas);

                Method method = ExpFunctions.class.getMethod(node.getData().getFunctionName(), paras);
                result = method.invoke(new ExpFunctions(), datas);
            } catch (Exception e) {
                logger.error("Method execute error: {}", e.getMessage());
            }
            return result;
        } else {
            return dataFactory.obtainData(ExpVariables.valueOf(node.getData().getFunctionName()), node.getData().getReturnType(), columnName);
        }
    }

}
