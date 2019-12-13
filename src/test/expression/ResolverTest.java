package expression;

import com.range.stcfactor.expression.ExpResolver;
import com.range.stcfactor.expression.tree.ExpTree;

/**
 * @author zrj5865@163.com
 * @create 2019-12-12
 */
public class ResolverTest {

    public static void main(String[] args) {
        String expStr = "regResi(square(corr(open,high,102)),corr(close,rankPct(turnover),206),128)";
        ExpTree expression = ExpResolver.analysis(expStr);
        System.out.println(expression);
        System.out.println(expression.getDepth());
    }

}
