package com.range.stcfactor.expression;

import com.range.stcfactor.common.Constant;
import com.range.stcfactor.common.utils.FileUtils;
import com.range.stcfactor.expression.tree.ExpTree;
import com.range.stcfactor.expression.tree.ExpTreeFactory;
import com.range.stcfactor.expression.tree.ExpModel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * 表达式生成器
 *
 * @author zrj5865@163.com
 * @create 2019-07-22
 */
public class ExpGenerator {

    private static final Logger logger = LogManager.getLogger(ExpGenerator.class);

    private static final String EXISTED_EXP = "expression";

    private ExpMode mode;
    private int total;
    private int depthMin;
    private int depthMax;
    private ExpPrintFormat format;

    private String dataFilePath;

    private ExpTreeFactory factory;

    public ExpGenerator(Properties config) {
        this.mode = ExpMode.valueOf(config.getProperty(Constant.EXP_MODE, Constant.DEFAULT_EXP_MODE));
        this.total = Integer.parseInt(config.getProperty(Constant.EXP_TOTAL, Constant.DEFAULT_EXP_TOTAL));
        this.depthMin = Integer.parseInt(config.getProperty(Constant.EXP_DEPTH_MIN, Constant.DEFAULT_EXP_DEPTH_MIN));
        this.depthMax = Integer.parseInt(config.getProperty(Constant.EXP_DEPTH_MAX, Constant.DEFAULT_EXP_DEPTH_MAX));
        this.dataFilePath = config.getProperty(Constant.DATA_FILE_PATH, Constant.DEFAULT_DATA_FILE_PATH);
        this.format = ExpPrintFormat.valueOf(config.getProperty(Constant.EXP_PRINT_FORMAT, Constant.DEFAULT_EXP_PRINT_FORMAT).toUpperCase());
        this.factory = new ExpTreeFactory(initModels(),
                                        initFunctions(),
                                        initVariables(),
                                        initWeights(),
                                        this.format);
    }

    /**
     * 初始化获取所有自定义函数、变量
     * @return 自定义函数、变量列表
     */
    private Map<Class, List<ExpModel>> initModels() {
        Map<Class, List<ExpModel>> models = new HashMap<>();

        Method[] methods = ExpFunctions.class.getDeclaredMethods();
        for (Method method : methods) {
            if (!Modifier.isPublic(method.getModifiers())) {
                continue;
            }
            Class returnType = method.getReturnType();
            ExpModel expModel = new ExpModel(method.getName(), method.getParameterTypes(), returnType);
            putMap(models, returnType, expModel);
        }
        for (ExpVariables var : ExpVariables.values()) {
            Class returnType = var.getType();
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
            if (!Modifier.isPublic(method.getModifiers())) {
                continue;
            }
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
            Class returnType = var.getType();
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

    public Set<ExpTree> obtainExpression() {
        Set<ExpTree> trees = new HashSet<>();
        switch (mode) {
            case auto:
                trees = generateRandomExpression(total, depthMin, depthMax);
                break;
            case existed:
                trees = getExistedExpression();
                break;
            default:
                logger.error("Unknown expression generator mode: {}.", mode);
        }
        return trees;
    }

    private Set<ExpTree> getExistedExpression() {
        String expFilepath = MessageFormat.format(this.dataFilePath, EXISTED_EXP);
        List<String[]> expStrs = FileUtils.readCsv(expFilepath, '|', 0);
        Set<ExpTree> exps = new HashSet<>(expStrs.size());
        for (String[] expStr : expStrs) {
            exps.add(ExpResolver.analysis(expStr[0], this.format));
        }
        return exps;
    }

    /**
     * 生成随机公式
     * @param total 公式数量
     * @param depthMin 随机公式树最大深度
     * @param depthMax 随机公式树最小深度
     * @return 公式树集合
     */
    private Set<ExpTree> generateRandomExpression(int total, int depthMin, int depthMax) {
        Set<ExpTree> exps = new HashSet<>(total);
        logger.info("Start build expression tree. total: {}, depthMin: {}, depthMax: {}.", total, depthMin, depthMax);
        for (int i=0; i<total; i++) {
            exps.add(factory.buildRandom(depthMin, depthMax, Constant.DEFAULT_TYPE));
        }
        return exps;
    }

}
