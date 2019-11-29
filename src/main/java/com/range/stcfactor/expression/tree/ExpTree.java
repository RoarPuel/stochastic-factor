package com.range.stcfactor.expression.tree;

import org.apache.commons.collections4.CollectionUtils;

/**
 * 树
 *
 * @author zrj5865@163.com
 * @create 2019-07-29
 */
public class ExpTree {

    private int depth;
    private ExpTreeNode<ExpModel> root;
    private double icThreshold;
    private double acThreshold;

    public ExpTree(int depth) {
        this.depth = depth;
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

    public double getIcThreshold() {
        return icThreshold;
    }

    public void setIcThreshold(double icThreshold) {
        this.icThreshold = icThreshold;
    }

    public double getAcThreshold() {
        return acThreshold;
    }

    public void setAcThreshold(double acThreshold) {
        this.acThreshold = acThreshold;
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
        StringBuilder stringBuilder = new StringBuilder();
        obtainExpression(root, stringBuilder);
        return stringBuilder.toString();
    }

    /**
     * 生成公式
     * @param node 节点
     * @param sb 公式字符串
     */
    private void obtainExpression(ExpTreeNode<ExpModel> node, StringBuilder sb) {
        sb.append(node.getData().getModelName());
        if (CollectionUtils.isNotEmpty(node.getChildNodes())) {
            sb.append("(");
            int index = 0;
            for (ExpTreeNode<ExpModel> n : node.getChildNodes()) {
                obtainExpression(n, sb);
                if (++index < node.getChildNodes().size()) {
                    sb.append(",");
                }
            }
            sb.append(")");
        }
    }

}
