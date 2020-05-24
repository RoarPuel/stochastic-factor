package expression;

import com.range.stcfactor.common.utils.FileUtils;
import com.range.stcfactor.expression.ExpFunctions;
import com.range.stcfactor.expression.constant.ExpVariables;
import com.range.stcfactor.signal.data.DataModel;
import org.junit.Test;
import org.nd4j.linalg.api.ndarray.INDArray;

/**
 * @author zrj5865@163.com
 * @create 2019-12-09
 */
public class ExpressionTest {

    private String dataPath = "D:\\Work\\Project\\Java\\stochastic-factor\\data\\";

    @Test
    public void test() {
        DataModel dataModel = initDataModel();
        ExpFunctions functions = new ExpFunctions(dataModel);
        INDArray open = (INDArray) dataModel.getData(ExpVariables.OPEN);
        INDArray high = (INDArray) dataModel.getData(ExpVariables.HIGH);
        INDArray low = (INDArray) dataModel.getData(ExpVariables.LOW);
        INDArray close = (INDArray) dataModel.getData(ExpVariables.CLOSE);
        INDArray vol = (INDArray) dataModel.getData(ExpVariables.VOL);
        INDArray share = (INDArray) dataModel.getData(ExpVariables.SHARE);
        INDArray turnover = (INDArray) dataModel.getData(ExpVariables.TURNOVER);

        long startTime = System.currentTimeMillis();
        System.out.println(functions.mul(open, close));
        System.out.println("==================================================================================> cost "
                + (System.currentTimeMillis() - startTime) + "ms");
    }

    private DataModel initDataModel() {
        DataModel dataModel = new DataModel();
        for (ExpVariables var : ExpVariables.values()) {
            dataModel.putData(var, FileUtils.readData(dataPath + var.name().toLowerCase() + ".csv"));
        }
        return dataModel;
    }

}
