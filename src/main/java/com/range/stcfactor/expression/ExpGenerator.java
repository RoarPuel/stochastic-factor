package com.range.stcfactor.expression;

import com.range.stcfactor.common.Constant;
import com.range.stcfactor.common.utils.FileUtils;
import com.range.stcfactor.common.utils.RandomsUtils;
import com.range.stcfactor.expression.constant.ExpMode;
import com.range.stcfactor.expression.constant.ExpPrintFormat;
import com.range.stcfactor.expression.constant.ExpVariables;
import com.range.stcfactor.expression.tree.ExpTree;
import com.range.stcfactor.expression.tree.ExpTreeFactory;
import com.range.stcfactor.expression.tree.ExpModel;
import org.apache.commons.lang3.StringUtils;
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
import java.util.function.Function;

/**
 * 表达式生成器
 *
 * @author zrj5865@163.com
 * @create 2019-07-22
 */
public class ExpGenerator {

    private static final Logger logger = LogManager.getLogger(ExpGenerator.class);

    private static final String EXISTED_EXP = "expression.csv";

    private ExpMode mode;
    private int depthMin;
    private int depthMax;
    private String dataFilePath;
    private ExpPrintFormat format;
    private ExpTreeFactory factory;

    public ExpGenerator(Properties config) {
        this.mode = ExpMode.valueOf(config.getProperty(Constant.EXP_MODE, Constant.DEFAULT_EXP_MODE));
        this.depthMin = Integer.parseInt(config.getProperty(Constant.EXP_DEPTH_MIN, Constant.DEFAULT_EXP_DEPTH_MIN));
        this.depthMax = Integer.parseInt(config.getProperty(Constant.EXP_DEPTH_MAX, Constant.DEFAULT_EXP_DEPTH_MAX));
        this.dataFilePath = config.getProperty(Constant.DATA_FILE_PATH, Constant.DEFAULT_DATA_FILE_PATH);
        this.format = ExpPrintFormat.valueOf(config.getProperty(Constant.EXP_PRINT_FORMAT, Constant.DEFAULT_EXP_PRINT_FORMAT).toUpperCase());

        this.factory = new ExpTreeFactory(initModels(), initFunctions(), initVariables(),
                                            initConstants(), initWeights(), this.format);
    }

    /**
     * 初始化获取所有自定义函数、变量
     * @return 自定义函数、变量列表
     */
    private Map<Class, List<ExpModel>> initModels() {
        Map<Class, List<ExpModel>> models = new HashMap<>(new HashMap<>());
        obtainFunctions(models);
        obtainVariables(models);
        logger.info("Success obtain models, size: {}.", printLog(models));
        return models;
    }

    /**
     * 初始化获取所有自定义函数
     * @return 自定义函数列表
     */
    private Map<Class, List<ExpModel>> initFunctions() {
        Map<Class, List<ExpModel>> functions = obtainFunctions(new HashMap<>());
        logger.info("Success obtain functions, size: {}.", printLog(functions));
        return functions;
    }

    /**
     * 初始化获取所有变量
     * @return 变量列表
     */
    private Map<Class, List<ExpModel>> initVariables() {
        Map<Class, List<ExpModel>> variables = obtainVariables(new HashMap<>());
        logger.info("Success obtain variables, size: {}.", printLog(variables));
        return variables;
    }

    private Map<Class, List<ExpModel>> obtainFunctions(Map<Class, List<ExpModel>> functions) {
        for (Method method : ExpFunctions.class.getDeclaredMethods()) {
            if (!Modifier.isPublic(method.getModifiers())) {
                continue;
            }
            putMap(functions, method.getReturnType(), new ExpModel(method));
        }
        return functions;
    }

    private Map<Class, List<ExpModel>> obtainVariables(Map<Class, List<ExpModel>> variables) {
        for (ExpVariables var : ExpVariables.values()) {
            putMap(variables, var.getType(), new ExpModel(var));
        }
        return variables;
    }

    private void putMap(Map<Class, List<ExpModel>> map, Class key, ExpModel value) {
        List<ExpModel> list = map.computeIfAbsent(key, k -> new ArrayList<>());
        list.add(value);
    }

    private String printLog(Map<Class, List<ExpModel>> map) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<Class, List<ExpModel>> entry : map.entrySet()) {
            sb.append(entry.getKey().getSimpleName());
            sb.append(":");
            sb.append(entry.getValue().size());
            sb.append(", ");
        }
        return sb.substring(0, sb.length() - 2);
    }

    /**
     * 初始化自定义函数常量
     * @return 自定义函数常量
     */
    private Map<String, Function<Integer, ExpModel>> initConstants() {
        Map<String, Function<Integer, ExpModel>> constants = new HashMap<>();
        constants.put("upper", index -> newExpModel(index, 2, RandomsUtils.getRandomInt(1, 5)));
        constants.put("lower", index -> newExpModel(index, 2, RandomsUtils.getRandomInt(1, 5)));
        constants.put("sumRatio", index -> newExpModel(index, 2, RandomsUtils.getRandomDouble(0.0, 1.0)));
        constants.put("wavg", index -> newExpModel(index, 2, RandomsUtils.getRandomDouble(0.0, 1.0)));
        constants.put("ite", index -> newExpModel(index, 2, RandomsUtils.getRandomDouble(0, 254.0 * 2) - 254.0));
        constants.put("ter", index -> newExpModel(index, 2, RandomsUtils.getRandomDouble(0, 254.0 * 2) - 254.0));
        constants.put("avg", index -> newExpModel(index, 0, RandomsUtils.getRandomInt(2, 5)));
        constants.put("gavg", index -> newExpModel(index, 0, RandomsUtils.getRandomInt(2, 5)));
        return constants;
    }

    private ExpModel newExpModel(int index, int ind, Object data) {
        int[] indexes = new int[]{ ind };
        Object[] datas = new Object[]{ data };
        return newExpModel(index, indexes, datas);
    }

    private ExpModel newExpModel(int index, int[] indexes, Object[] datas) {
        ExpModel result = new ExpModel(RandomsUtils.getRandomInt(2, 243));
        for (int i=0; i<indexes.length; i++) {
            if (index == indexes[i]) {
                result = new ExpModel(datas[i]);
                break;
            }
        }
        return result;
    }

    /**
     * 初始化自定义函数权重
     * @return 自定义函数权重
     */
    private Map<ExpModel, Double> initWeights() {
        // TODO 权重
        Map<ExpModel, Double> weights = new HashMap<>();
        logger.info("Success obtain weights size: {}.", weights.size());
        return weights;
    }

    public Set<ExpTree> obtainExpression(int total) {
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
        String expFilepath = StringUtils.joinWith("/", this.dataFilePath, EXISTED_EXP);
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
