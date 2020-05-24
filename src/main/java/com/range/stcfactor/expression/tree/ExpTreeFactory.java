package com.range.stcfactor.expression.tree;

import com.range.stcfactor.common.Constant;
import com.range.stcfactor.common.utils.RandomsUtils;
import com.range.stcfactor.expression.constant.ExpPrintFormat;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

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
    private Map<String, Function<Integer, ExpModel>> constants;
    private Map<ExpModel, Double> weights;
    private ExpPrintFormat format;

    public ExpTreeFactory(Map<Class, List<ExpModel>> models,
                          Map<Class, List<ExpModel>> functions,
                          Map<Class, List<ExpModel>> variables,
                          Map<String, Function<Integer, ExpModel>> constants,
                          Map<ExpModel, Double> weights,
                          ExpPrintFormat format) {
        this.models = models;
        this.functions = functions;
        this.variables = variables;
        this.constants = constants;
        this.weights = weights;
        this.format = format;
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
        return build(RandomsUtils.getRandomInt(depthMin, depthMax + 1), root, rootType);
    }

    /**
     * 创建公式树
     */
    private ExpTree build(int depth, ExpTreeNode<ExpModel> root, Class rootType) {
        ExpTree expTree = new ExpTree(depth, format);
        if (root == null) {
            root = newNode(rootType, functions, null, 0);
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
        ExpModel parentModel = parent.getData();
        if (parentModel.isFunction()) {
            List<ExpTreeNode<ExpModel>> nodes = new ArrayList<>();
            Method method = (Method) parentModel.getModel();
            String parentName = method.getName();
            Class[] parameterTypes = method.getParameterTypes();
            if (layer > 0) {
                if (parentModel.isArrays()) {
                    ExpModel em = constants.get(parentName).apply(0);
                    for (int index=0; index<(Integer) em.getModel(); index++) {
                        ExpTreeNode<ExpModel> node = newNode(Constant.DEFAULT_TYPE, models, parentName, index);
                        addChild(node, layer - 1);
                        nodes.add(node);
                    }
                } else {
                    for (int index=0; index<parameterTypes.length; index++) {
                        ExpTreeNode<ExpModel> node = newNode(parameterTypes[index], models, parentName, index);
                        addChild(node, layer - 1);
                        nodes.add(node);
                    }
                }
            } else {
                if (parentModel.isArrays()) {
                    ExpModel em = constants.get(parentName).apply(0);
                    for (int index=0; index<(Integer) em.getModel(); index++) {
                        ExpTreeNode<ExpModel> node = newNode(Constant.DEFAULT_TYPE, variables, parentName, index);
                        node.setChildNodes(new ArrayList<>());
                        nodes.add(node);
                    }
                } else {
                    for (int index=0; index<parameterTypes.length; index++) {
                        ExpTreeNode<ExpModel> node = newNode(parameterTypes[index], variables, parentName, index);
                        node.setChildNodes(new ArrayList<>());
                        nodes.add(node);
                    }
                }
            }
            parent.setChildNodes(nodes);
        } else {
            parent.setChildNodes(new ArrayList<>());
        }
    }

    private ExpTreeNode<ExpModel> newNode(Class type, Map<Class, List<ExpModel>> infos, String parentName, Integer index) {
        ExpModel model = null;
        if (type == Constant.DEFAULT_TYPE) {
            model = (ExpModel) RandomsUtils.getRandomInfo(infos.get(type)).clone();
        } else {
            if (constants.containsKey(parentName)) {
                model = constants.get(parentName).apply(index);
            } else {
                if (type == Integer.class) {
                    model = new ExpModel(RandomsUtils.getRandomInt(2, 243));
                } else {
                    logger.error("[{}]({} : {}) type has not default value.", parentName, index+1, type.getSimpleName());
                }
            }
        }
        return new ExpTreeNode<>(model);
    }

}
