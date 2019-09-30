package com.range.stcfactor.signal;

import com.opencsv.CSVReader;
import com.range.stcfactor.common.Constant;
import com.range.stcfactor.expression.ExpVariables;
import com.range.stcfactor.expression.tree.ExpTree;
import com.range.stcfactor.signal.data.DataBean;
import com.range.stcfactor.signal.data.DataFactory;
import com.range.stcfactor.signal.data.DataModel;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.nd4j.linalg.api.buffer.DataType;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import java.io.FileReader;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 信号生成
 *
 * @author renjie.zhu@woqutech.com
 * @create 2019-08-06
 */
public class SignalGenerator {

    private static final Logger logger = LogManager.getLogger(SignalGenerator.class);

    private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private DecimalFormat decimalFormat = new DecimalFormat("#.00");

    private Properties config;
    private ExecutorService threadPool;
    private DataFactory factory;

    public SignalGenerator(Properties config) {
        this.config = config;
        this.threadPool = Executors.newFixedThreadPool(20);

        this.factory = new DataFactory(initData());
    }

    private DataModel initData() {
        DataModel dataModel = new DataModel();
        for (ExpVariables var : ExpVariables.values()) {
            Object data;
            if (ExpVariables.day_num == var) {
                data = Integer.valueOf(config.getProperty(Constant.DATA_DATE_NUM, "1"));
            } else {
                data = readCsv(var, config.getProperty(Constant.DATA_FILE_PATH)).getData();
            }
            dataModel.putData(var, data);
        }
        return dataModel;
    }

    private DataBean readCsv(ExpVariables type, String path) {
        String filepath = MessageFormat.format(path, type.name());
        List<String> header = new ArrayList<>();
        List<String[]> lines = new ArrayList<>();
        try {
            CSVReader csv = new CSVReader(new FileReader(filepath));
            String[] head = csv.readNext();
            header = Arrays.asList(Arrays.copyOfRange(head, 1, head.length));
            lines = csv.readAll();
            logger.info("---> read [{}] csv success, start loading data.", type.name());
        } catch (Exception e) {
            logger.error("read csv from [{}] error.", filepath, e);
        }

        List<Date> index = new ArrayList<>();
        INDArray data = Nd4j.create(DataType.DOUBLE, lines.size(), header.size());
        long start = System.currentTimeMillis();
        for (int i=0; i<lines.size(); i++) {
            String[] line = lines.get(i);
            index.add(parseStrToDate(line[0]));

            List<Double> items = new ArrayList<>();
            for (int j=1; j<line.length; j++) {
                String item = line[j];
                if (StringUtils.isEmpty(item)) {
                    items.add(Double.NaN);
                } else {
                    items.add(Double.parseDouble(item));
                }
            }
            data.putRow(i, Nd4j.create(items));

            if (System.currentTimeMillis() - start > 1000 || i == lines.size() - 1) {
                logger.info("loading...... {}%", decimalFormat.format((double) (i + 1) / lines.size() * 100));
                start = System.currentTimeMillis();
            }
        }

        DataBean bean = new DataBean();
        bean.setType(type);
        bean.setHeader(header);
        bean.setIndex(index);
        bean.setData(data);
        logger.info("---> load [{}] data from [{}] success.", type.name(), filepath);
        return bean;
    }

    private Date parseStrToDate(String date) {
        try {
            return dateFormat.parse(date);
        } catch (Exception e) {
            logger.error("Parse date error.", e);
            return null;
        }
    }

    private String parseDateToStr(Date date) {
        return dateFormat.format(date);
    }

    public void startTask(ExpTree expTree) {
        SignalTask signalTask = new SignalTask(expTree, factory);
        threadPool.submit(signalTask);
    }

}
