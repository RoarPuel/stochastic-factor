package com.range.stcfactor.expression;

import org.apache.commons.lang3.RandomUtils;
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
public class RandomHelper {

    private static final Logger logger = LogManager.getLogger(RandomHelper.class);

    public static int getRandomNum() {
        return RandomUtils.nextInt();
    }

    public static int getRandomNum(int maxExclusive) {
        return RandomUtils.nextInt(0, maxExclusive + 1);
    }

    public static int getRandomNum(int minInclusive, int maxExclusive) {
        return RandomUtils.nextInt(minInclusive, maxExclusive + 1);
    }

    public static <T> T getRandomInfos(List<T> infos) {
        Collections.shuffle(infos);
        int index = (int) (RandomUtils.nextDouble(0, 1) * infos.size());
        return infos.get(index);
    }

}
