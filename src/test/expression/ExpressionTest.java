package expression;

import com.range.stcfactor.common.utils.FileUtils;
import com.range.stcfactor.expression.ExpFunctions;
import org.nd4j.linalg.api.ndarray.INDArray;

/**
 * @author zrj5865@163.com
 * @create 2019-12-09
 */
public class ExpressionTest {

    public static void main(String[] args) {
        String dataPath = "D:\\Work\\Project\\Java\\stochastic-factor\\data\\";
        test(dataPath);
    }

    private static void test(String path) {
        ExpFunctions functions = new ExpFunctions();

        INDArray open = FileUtils.readData(path + "open.csv");
        INDArray high = FileUtils.readData(path + "high.csv");
        INDArray low = FileUtils.readData(path + "low.csv");
        INDArray close = FileUtils.readData(path + "close.csv");
        INDArray vol = FileUtils.readData(path + "vol.csv");
        INDArray share = FileUtils.readData(path + "share.csv");
        INDArray turnover = FileUtils.readData(path + "turnover.csv");

        long startTime = System.currentTimeMillis();
        System.out.println(functions.mul(open,close));
        System.out.println("==================================================================================> cost "
                + (System.currentTimeMillis() - startTime) + "ms");
    }

}
