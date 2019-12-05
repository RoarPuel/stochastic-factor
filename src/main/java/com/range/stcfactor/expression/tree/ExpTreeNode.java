package com.range.stcfactor.expression.tree;

import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

/**
 * 节点
 *
 * @author zrj5865@163.com
 * @create 2019-08-05
 */
public class ExpTreeNode<T> {

    private T data;
    private List<ExpTreeNode<T>> childNodes;

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public List<ExpTreeNode<T>> getChildNodes() {
        return childNodes;
    }

    public void setChildNodes(List<ExpTreeNode<T>> childNodes) {
        this.childNodes = childNodes;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(data);
        if (CollectionUtils.isEmpty(childNodes)) {
            return sb.toString();
        }
        sb.append("(");
        childNodes.forEach(node -> sb.append(node.getData()));
        sb.append(")");
        return sb.toString();
    }
}
