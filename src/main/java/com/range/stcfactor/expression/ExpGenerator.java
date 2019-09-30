package com.range.stcfactor.expression;

import com.range.stcfactor.common.Constant;
import com.range.stcfactor.expression.tree.ExpTree;
import com.range.stcfactor.expression.tree.ExpTreeFactory;
import com.range.stcfactor.expression.tree.ExpModel;
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
        factory = new ExpTreeFactory(initModels(),
                                    initFunctions(),
                                    initVariables(),
                                    initWeights());
    }

    /**
     * 初始化获取所有自定义函数、变量
     * @return 自定义函数、变量列表
     */
    private Map<Class, List<ExpModel>> initModels() {
        Map<Class, List<ExpModel>> models = new HashMap<>();

        Method[] methods = ExpFunctions.class.getDeclaredMethods();
        for (Method method : methods) {
            Class returnType = method.getReturnType();
            ExpModel expModel = new ExpModel(method.getName(), method.getParameterTypes(), returnType);
            putMap(models, returnType, expModel);
        }
        for (ExpVariables var : ExpVariables.values()) {
            Class returnType = var.getParameterType();
            ExpModel expModel = new ExpModel(var.name(), null, returnType);
            putMap(models, returnType, expModel);
        }

        logger.info("Success obtain infos, size: {}.", models.size());
        return models;
    }

    /**
     * 初始化获取所有自定义函数
     * @return 自定义函数列表
     */
    private Map<Class, List<ExpModel>> initFunctions() {
        Map<Class, List<ExpModel>> functions = new HashMap<>();

        Method[] methods = ExpFunctions.class.getDeclaredMethods();
        for (Method method : methods) {
            Class returnType = method.getReturnType();
            ExpModel expModel = new ExpModel(method.getName(), method.getParameterTypes(), returnType);
            putMap(functions, returnType, expModel);
        }

        logger.info("Success obtain functions, size: {}.", functions.size());
        return functions;
    }

    /**
     * 初始化获取所有变量
     * @return 变量列表
     */
    private Map<Class, List<ExpModel>> initVariables() {
        Map<Class, List<ExpModel>> variables = new HashMap<>();

        for (ExpVariables var : ExpVariables.values()) {
            Class returnType = var.getParameterType();
            ExpModel expModel = new ExpModel(var.name(), null, returnType);
            putMap(variables, returnType, expModel);
        }

        logger.info("Success obtain variables, size: {}.", variables.size());
        return variables;
    }

    private void putMap(Map<Class, List<ExpModel>> map, Class key, ExpModel value) {
        List<ExpModel> list = map.get(key);
        if (list == null) {
            list = new ArrayList<>();
            list.add(value);
            map.put(key, list);
        } else {
            list.add(value);
        }
    }

    /**
     * 初始化自定义函数权重
     * @return 自定义函数权重
     */
    private Map<ExpModel, Double> initWeights() {
        Map<ExpModel, Double> weights = new HashMap<>();
        logger.info("Success obtain weights size: {}.", weights.size());
        return weights;
    }

    public Set<ExpTree> generateRandomExpression() {
        int total = Integer.parseInt(config.getProperty(Constant.EXP_TOTAL, "10"));
        int depthMin = Integer.parseInt(config.getProperty(Constant.EXP_DEPTH_MIN, "2"));
        int depthMax = Integer.parseInt(config.getProperty(Constant.EXP_DEPTH_MAX, "3"));
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
            exps.add(factory.buildRandom(depthMin, depthMax, Constant.DEFAULT_TYPE));
        }
        return exps;
    }

}
