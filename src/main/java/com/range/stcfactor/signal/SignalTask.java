package com.range.stcfactor.signal;

import com.range.stcfactor.expression.ExpFunctions;
import com.range.stcfactor.expression.tree.ExpTree;
import com.range.stcfactor.expression.tree.ExpTreeNode;
import com.range.stcfactor.expression.tree.ExpModel;
import com.range.stcfactor.signal.data.DataFactory;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Method;
import java.util.ArrayList;
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

    public SignalTask(ExpTree expTree, DataFactory dataFactory) {
        this.expTree = expTree;
        this.dataFactory = dataFactory;
    }

    @Override
    public void run() {
        logger.info(expTree);
        System.out.println(calculate(expTree.getRoot()));
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

}
