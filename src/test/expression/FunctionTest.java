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
public class FunctionTest {

    private String dataPath = "D:\\Work\\Project\\Java\\stochastic-factor\\data\\";

    @Test
    public void test() {
        DataModel dataModel = initDataModel();
        ExpFunctions functions = new ExpFunctions(dataModel);

//        INDArray open = Nd4j.create(new double[][]{{Double.NaN,-2.0,3.0},{4.0,Double.NaN,-6.0},{4.0,-8.0,Double.NaN},{12.0,12.0,25.0}});
//        INDArray close = Nd4j.create(new double[][]{{4.0,-2.0,33.0},{24.0,2.0,-6.0},{-7.0,18.0,25.0},{12.0,58.0,32.0}});
        INDArray open = (INDArray) dataModel.getData(ExpVariables.OPEN);
        INDArray close = (INDArray) dataModel.getData(ExpVariables.CLOSE);

        System.out.println();
        System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
        System.out.println("open: " + open);
        System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
        System.out.println("close: " + close);
        System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
        System.out.println();

        long startTime = System.currentTimeMillis();

        // =========================== 1 array =========================== //
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
        // =========================== 1 array =========================== //

        // =========================== 2 array =========================== //
//        System.out.println("add: " + functions.add(open, close));
//        System.out.println("sub: " + functions.sub(open, close));
//        System.out.println("mul: " + functions.mul(open, close));
//        System.out.println("div: " + functions.div(open, close));
//        System.out.println("diff: " + functions.diff(open, close));
//        System.out.println("min: " + functions.min(open, close));
//        System.out.println("max: " + functions.max(open, close));
//        System.out.println("signedPower: " + functions.signedPower(open, close));
//        System.out.println("fPass: " + functions.fPass(open, close));
//        System.out.println("kelly: " + functions.kelly(open, close));
        // =========================== 2 array =========================== //

        // =========================== n array =========================== //
//        System.out.println("avg: " + functions.avg(open, close));
//        System.out.println("gavg: " + functions.gavg(open, close));
        // =========================== n array =========================== //

        // =========================== 1 array + 1 num =========================== //
//        System.out.println("std: " + functions.std(open, 2));
//        System.out.println("mean: " + functions.mean(open, 2));
//        System.out.println("prod: " + functions.prod(open, 2));
//        System.out.println("tsSum: " + functions.tsSum(open, 2));
//        System.out.println("tsMin: " + functions.tsMin(open, 2));
//        System.out.println("tsMax: " + functions.tsMax(open, 2));
//        System.out.println("tsRank: " + functions.tsRank(open, 2));
//        System.out.println("delay: " + functions.delay(open, 2));
//        System.out.println("delta: " + functions.delta(open, 2));
//        System.out.println("decayLinear: " + functions.decayLinear(open, 2));
//        System.out.println("count: " + functions.count(open, 2));
//        System.out.println("highDay: " + functions.highDay(open, 2));
//        System.out.println("lowDay: " + functions.lowDay(open, 2));
//        System.out.println("ret: " + functions.ret(open, 2));
//        System.out.println("retL: " + functions.retL(open, 2));
//        System.out.println("roc: " + functions.roc(open, 2));
//        System.out.println("ma: " + functions.ma(open, 2));
//        System.out.println("wma: " + functions.wma(open, 2));
//        System.out.println("sumSign: " + functions.sumSign(open, 2));
//        System.out.println("sum: " + functions.sum(open, 2));
//        System.out.println("bias: " + functions.bias(open, 2));
//        System.out.println("cv: " + functions.cv(open, 2));
//        System.out.println("cv2: " + functions.cv2(open, 2));
//        System.out.println("mcs: " + functions.mcs(open, 2));
//        System.out.println("rs: " + functions.rs(open, 2));
//        System.out.println("rsi: " + functions.rsi(open, 2));
//        System.out.println("tsRng: " + functions.tsRng(open, 2));
//        System.out.println("scale: " + functions.scale(open, 2));
//        System.out.println("ema: " + functions.ema(open, 2));
//        System.out.println("meanS: " + functions.divS(open, 2));
//        System.out.println("divS: " + functions.divS(open, 2));
//        System.out.println("subS: " + functions.subS(open, 2));
//        System.out.println("meanC: " + functions.meanC(open, 2));
//        System.out.println("stdC: " + functions.stdC(open, 2));
//        System.out.println("zScore: " + functions.zScore(open, 2));
//        System.out.println("zScoreC: " + functions.zScoreC(open, 2));
        // =========================== 1 array + 1 num =========================== //

        // =========================== 1 array + 2 num =========================== //
//        System.out.println("sma: " + functions.sma(open, 3, 2));
//        System.out.println("upper: " + functions.upper(open, 3, 2));
//        System.out.println("lower: " + functions.lower(open, 3, 2));
//        System.out.println("odds: " + functions.odds(open, 3, 2));
//        System.out.println("sumRatio: " + functions.sumRatio(open, 3, 2.0));
        // =========================== 1 array + 2 num =========================== //

        // =========================== 1 array + 3 num =========================== //
//        System.out.println("macd: " + functions.macd(open, 3, 2, 1));
        // =========================== 1 array + 3 num =========================== //

        // =========================== 2 array + 1 num =========================== //
//        System.out.println("cov: " + functions.cov(open, close, 2));
//        System.out.println("corr: " + functions.corr(open, close, 2));
//        System.out.println("crr: " + functions.crr(open, close, 2));
//        System.out.println("regBeta: " + functions.regBeta(open, close, 2));
//        System.out.println("regResi: " + functions.regResi(open, close, 2));
//        System.out.println("sumIf: " + functions.sumIf(open, 2, close));
//        System.out.println("wavg: " + functions.wavg(open, close, 2.0));
//        System.out.println("ite: " + functions.ite(open, close, 2.0));
//        System.out.println("ter: " + functions.ter(open, close, 2.0));
        // =========================== 2 array + 1 num =========================== //

        // =========================== none =========================== //
//        System.out.println("retGap: " + functions.retGap());
//        System.out.println("retIntra: " + functions.retIntra());
//        System.out.println("ad: " + functions.ad());
//        System.out.println("avgp: " + functions.avgp());
//        System.out.println("mf: " + functions.mf());
//        System.out.println("mfm: " + functions.mfm());
//        System.out.println("mfv: " + functions.mfv());
//        System.out.println("dif: " + functions.dif());
//        System.out.println("dbm: " + functions.dbm());
//        System.out.println("dtm: " + functions.dtm());
        // =========================== none =========================== //

        // =========================== 1 num =========================== //
//        System.out.println("adtm: " + functions.adtm(2));
//        System.out.println("adx: " + functions.adx(2));
//        System.out.println("ar: " + functions.ar(2));
//        System.out.println("aroonUp: " + functions.aroonUp(2));
//        System.out.println("aroonDown: " + functions.aroonDown(2));
//        System.out.println("aroonOsc: " + functions.aroonOsc(2));
//        System.out.println("asi: " + functions.asi(2));
//        System.out.println("tr: " + functions.tr(2));
//        System.out.println("atr: " + functions.atr(2));
//        System.out.println("str: " + functions.str(2));
//        System.out.println("bp: " + functions.bp(2));
//        System.out.println("br: " + functions.br(2));
//        System.out.println("cci: " + functions.cci(2));
//        System.out.println("cmo: " + functions.cmo(2));
//        System.out.println("cr: " + functions.cr(2));
//        System.out.println("ddi: " + functions.ddi(2));
//        System.out.println("dmm: " + functions.dmm(2));
//        System.out.println("dmm2: " + functions.dmm2(2));
//        System.out.println("dmp: " + functions.dmp(2));
//        System.out.println("dmp2: " + functions.dmp2(2));
//        System.out.println("eom: " + functions.eom(2));
//        System.out.println("er: " + functions.er(2));
//        System.out.println("mdi: " + functions.mdi(2));
//        System.out.println("mdi2: " + functions.mdi2(2));
//        System.out.println("mfi: " + functions.mfi(2));
//        System.out.println("mfi2: " + functions.mfi2(2));
//        System.out.println("obv: " + functions.obv(2));
//        System.out.println("pdi: " + functions.pdi(2));
//        System.out.println("pdi2: " + functions.pdi2(2));
//        System.out.println("psy: " + functions.psy(2));
//        System.out.println("nvi: " + functions.nvi(2));
//        System.out.println("pvi: " + functions.pvi(2));
//        System.out.println("rvi: " + functions.rvi(2));
//        System.out.println("stochK: " + functions.stochK(2));
//        System.out.println("stochD: " + functions.stochD(2));
//        System.out.println("stochRsi: " + functions.stochRsi(2));
//        System.out.println("trix: " + functions.trix(2));
//        System.out.println("tsRngP: " + functions.tsRngP(2));
//        System.out.println("ulcer: " + functions.ulcer(2));
//        System.out.println("uo: " + functions.uo(2));
//        System.out.println("vrsi: " + functions.vrsi(2));
//        System.out.println("vtxMinus: " + functions.vtxMinus(2));
//        System.out.println("vtxPlus: " + functions.vtxPlus(2));
//        System.out.println("volatility: " + functions.volatility(2));
//        System.out.println("williams: " + functions.williams(2));
        // =========================== 1 num =========================== //

        // =========================== 2 num =========================== //
//        System.out.println("ppo: " + functions.ppo(3, 2));
//        System.out.println("pvo: " + functions.pvo(3, 2));
//        System.out.println("tsi: " + functions.tsi(3, 2));
        // =========================== 2 num =========================== //

        // =========================== 3 num =========================== //
//        System.out.println("copPock: " + functions.copPock(3, 2, 1));
//        System.out.println("dbcd: " + functions.dbcd(3, 2, 1));
//        System.out.println("kdjK: " + functions.kdjK(3, 2, 1));
//        System.out.println("kdjD: " + functions.kdjD(3, 2, 1));
//        System.out.println("kdjJ: " + functions.kdjJ(3, 2, 1));
        // =========================== 3 num =========================== //

        // =========================== 4 num =========================== //
//        System.out.println("bbi: " + functions.bbi(4, 3, 2, 1));
        // =========================== 4 num =========================== //

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
