package com.range.stcfactor.signal;

import com.range.stcfactor.common.utils.ArrayUtils;
import com.range.stcfactor.expression.ExpFunctions;
import com.range.stcfactor.expression.ExpVariables;
import com.range.stcfactor.expression.tree.ExpTree;
import com.range.stcfactor.expression.tree.ExpTreeNode;
import com.range.stcfactor.expression.tree.ExpModel;
import com.range.stcfactor.signal.data.DataFactory;
import com.range.stcfactor.signal.data.DataScreen;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.nd4j.linalg.api.ndarray.INDArray;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * 计算任务
 *
 * @author zrj5865@163.com
 * @create 2019-08-08
 */
public class SignalTask implements Callable<DataScreen> {

    private static final Logger logger = LogManager.getLogger(SignalTask.class);

    private ExpTree expression;
    private DataFactory factory;
    private SignalFilter filter;

    private INDArray income;

    public SignalTask(ExpTree expression, DataFactory factory, SignalFilter filter) {
        this.expression = expression;
        this.factory = factory;
        this.filter = filter;

        INDArray close = (INDArray) factory.obtainData(ExpVariables.close);
        this.income = ArrayUtils.shift(close, -1).div(close).sub(1.0);
    }

    @Override
    public DataScreen call() {
        logger.info("-------- Start expression : [{}].", expression);

        long startTime = System.currentTimeMillis();
        INDArray factor = (INDArray) calculate(expression.getRoot());
        logger.info("======== Finish calculate [{}] factors cost {}s.", expression, (System.currentTimeMillis() - startTime) / 1000);

        startTime = System.currentTimeMillis();
        DataScreen screen = filter.screen(expression, factor, income);
        logger.info("======== Finish filter [{}] indexes cost {}s.", expression, (System.currentTimeMillis() - startTime) / 1000);

        return screen;
    }

    private Object calculate(ExpTreeNode<ExpModel> node) {
        if (CollectionUtils.isNotEmpty(node.getChildNodes())) {
            Object result = null;
            try {
                List<Class> paraList = new ArrayList<>();
                List<Object> dataList = new ArrayList<>();
                for (ExpTreeNode<ExpModel> n : node.getChildNodes()) {
                    paraList.add(n.getData().getReturnType());
                    dataList.add(calculate(n));
                }

                Class[] paras = new Class[paraList.size()];
                paraList.toArray(paras);
                Object[] datas = new Object[dataList.size()];
                dataList.toArray(datas);

                Method method = ExpFunctions.class.getMethod(node.getData().getModelName(), paras);
                result = method.invoke(null, datas);
            } catch (Exception e) {
                logger.error("Method execute error: {}", node, e);
            }
            return result;
        } else {
            Object data;
            String variableName = node.getData().getModelName();
            try {
                ExpVariables variable = ExpVariables.valueOf(variableName);
                data = factory.obtainData(variable);
                if (ExpVariables.day_num == variable) {
                    node.getData().setModelName(String.valueOf(data));
                }
            } catch (Exception e) {
                logger.debug("Error variable: {}.", variableName, e);
                data = Integer.valueOf(variableName);
            }
            return data;
        }
    }

}
