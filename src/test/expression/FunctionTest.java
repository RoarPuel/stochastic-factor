package expression;

import com.range.stcfactor.common.utils.FileUtils;
import org.nd4j.linalg.api.ndarray.INDArray;

import static com.range.stcfactor.expression.ExpFunctions.*;

/**
 * @author renjie.zhu@woqutech.com
 * @create 2019-12-09
 */
public class FunctionTest {

    public static void main(String[] args) {
        String dataPath = "D:\\Work\\Project\\Java\\stochastic-factor\\data\\";
        test(dataPath);
    }

    private static void test(String dataPath) {
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
//        System.out.println("sum: " + sum(open, close));
//        System.out.println("sub: " + sub(open, close));
//        System.out.println("mul: " + mul(open, close));
//        System.out.println("div: " + div(open, close));
//        System.out.println("tsSum: " + tsSum(open, 2));
//        System.out.println("std: " + std(open, 2));
//        System.out.println("mean: " + mean(open, 2));
//        System.out.println("prod: " + prod(open, 2));
//        System.out.println("tsMin: " + tsMin(open, 2));
//        System.out.println("llv: " + llv(open, 2));
//        System.out.println("tsMax: " + tsMax(open, 2));
//        System.out.println("hhv: " + hhv(open, 2));
//        System.out.println("tsRank: " + tsRank(open, 2));
//        System.out.println("cov: " + cov(open, close, 2));
//        System.out.println("corr: " + corr(open, close, 2));
//        System.out.println("abs: " + abs(open));
//        System.out.println("relu: " + relu(open));
//        System.out.println("sigmoid: " + sigmoid(open));
//        System.out.println("log: " + log(open));
//        System.out.println("sqrt: " + sqrt(open));
//        System.out.println("square: " + square(open));
//        System.out.println("sign: " + sign(open));
//        System.out.println("exp: " + exp(open));
//        System.out.println("rank: " + rank(open));
//        System.out.println("rankPct: " + rankPct(open));
//        System.out.println("delay: " + delay(open, 2));
//        System.out.println("delta: " + delta(open, 2));
//        System.out.println("decayLinear: " + decayLinear(open, 2));
//        System.out.println("min: " + min(open, close));
//        System.out.println("max: " + max(open, close));
//        System.out.println("count: " + count(open, 2));
//        System.out.println("sumIf: " + sumIf(open, 2));
//        System.out.println("sma: " + sma(open, 3, 2));
//        System.out.println("highDay: " + highDay(open, 2));
//        System.out.println("lowDay: " + lowDay(open, 2));
//        System.out.println("ret: " + ret(open, 2));
//        System.out.println("wma: " + wma(open, 2));
//        System.out.println("regBeta: " + regBeta(open, close, 2));
        System.out.println("regResi: " + regResi(open, close, 2));
        System.out.println("==================================================================================> cost "
                + (System.currentTimeMillis() - startTime) + "ms");
    }

}
