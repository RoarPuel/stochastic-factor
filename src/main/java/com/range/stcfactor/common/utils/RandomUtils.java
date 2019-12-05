package com.range.stcfactor.common.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collections;
import java.util.List;

/**
 * 随机生成器
 *
 * @author zrj5865@163.com
 * @create 2019-08-05
 */
public class RandomUtils {

    private static final Logger logger = LogManager.getLogger(RandomUtils.class);

    public static int getRandomNum() {
        return org.apache.commons.lang3.RandomUtils.nextInt();
    }

    public static int getRandomNum(int maxExclusive) {
        return org.apache.commons.lang3.RandomUtils.nextInt(0, maxExclusive);
    }

    public static int getRandomNum(int minInclusive, int maxExclusive) {
        return org.apache.commons.lang3.RandomUtils.nextInt(minInclusive, maxExclusive);
    }

    public static <T> T getRandomInfo(List<T> infos) {
        Collections.shuffle(infos);
        int index = (int) (org.apache.commons.lang3.RandomUtils.nextDouble(0, 1) * infos.size());
        return infos.get(index);
    }

}
