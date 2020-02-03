package com.range.stcfactor.common.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author renjie.zhu@woqutech.com
 * @create 2020-02-04
 */
public class FormatUtils {

    private static final Logger logger = LogManager.getLogger(FormatUtils.class);

    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private static final DecimalFormat DOUBLE_DECIMAL_FORMAT = new DecimalFormat("#0.00");

    public static Date parseStrToDate(String date) {
        try {
            return DATE_FORMAT.parse(date);
        } catch (Exception e) {
            logger.error("Parse date error.", e);
            return null;
        }
    }

    public static String parseDateToStr(Date date) {
        return DATE_FORMAT.format(date);
    }

    public static String parseDoubleToStr(double number) {
        return DOUBLE_DECIMAL_FORMAT.format(number);
    }

    public static double parseStringToDouble(String number) {
        return Double.parseDouble(new BigDecimal(number).toPlainString());
    }

}
