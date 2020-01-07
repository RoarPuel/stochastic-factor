package com.range.stcfactor.expression.tree;

import com.range.stcfactor.expression.ExpFunctionSymbol;
import com.range.stcfactor.expression.ExpPrintFormat;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 树
 *
 * @author zrj5865@163.com
 * @create 2019-07-29
 */
public class ExpTree {

    private static final Logger logger = LogManager.getLogger(ExpTree.class);

    private int depth;
    private ExpTreeNode<ExpModel> root;
    private ExpPrintFormat format;

    public ExpTree(int depth, ExpPrintFormat format) {
        this.depth = depth;
        this.format = format;
    }

    public ExpTree(ExpTreeNode<ExpModel> root, ExpPrintFormat format) {
        this.depth = analysisDepth(root);
        this.root = root;
        this.format = format;
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public ExpTreeNode<ExpModel> getRoot() {
        return root;
    }

    public void setRoot(ExpTreeNode<ExpModel> root) {
        this.root = root;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj.getClass() != this.getClass()) {
            return false;
        }
        ExpTree expTree = (ExpTree) obj;
        if (depth != expTree.getDepth()) {
            return false;
        }
        return this.root.equals(expTree.getRoot());
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        obtainExpression(root, sb);
        String result;
        switch (format) {
            case UPPER:
                result = sb.toString().toUpperCase();
                break;
            case LOWER:
                result = sb.toString().toLowerCase();
                break;
            case DEFAULT:
            default:
                result = sb.toString();
                break;
        }
        return result;
    }

    /**
     * 生成公式
     * @param node 节点
     * @param sb 公式字符串
     */
    private void obtainExpression(ExpTreeNode<ExpModel> node, StringBuilder sb) {
        String symbol = "";
        try {
            symbol = ExpFunctionSymbol.valueOf(node.getData().getModelName().toUpperCase()).getSymbol();
        } catch (Exception e) {
            logger.debug("Not found function symbol");
        }

        if (StringUtils.isEmpty(symbol)) {
            sb.append(node.getData().getModelName());
        }
        if (CollectionUtils.isNotEmpty(node.getChildNodes())) {
            sb.append("(");
            int index = 0;
            for (ExpTreeNode<ExpModel> n : node.getChildNodes()) {
                obtainExpression(n, sb);
                if (++index < node.getChildNodes().size()) {
                    if (StringUtils.isEmpty(symbol)) {
                        sb.append(", ");
                    } else {
                        sb.append(" ").append(symbol).append(" ");
                    }
                }
            }
            sb.append(")");
        }
    }

    private int analysisDepth(ExpTreeNode<ExpModel> node) {
        if (CollectionUtils.isEmpty(node.getChildNodes())) {
            return 0;
        }

        int max = 0;
        for (ExpTreeNode<ExpModel> child : node.getChildNodes()) {
            int depth = analysisDepth(child);
            if (depth > max) {
                max = depth;
            }
        }
        return max + 1;
    }

}
