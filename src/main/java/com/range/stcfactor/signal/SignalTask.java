package com.range.stcfactor.signal;

import com.range.stcfactor.common.utils.ArrayUtils;
import com.range.stcfactor.expression.ExpFunctions;
import com.range.stcfactor.expression.constant.ExpVariables;
import com.range.stcfactor.expression.tree.ExpTree;
import com.range.stcfactor.expression.tree.ExpTreeNode;
import com.range.stcfactor.expression.tree.ExpModel;
import com.range.stcfactor.signal.data.DataModel;
import com.range.stcfactor.signal.data.DataScreen;
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

    private ExpFunctions functions;
    private ExpTree expression;
    private DataModel model;
    private SignalFilter filter;

    private INDArray income;

    public SignalTask(ExpTree expression, DataModel model, SignalFilter filter) {
        this.functions = new ExpFunctions(model);
        this.expression = expression;
        this.model = model;
        this.filter = filter;

        INDArray close = (INDArray) model.getData(ExpVariables.CLOSE);
        this.income = ArrayUtils.shift(close, -1).div(close).sub(1.0);
    }

    @Override
    public DataScreen call() {
        logger.debug("-------- Start expression : [{}].", expression);

        long startTime = System.currentTimeMillis();
        INDArray factor = (INDArray) calculate(expression.getRoot());
        logger.debug("======== Finish calculate [{}] factors cost {}s.", expression, (System.currentTimeMillis() - startTime) / 1000);

        startTime = System.currentTimeMillis();
        DataScreen screen = filter.screen(expression, factor, income);
        logger.debug("======== Finish filter [{}] indexes cost {}s.", expression, (System.currentTimeMillis() - startTime) / 1000);

        return screen;
    }

    private Object calculate(ExpTreeNode<ExpModel> node) {
        ExpModel expModel = node.getData();
        Object result = null;
        if (expModel.isFunction()) {
            if (expModel.isArrays()) {
                try {
                    List<INDArray> dataList = new ArrayList<>();
                    for (ExpTreeNode<ExpModel> n : node.getChildNodes()) {
                        dataList.add((INDArray) calculate(n));
                    }
                    result = ((Method) expModel.getModel()).invoke(functions, (Object) dataList.toArray(new INDArray[0]));
                } catch (Exception e) {
                    logger.error("Method execute error: {}", node, e);
                }

            } else if (expModel.isEmpty()) {
                try {
                    result = ((Method) expModel.getModel()).invoke(functions);
                } catch (Exception e) {
                    logger.error("Method execute error: {}", node, e);
                }

            } else {
                try {
                    List<Object> dataList = new ArrayList<>();
                    for (ExpTreeNode<ExpModel> n : node.getChildNodes()) {
                        dataList.add(calculate(n));
                    }
                    result = ((Method) expModel.getModel()).invoke(functions, dataList.toArray(new Object[0]));
                } catch (Exception e) {
                    logger.error("Method execute error: {}", node, e);
                }

            }
        } else {
            result = expModel.getModel();
            if (result instanceof ExpVariables) {
                result = model.getData((ExpVariables) result);
            }
        }

        return result;
    }

}
