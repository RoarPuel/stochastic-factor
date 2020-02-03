package expression;

import com.range.stcfactor.common.utils.FileUtils;
import com.range.stcfactor.expression.ExpFunctions;
import org.nd4j.linalg.api.ndarray.INDArray;

/**
 * @author zrj5865@163.com
 * @create 2019-12-09
 */
public class FunctionTest {

    public static void main(String[] args) {
        String dataPath = "D:\\Work\\Project\\Java\\stochastic-factor\\data\\";
        test(dataPath);
    }

    private static void test(String dataPath) {
        ExpFunctions functions = new ExpFunctions();

//        INDArray open = Nd4j.create(new double[][]{{Double.NaN,-2.0,3.0},{4.0,Double.NaN,-6.0},{4.0,-8.0,Double.NaN},{12.0,12.0,25.0}});
//        INDArray close = Nd4j.create(new double[][]{{4.0,-2.0,33.0},{24.0,2.0,-6.0},{-7.0,18.0,25.0},{12.0,58.0,32.0}});
        INDArray open = FileUtils.readData(dataPath + "open.csv");
        INDArray close = FileUtils.readData(dataPath + "close.csv");

        System.out.println();
        System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
        System.out.println("open: " + open);
        System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
        System.out.println("close: " + close);
        System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
        System.out.println();

        long startTime = System.currentTimeMillis();
//        System.out.println("sum: " + functions.sum(open, close));
//        System.out.println("sub: " + functions.sub(open, close));
//        System.out.println("mul: " + functions.mul(open, close));
//        System.out.println("div: " + functions.div(open, close));
//        System.out.println("tsSum: " + functions.tsSum(open, 2));
//        System.out.println("std: " + functions.std(open, 2));
//        System.out.println("mean: " + functions.mean(open, 2));
//        System.out.println("prod: " + functions.prod(open, 2));
//        System.out.println("tsMin: " + functions.tsMin(open, 2));
//        System.out.println("llv: " + functions.llv(open, 2));
//        System.out.println("tsMax: " + functions.tsMax(open, 2));
//        System.out.println("hhv: " + functions.hhv(open, 2));
//        System.out.println("tsRank: " + functions.tsRank(open, 2));
//        System.out.println("cov: " + functions.cov(open, close, 2));
//        System.out.println("corr: " + functions.corr(open, close, 2));
//        System.out.println("abs: " + functions.abs(open));
//        System.out.println("relu: " + functions.relu(open));
//        System.out.println("sigmoid: " + functions.sigmoid(open));
//        System.out.println("log: " + functions.log(open));
//        System.out.println("sqrt: " + functions.sqrt(open));
//        System.out.println("square: " + functions.square(open));
//        System.out.println("sign: " + functions.sign(open));
//        System.out.println("exp: " + functions.exp(open));
//        System.out.println("rank: " + functions.rank(open));
//        System.out.println("rankPct: " + functions.rankPct(open));
//        System.out.println("delay: " + functions.delay(open, 2));
//        System.out.println("delta: " + functions.delta(open, 2));
//        System.out.println("decayLinear: " + functions.decayLinear(open, 2));
//        System.out.println("min: " + functions.min(open, close));
//        System.out.println("max: " + functions.max(open, close));
//        System.out.println("count: " + functions.count(open, 2));
//        System.out.println("sumIf: " + functions.sumIf(open, 2));
//        System.out.println("sma: " + functions.sma(open, 3, 2));
//        System.out.println("highDay: " + functions.highDay(open, 2));
//        System.out.println("lowDay: " + functions.lowDay(open, 2));
//        System.out.println("ret: " + functions.ret(open, 2));
//        System.out.println("wma: " + functions.wma(open, 2));
//        System.out.println("regBeta: " + functions.regBeta(open, close, 2));
//        System.out.println("regResi: " + functions.regResi(open, close, 2));
        System.out.println("==================================================================================> cost "
                + (System.currentTimeMillis() - startTime) + "ms");
    }

}
