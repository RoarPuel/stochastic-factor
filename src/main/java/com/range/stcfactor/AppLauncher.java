package com.range.stcfactor;

import com.range.stcfactor.common.Constant;
import com.range.stcfactor.expression.ExpGenerator;
import com.range.stcfactor.expression.ExpVariables;
import com.range.stcfactor.expression.tree.ExpModel;
import com.range.stcfactor.expression.tree.ExpTree;
import com.range.stcfactor.expression.tree.ExpTreeNode;
import com.range.stcfactor.signal.SignalGenerator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.nd4j.linalg.api.ndarray.INDArray;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

/**
 * 总入口
 *
 * @author zrj5865@163.com
 * @create 2019-07-22
 */
public class AppLauncher {

    private static final Logger logger = LogManager.getLogger(AppLauncher.class);

    public static void main(String[] args) {
        Properties config = initConfig();

        logger.info("=== Start generate expressions.");
        ExpGenerator expGenerator = new ExpGenerator(config);
        Set<ExpTree> exps = expGenerator.generateRandomExpression();
        logger.info("=== Finish generate expressions. Actual count:{}.", exps.size());

        logger.info("=== Start generate signal.");
        SignalGenerator signalGenerator = new SignalGenerator(config);
//        signalGenerator.startTasks(exps);
        signalGenerator.startTask(customExp());
        logger.info("=== Finish signal task.");
    }

    private static ExpTree customExp() {
        ExpTree exp = new ExpTree(2);

        ExpModel child1Data = new ExpModel();
        child1Data.setModelName(ExpVariables.share.name());
        child1Data.setReturnType(INDArray.class);
        ExpTreeNode<ExpModel> child1 = new ExpTreeNode<>();
        child1.setData(child1Data);

        ExpModel child2Data = new ExpModel();
        child2Data.setModelName(ExpVariables.day_num.name());
        child2Data.setReturnType(Integer.class);
        ExpTreeNode<ExpModel> child2 = new ExpTreeNode<>();
        child2.setData(child2Data);

        ExpModel rootData = new ExpModel();
        rootData.setModelName("tsRank");
        rootData.setParametersType(new Class[]{INDArray.class, Integer.class});
        rootData.setReturnType(INDArray.class);

        ExpTreeNode<ExpModel> root = new ExpTreeNode<>();
        root.setData(rootData);
        exp.setRoot(root);
        List<ExpTreeNode<ExpModel>> children = new ArrayList<>();
        children.add(child1);
        children.add(child2);
        root.setChildNodes(children);

        return exp;
    }

    private static Properties initConfig() {
        Properties config = new Properties();
        config.put(Constant.EXP_TOTAL, "1");
        config.put(Constant.EXP_DEPTH_MIN, "3");
        config.put(Constant.EXP_DEPTH_MAX, "5");

        config.put(Constant.DATA_DATE_NUM, "2");
        config.put(Constant.DATA_FILE_PATH, "D:\\Work\\Project\\Java\\stochastic-factor\\data\\{0}.csv");

        config.put(Constant.THRESHOLD_INFORMATION_COEFFICIENT, "0.02");
        config.put(Constant.THRESHOLD_AUTO_CORRELATION, "0.8");
        return config;
    }

}
