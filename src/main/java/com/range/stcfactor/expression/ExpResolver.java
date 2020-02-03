package com.range.stcfactor.expression;

import com.range.stcfactor.expression.tree.ExpModel;
import com.range.stcfactor.expression.tree.ExpTree;
import com.range.stcfactor.expression.tree.ExpTreeNode;
import org.apache.commons.collections4.map.CaseInsensitiveMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author zrj5865@163.com
 * @create 2019-12-12
 */
public class ExpResolver {

    private static final Logger logger = LogManager.getLogger(ExpResolver.class);

    private static Map<String, Method> methods;
    private static Map<String, String> symbols;
    private static Pattern pattern;

    static {
        methods = new CaseInsensitiveMap<>();
        for (Method method : ExpFunctions.class.getDeclaredMethods()) {
            if (!Modifier.isPublic(method.getModifiers())) {
                continue;
            }
            methods.put(method.getName(), method);
        }

        symbols = new CaseInsensitiveMap<>();
        for (ExpFunctionSymbol symbol : ExpFunctionSymbol.values()) {
            symbols.put(symbol.getSymbol(), symbol.name());
        }
        StringBuilder sb = new StringBuilder();
        symbols.keySet().forEach(s -> sb.append("\\").append(s));
        pattern = Pattern.compile(sb.insert(0, "[").append("]").toString());
    }

    public static ExpTree analysis(String expStr) {
        return analysis(expStr, ExpPrintFormat.DEFAULT);
    }

    public static ExpTree analysis(String expStr, ExpPrintFormat format) {
        expStr = replaceSymbol(expStr.replaceAll(" ", ""));
        return new ExpTree(resolveNode(expStr), format);
    }

    private static String replaceSymbol(String expStr) {
        Matcher matcher = pattern.matcher(expStr);
        if (matcher.find()) {
            StringBuilder sb = new StringBuilder(expStr);
            int symbolIndex = matcher.start();
            // 匹配左边公式
            int start;
            if (expStr.toCharArray()[symbolIndex - 1] == ')') {
                int left = matchLeftParenthesis(expStr.substring(0, symbolIndex));
                start = expStr.substring(0, left - 1).lastIndexOf('(');
            } else {
                start = expStr.substring(0, symbolIndex).lastIndexOf('(');
            }
            // 替换符号为函数
            String function = symbols.get(String.valueOf(expStr.toCharArray()[symbolIndex]));
            expStr = sb.replace(symbolIndex, symbolIndex + 1, ",")
                        .insert(start, function).toString();
            expStr = replaceSymbol(expStr);
        }
        return expStr;
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

    private static List<String> splitParameters(String parameterStr) {
        List<String> result = new ArrayList<>();
        // 左右括号截取
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

    private static int matchLeftParenthesis(String parameterStr) {
        int right = parameterStr.lastIndexOf(")");
        char[] characters = parameterStr.substring(0, right + 1).toCharArray();
        Stack<Integer> stack = new Stack<>();
        for (int i=characters.length-1; i>=0; i--) {
            char character = characters[i];
            if (')' == character) {
                stack.push(i);
            } else if ('(' == character) {
                stack.pop();
            }

            if (stack.isEmpty()) {
                return i;
            }
        }
        return right;
    }

    private static ExpModel getFunction(String functionName) {
        String methodName = null;
        Class[] parametersType = null;
        Class returnType = null;
        try {
            Method method = methods.get(functionName);
            methodName = method.getName();
            parametersType = method.getParameterTypes();
            returnType = method.getReturnType();
        } catch (Exception e) {
            logger.error("Get function: [{}] error.", functionName, e);
        }
        return new ExpModel(methodName, parametersType, returnType);
    }

    private static ExpModel getVariable(String variableName) {
        Class returnType;
        try {
            returnType = ExpVariables.valueOf(variableName.toUpperCase()).getType();
        } catch (Exception e) {
            returnType = ExpVariables.DAY_NUM.getType();
        }
        return new ExpModel(variableName, null, returnType);
    }

}
