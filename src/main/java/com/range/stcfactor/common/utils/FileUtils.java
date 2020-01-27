package com.range.stcfactor.common.utils;

import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.CSVWriter;
import com.opencsv.CSVWriterBuilder;
import com.opencsv.ICSVWriter;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.nd4j.linalg.api.buffer.DataType;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * @author zrj5865@163.com
 * @create 2019-11-28
 */
public class FileUtils {

    private static final Logger logger = LogManager.getLogger(FileUtils.class);

    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private static final DecimalFormat DOUBLE_DECIMAL_FORMAT = new DecimalFormat("#0.00");

    public static List<String[]> readCsv(String filepath, char separator, int skipLines) {
        List<String[]> lines = new ArrayList<>();
        try (CSVReader csv = new CSVReaderBuilder(new FileReader(filepath))
                .withCSVParser(new CSVParserBuilder().withSeparator(separator).build())
                .withSkipLines(skipLines).build()) {
            lines = csv.readAll();
        } catch (Exception e) {
            logger.error("Read csv from [{}] error.", filepath);
        }

        return lines;
    }

    public static void writeCsvAppend(String filepath, char separator, String[] data) {
        writeCsv(filepath, separator, Collections.singletonList(data), true);
    }

    public static void writeCsvAppend(String filepath, char separator, List<String[]> data) {
        writeCsv(filepath, separator, data, true);
    }

    public static void writeCsvNew(String filepath, char separator, String[] data) {
        writeCsv(filepath, separator, Collections.singletonList(data), false);
    }

    public static void writeCsvNew(String filepath, char separator, List<String[]> data) {
        writeCsv(filepath, separator, data, false);
    }

    private static void writeCsv(String filepath, char separator, List<String[]> data, boolean append) {
        File file = new File(filepath);
        if (!file.exists() && append) {
            logger.error("File: [{}] is not existed.", filepath);
            return;
        }

        try (ICSVWriter writer = new CSVWriterBuilder(new FileWriter(file, append))
                .withSeparator(separator)
                .withQuoteChar(CSVWriter.NO_QUOTE_CHARACTER)
                .build()) {
            writer.writeAll(data);
            writer.flush();
        } catch (Exception e) {
            logger.error("Write csv to [{}] error.", filepath);
        }
    }

    public static INDArray readData(String filepath) {
        return readData(filepath, new ArrayList<>(), new ArrayList<>());
    }

    public static INDArray readData(String filepath, boolean process) {
        return readData(filepath, new ArrayList<>(), new ArrayList<>(), ',', process);
    }

    public static INDArray readData(String filepath, List<String> headers, List<Date> indexes) {
        return readData(filepath, headers, indexes, ',', true);
    }

    public static INDArray readData(String filepath, List<String> headers, List<Date> indexes, char separator, boolean process) {
        List<String[]> lines = new ArrayList<>();
        try (CSVReader csv = new CSVReaderBuilder(new FileReader(filepath))
                .withCSVParser(new CSVParserBuilder().withSeparator(separator).build())
                .build()) {
            String[] head = csv.readNext();
            headers.addAll(Arrays.asList(Arrays.copyOfRange(head, 1, head.length)));
            lines = csv.readAll();
        } catch (Exception e) {
            logger.error("Read csv from [{}] error.", filepath, e);
        }

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

            if (process && (System.currentTimeMillis() - start > 1000 || i == lines.size() - 1)) {
                logger.info("........... loading ........... {}%",
                        DOUBLE_DECIMAL_FORMAT.format((double) (i + 1) / lines.size() * 100));
                start = System.currentTimeMillis();
            }
        }

        return data;
    }

    public static void writeData(String filepath, List<String> headers, List<Date> indexes, INDArray data) {
        writeData(filepath, headers, indexes, data, CSVWriter.DEFAULT_SEPARATOR);
    }

    public static void writeData(String filepath, List<String> headers, List<Date> indexes, INDArray data, char separator) {
        File file = new File(filepath);
        if (file.exists()) {
            logger.error("File: [{}] is existed.", filepath);
            return;
        }

        try (ICSVWriter writer = new CSVWriterBuilder(new FileWriter(file))
                .withSeparator(separator)
                .withQuoteChar(CSVWriter.NO_QUOTE_CHARACTER)
                .build()) {
            writer.writeNext(headers.toArray(new String[0]));

            for (int i=0; i<data.rows(); i++) {
                INDArray rowData = data.getRow(i);
                String[] rowStr = new String[rowData.columns() + 1];
                rowStr[0] = parseDateToStr(indexes.get(i));
                for (int j=0; j<rowData.columns(); j++) {
                    double num = rowData.getDouble(j);
                    rowStr[j + 1] = Double.isNaN(num) ? "" : String.valueOf(num);
                }
                writer.writeNext(rowStr);
            }
            writer.flush();
        } catch (Exception e) {
            logger.error("Write csv to [{}] error.", filepath, e);
        }
    }

    private static Date parseStrToDate(String date) {
        try {
            return DATE_FORMAT.parse(date);
        } catch (Exception e) {
            logger.error("Parse date error.", e);
            return null;
        }
    }

    private static String parseDateToStr(Date date) {
        return DATE_FORMAT.format(date);
    }

}
