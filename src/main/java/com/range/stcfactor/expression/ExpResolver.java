package com.range.stcfactor.expression;

import com.range.stcfactor.expression.tree.ExpModel;
import com.range.stcfactor.expression.tree.ExpTree;
import com.range.stcfactor.expression.tree.ExpTreeNode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

/**
 * @author zrj5865@163.com
 * @create 2019-12-12
 */
public class ExpResolver {

    private static final Logger logger = LogManager.getLogger(ExpResolver.class);

    private static Map<String, Method> methods;

    static {
        methods = new HashMap<>();
        for (Method method : ExpFunctions.class.getDeclaredMethods()) {
            methods.put(method.getName(), method);
        }
    }

    public static ExpTree analysis(String expStr) {
        return new ExpTree(resolveNode(expStr));
    }

    private static ExpTreeNode<ExpModel> resolveNode(String expStr) {
        ExpTreeNode<ExpModel> node = new ExpTreeNode<>();
        int left = expStr.indexOf("(");
        if (left >= 0) {
            List<String> childNodeStrs = splitParameters(expStr.substring(left));
            List<ExpTreeNode<ExpModel>> childNodes = new ArrayList<>();
            for (String childNodeStr : childNodeStrs) {
                childNodes.add(resolveNode(childNodeStr));
            }
            node.setData(getFunction(expStr.substring(0, left)));
            node.setChildNodes(childNodes);
        } else {
            node.setData(getVariable(expStr));
            node.setChildNodes(new ArrayList<>());
        }

        return node;
    }

    private static ExpModel getFunction(String functionName) {
        Class[] parametersType = null;
        Class returnType = null;
        try {
            Method method = methods.get(functionName);
            parametersType = method.getParameterTypes();
            returnType = method.getReturnType();
        } catch (Exception e) {
            logger.error("Get function: [{}] error.", functionName, e);
        }
        return new ExpModel(functionName, parametersType, returnType);
    }

    private static ExpModel getVariable(String variableName) {
        Class returnType;
        try {
            returnType = ExpVariables.valueOf(variableName).getType();
        } catch (Exception e) {
            returnType = ExpVariables.day_num.getType();
        }
        return new ExpModel(variableName, null, returnType);
    }

    private static List<String> splitParameters(String parameterStr) {
        List<String> result = new ArrayList<>();
        parameterStr = parameterStr.substring(1, parameterStr.length() - 1);
        while (parameterStr.length() > 0) {
            int left = parameterStr.indexOf("(");
            int comma = parameterStr.indexOf(",");
            if (left >= 0 && left < comma) {
                int right = matchRightParenthesis(parameterStr);
                result.add(parameterStr.substring(0, right + 1));
                comma = parameterStr.indexOf(",", right);
            } else {
                if (comma >= 0) {
                    result.add(parameterStr.substring(0, comma));
                } else {
                    result.add(parameterStr);
                }
            }

            if (comma >= 0) {
                parameterStr = parameterStr.substring(comma + 1);
            } else {
                parameterStr = parameterStr.substring(parameterStr.length());
            }
        }

        return result;
    }

    private static int matchRightParenthesis(String parameterStr) {
        int left = parameterStr.indexOf("(");
        if (left < 0) {
            logger.error("Expression: [{}] is not start with '('.", parameterStr);
            return left;
        }

        char[] characters = parameterStr.substring(left).toCharArray();
        Stack<Integer> stack = new Stack<>();
        for (int i=0; i<characters.length; i++) {
            char character = characters[i];
            if ('(' == character) {
                stack.push(i);
            } else if (')' == character) {
                stack.pop();
            }

            if (stack.isEmpty()) {
                return left + i;
            }
        }
        return left;
    }

}
