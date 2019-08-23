package com.range.stcfactor.expression.tree;

import com.range.stcfactor.expression.ExpVariables;
import com.range.stcfactor.expression.RandomHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tech.tablesaw.api.DoubleColumn;

import java.util.LinkedList;
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

    private Map<Class, List<FunctionInfo>> functions;
    private List<ExpVariables> variables;
    private Map<FunctionInfo, Double> weights;

    public ExpTreeFactory(Map<Class, List<FunctionInfo>> functions, List<ExpVariables> variables, Map<FunctionInfo, Double> weights) {
        this.functions = functions;
        this.variables = variables;
        this.weights = weights;
    }

    public ExpTree buildSpecified(int depth) {
        return buildSpecified(depth, null);
    }

    /**
     * 生成指定深度公式树
     * @param depth 深度
     * @param root 根节点
     * @return 公式树
     */
    public ExpTree buildSpecified(int depth, ExpTreeNode<FunctionInfo> root) {
        if (depth < MIN_INCLUSIVE) {
            logger.warn("Depth can not < {}", MIN_INCLUSIVE);
            depth = MIN_INCLUSIVE;
        }
        return build(depth, root);
    }

    public ExpTree buildRandom(int depthMin, int depthMax) {
        return buildRandom(depthMin, depthMax, null);
    }

    /**
     * 生成随机深度公式树
     * @param depthMin 最大深度
     * @param depthMax 最小深度
     * @
     * @return 公式树
     */
    public ExpTree buildRandom(int depthMin, int depthMax, ExpTreeNode<FunctionInfo> root) {
        if (depthMin < MIN_INCLUSIVE) {
            depthMin = MIN_INCLUSIVE;
        }
        if (depthMin > depthMax) {
            depthMin = MIN_INCLUSIVE;
            depthMax = MAX_EXCLUSIVE;
        }
        logger.debug("Build tree {}<=depth<={}, root: {}.", depthMin, depthMax, root);
        return build(RandomHelper.getRandomNum(depthMin, depthMax), root);
    }

    /**
     * 创建公式树
     */
    private ExpTree build(int depth, ExpTreeNode<FunctionInfo> root) {
        ExpTree expTree = new ExpTree(depth);
        if (root == null) {
            root = new ExpTreeNode<>();
            root.setData(RandomHelper.getRandomFunction(functions.get(DoubleColumn.class)));
        }
        addChild(root, depth-1);
        expTree.setRoot(root);
        return expTree;
    }

    /**
     * 递归添加子节点
     * @param parent 父节点
     * @param layer 树的层数
     */
    private void addChild(ExpTreeNode<FunctionInfo> parent, int layer) {
        List<ExpTreeNode<FunctionInfo>> nodes = new LinkedList<>();
        if (layer > 1) {
            for (Class type : parent.getData().getVariablesType()) {
                ExpTreeNode<FunctionInfo> node = new ExpTreeNode<>();
                node.setData(RandomHelper.getRandomFunction(functions.get(type)));
                addChild(node, layer-1);
                nodes.add(node);
            }
        } else {
            for (Class type : parent.getData().getVariablesType()) {
                ExpTreeNode<FunctionInfo> node = new ExpTreeNode<>();
                node.setData(new FunctionInfo(RandomHelper.getRandomVariable(variables).name(), new Class[]{}, type));
                node.setChildNodes(new LinkedList<>());
                nodes.add(node);
            }
        }
        parent.setChildNodes(nodes);
    }

}
