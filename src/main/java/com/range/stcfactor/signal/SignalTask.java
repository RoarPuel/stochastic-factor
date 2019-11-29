package com.range.stcfactor.signal;

import com.range.stcfactor.common.utils.ArrayUtils;
import com.range.stcfactor.expression.ExpFunctions;
import com.range.stcfactor.expression.ExpVariables;
import com.range.stcfactor.expression.tree.ExpTree;
import com.range.stcfactor.expression.tree.ExpTreeNode;
import com.range.stcfactor.expression.tree.ExpModel;
import com.range.stcfactor.signal.data.DataFactory;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.nd4j.linalg.api.buffer.DataType;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

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
public class SignalTask implements Callable<ExpTree> {

    private static final Logger logger = LogManager.getLogger(SignalTask.class);

    private ExpTree expTree;
    private DataFactory dataFactory;
    private INDArray income;

    public SignalTask(ExpTree expTree, DataFactory dataFactory) {
        this.expTree = expTree;
        this.dataFactory = dataFactory;

        INDArray close = (INDArray) dataFactory.obtainData(ExpVariables.close);
        this.income = ArrayUtils.shift(close, -1).div(close).sub(1.0);
    }

    @Override
    public ExpTree call() {
        logger.info("===== Thread [{}] expression: [{}].", Thread.currentThread().getId(), expTree);
        long startTime = System.currentTimeMillis();
        INDArray result = (INDArray) calculate(expTree.getRoot());
        logger.info("===== Calculate [{}] cost {}s.", expTree, (System.currentTimeMillis() - startTime) / 1000);

        expTree.setIcThreshold(filter(result));
        return expTree;
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
            return dataFactory.obtainData(node.getData().getModelName());
        }
    }

    private Double filter(INDArray array) {
        long[] shape = {1, array.rows()};
        INDArray result = Nd4j.valueArrayOf(shape, Double.NaN, DataType.DOUBLE);
        for (int current=0; current < array.rows(); current++) {
            INDArray row1 = array.getRow(current);
            INDArray row2 = income.getRow(current);
            result.put(0, current, ArrayUtils.corrEffective(row1, row2));
        }
        return (Double) ArrayUtils.replaceNan(result, 0.0).meanNumber();
    }

}
