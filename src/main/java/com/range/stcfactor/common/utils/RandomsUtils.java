package com.range.stcfactor.common.utils;

import org.apache.commons.lang3.RandomUtils;

import java.util.Collections;
import java.util.List;

/**
 * 随机生成器
 *
 * @author zrj5865@163.com
 * @create 2019-08-05
 */
public class RandomsUtils {

    public static int getRandomInt() {
        return org.apache.commons.lang3.RandomUtils.nextInt();
    }

    public static int getRandomInt(int maxExclusive) {
        return RandomUtils.nextInt(0, maxExclusive);
    }

    public static int getRandomInt(int minInclusive, int maxExclusive) {
        return RandomUtils.nextInt(minInclusive, maxExclusive);
    }

    public static double getRandomDouble(double minInclusive, double maxExclusive) {
        return RandomUtils.nextDouble(minInclusive, maxExclusive);
    }

    public static <T> T getRandomInfo(List<T> infos) {
        Collections.shuffle(infos);
        int index = (int) (RandomUtils.nextDouble(0, 1) * infos.size());
        return infos.get(index);
    }

}
