package com.range.stcfactor.expression.tree;

import com.range.stcfactor.common.utils.RandomUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 生成树
 *
 * @author zrj5865@163.com
 * @create 2019-08-01
 */
public class ExpTreeFactory {

    private static final Logger logger = LogManager.getLogger(ExpTreeFactory.class);

    private final int MIN_INCLUSIVE = 2;
    private final int MAX_EXCLUSIVE = 10;

    private Map<Class, List<ExpModel>> models;
    private Map<Class, List<ExpModel>> functions;
    private Map<Class, List<ExpModel>> variables;
    private Map<ExpModel, Double> weights;

    public ExpTreeFactory(Map<Class, List<ExpModel>> models,
                          Map<Class, List<ExpModel>> functions,
                          Map<Class, List<ExpModel>> variables,
                          Map<ExpModel, Double> weights) {
        this.models = models;
        this.functions = functions;
        this.variables = variables;
        this.weights = weights;
    }

    public ExpTree buildSpecified(int depth, Class rootType) {
        return buildSpecified(depth, rootType, null);
    }

    /**
     * 生成指定深度公式树
     * @param depth 深度
     * @param root 根节点
     * @return 公式树
     */
    public ExpTree buildSpecified(int depth, Class rootType, ExpTreeNode<ExpModel> root) {
        if (depth < MIN_INCLUSIVE) {
            logger.warn("Depth can not < {}", MIN_INCLUSIVE);
            depth = MIN_INCLUSIVE;
        }
        return build(depth, root, rootType);
    }

    public ExpTree buildRandom(int depthMin, int depthMax, Class rootType) {
        return buildRandom(depthMin, depthMax, rootType, null);
    }

    /**
     * 生成随机深度公式树
     * @param depthMin 最大深度
     * @param depthMax 最小深度
     * @
     * @return 公式树
     */
    public ExpTree buildRandom(int depthMin, int depthMax, Class rootType, ExpTreeNode<ExpModel> root) {
        if (depthMin < MIN_INCLUSIVE) {
            depthMin = MIN_INCLUSIVE;
        }
        if (depthMin > depthMax) {
            depthMin = MIN_INCLUSIVE;
            depthMax = MAX_EXCLUSIVE;
        }
        logger.debug("Build tree {}<=depth<={}, root: {}.", depthMin, depthMax, root);
        return build(RandomUtils.getRandomNum(depthMin, depthMax + 1), root, rootType);
    }

    /**
     * 创建公式树
     */
    private ExpTree build(int depth, ExpTreeNode<ExpModel> root, Class rootType) {
        ExpTree expTree = new ExpTree(depth);
        if (root == null) {
            root = new ExpTreeNode<>();
            root.setData(getRandomInfo(functions.get(rootType)));
        }
        addChild(root, depth - 1);
        expTree.setRoot(root);
        return expTree;
    }

    /**
     * 递归添加子节点
     * @param parent 父节点
     * @param layer 树的层数
     */
    private void addChild(ExpTreeNode<ExpModel> parent, int layer) {
        List<ExpTreeNode<ExpModel>> nodes = new ArrayList<>();
        if (parent.getData().getParametersType() != null) {
            if (layer <= 0) {
                for (Class type : parent.getData().getParametersType()) {
                    ExpTreeNode<ExpModel> node = new ExpTreeNode<>();
                    node.setData(getRandomInfo(variables.get(type)));
                    node.setChildNodes(new ArrayList<>());
                    nodes.add(node);
                }
            } else {
                for (Class type : parent.getData().getParametersType()) {
                    ExpTreeNode<ExpModel> node = new ExpTreeNode<>();
                    node.setData(getRandomInfo(models.get(type)));
                    addChild(node, layer - 1);
                    nodes.add(node);
                }
            }
        }
        parent.setChildNodes(nodes);
    }

    private ExpModel getRandomInfo(List<ExpModel> infos) {
        return (ExpModel) RandomUtils.getRandomInfo(infos).clone();
    }

}
