package com.range.stcfactor.common.utils;

import com.opencsv.CSVReader;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.nd4j.linalg.api.buffer.DataType;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import java.io.FileReader;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @author zrj5865@163.com
 * @create 2019-11-28
 */
public class FileUtils {

    private static final Logger logger = LogManager.getLogger(FileUtils.class);

    private static final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private static final DecimalFormat decimalFormat = new DecimalFormat("#.00");

    public static INDArray readCsv(String filepath) {
        return readCsv(filepath, null, null);
    }

    public static INDArray readCsv(String filepath, List<String> headers, List<Date> indexes) {
        List<String[]> lines = new ArrayList<>();
        try {
            CSVReader csv = new CSVReader(new FileReader(filepath));
            String[] head = csv.readNext();
            headers = Arrays.asList(Arrays.copyOfRange(head, 1, head.length));
            lines = csv.readAll();
        } catch (Exception e) {
            logger.error("read csv from [{}] error.", filepath, e);
        }

        indexes = new ArrayList<>();
        INDArray data = Nd4j.create(DataType.DOUBLE, lines.size(), headers.size());
        long start = System.currentTimeMillis();
        for (int i=0; i<lines.size(); i++) {
            String[] line = lines.get(i);
            indexes.add(parseStrToDate(line[0]));

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
                logger.info("----> loading...... {}%", decimalFormat.format((double) (i + 1) / lines.size() * 100));
                start = System.currentTimeMillis();
            }
        }

        return data;
    }

    private static Date parseStrToDate(String date) {
        try {
            return dateFormat.parse(date);
        } catch (Exception e) {
            logger.error("Parse date error.", e);
            return null;
        }
    }

    private static String parseDateToStr(Date date) {
        return dateFormat.format(date);
    }

}
