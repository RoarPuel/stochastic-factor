package com.range.stcfactor.expression;

import com.range.stcfactor.ConfigConstant;
import com.range.stcfactor.expression.tree.ExpTree;
import com.range.stcfactor.expression.tree.ExpTreeFactory;
import com.range.stcfactor.expression.tree.FunctionInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Method;
import java.util.*;

/**
 * 表达式生成器
 *
 * @author zrj5865@163.com
 * @create 2019-07-22
 */
public class ExpGenerator {

    private static final Logger logger = LogManager.getLogger(ExpGenerator.class);

    private Properties config;
    private ExpTreeFactory factory;

    public ExpGenerator(Properties config) {
        this.config = config;
        factory = new ExpTreeFactory(initFunctions(), initVariables(), initWeights());
    }

    /**
     * 初始化获取所有自定义函数
     * @return 自定义函数列表
     */
    private Map<Class, List<FunctionInfo>> initFunctions() {
        Map<Class, List<FunctionInfo>> functions = new HashMap<>();
        Method[] methods = ExpFunctions.class.getDeclaredMethods();
        for (Method method : methods) {
            Class type = method.getReturnType();
            FunctionInfo functionInfo = new FunctionInfo(method.getName(), method.getParameterTypes(), type);

            List<FunctionInfo> list = functions.get(type);
            if (list == null) {
                list = new ArrayList<>();
                list.add(functionInfo);
                functions.put(type, list);
            } else {
                list.add(functionInfo);
            }
        }
        logger.info("Success obtain functions size: {}.", functions.size());
        return functions;
    }

    /**
     * 初始化获取所有变量名
     * @return 变量名列表
     */
    private List<ExpVariables> initVariables() {
        return Arrays.asList(ExpVariables.values());
    }

    /**
     * 初始化自定义函数权重
     * @return 自定义函数权重
     */
    private Map<FunctionInfo, Double> initWeights() {
        Map<FunctionInfo, Double> weights = new HashMap<>();
        logger.info("Success obtain weights size: {}.", weights.size());
        return weights;
    }

    public Set<ExpTree> generateRandomExpression() {
        int total = Integer.parseInt(config.getProperty(ConfigConstant.EXP_TOTAL, "10"));
        int depthMin = Integer.parseInt(config.getProperty(ConfigConstant.EXP_DEPTH_MIN, "2"));
        int depthMax = Integer.parseInt(config.getProperty(ConfigConstant.EXP_DEPTH_MAX, "3"));
        return generateRandomExpression(total, depthMin, depthMax);
    }

    /**
     * 生成随机公式
     * @param total 公式数量
     * @param depthMin 随机公式树最大深度
     * @param depthMax 随机公式树最小深度
     * @return 公式树集合
     */
    public Set<ExpTree> generateRandomExpression(int total, int depthMin, int depthMax) {
        Set<ExpTree> exps = new HashSet<>(total);
        logger.info("Start build expression tree. total: {}, depthMin: {}, depthMax: {}.", total, depthMin, depthMax);
        for (int i=0; i<total; i++) {
            exps.add(factory.buildRandom(depthMin, depthMax));
        }
        return exps;
    }

}
