package expression;

import com.range.stcfactor.expression.ExpPrintFormat;
import com.range.stcfactor.expression.ExpResolver;
import com.range.stcfactor.expression.tree.ExpTree;

/**
 * @author zrj5865@163.com
 * @create 2019-12-12
 */
public class ResolverTest {

    public static void main(String[] args) {
        String expStr = "(((tsSum(share, day_num) / share) * ((open - low) * vol)) - (close + tsSum(std(close, day_num), day_num)))";
        ExpTree expression = ExpResolver.analysis(expStr, ExpPrintFormat.UPPER);
        System.out.println(expression);
        System.out.println(expression.getDepth());
    }

}
