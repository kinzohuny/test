package com.eci.youku.util;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.util.UUID;

/**
 * 提供一些随机数或字符的工具类
 * 
 * @Version: 1.0
 * @ProjectName:bm_airui
 * @Filename: RandomUtils.java
 * @PackageName: cn.com.bmks.util
 * @Author: liuxichen 刘希臣
 * @Email: aaron@ecinsight.com.cn
 *
 */
public abstract class RandomUtils {

    private static SecureRandom random;

    public static final Object[] CODE_DIGIT_LETTER = new Object[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 'A', 'B', 'C', 'D',
            'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y',
            'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't',
            'u', 'v', 'w', 'x', 'y', 'z' };

    public static final Object[] CODE_NUMBER = new Integer[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 };

    static {
        try {
            random = SecureRandom.getInstance("SHA1PRNG", "SUN");
        } catch (NoSuchAlgorithmException e) {
            throw new Error(e);
        } catch (NoSuchProviderException e) {
            throw new Error(e);
        }
    }

    /**
     * 获取随机6位数字
     * 
     * @return 六位数字
     */
    public static final String getRandomNum() {
        return getRandomNum(6);
    }

    /**
     * 获取指定位数的随机数
     * 
     * @param decimals
     * @return 指定位数的随机数
     */
    public static final String getRandomNum(int decimals) {
        return getRandomNum(decimals, CODE_NUMBER);
    }

    /**
     * 获取随机数
     * 
     * @param decimals
     *            获取的个数
     * @param source
     *            获取来源
     * @return 随机数
     */
    public static final String getRandomNum(int decimals, Object[] source) {
        StringBuilder builder = new StringBuilder();
        while (decimals-- > 0) {
            builder.append(source[random.nextInt(source.length)]);
        }
        return builder.toString();
    }

    /**
     * 获取随机的UUID实例
     * 
     * @return UUID
     */
    public static UUID randomUUID() {
        return UUID.randomUUID();
    }

    /**
     * 获取随机的UUID字符串
     * 
     * @return UUID字符串
     */
    public static String randomGUID() {
        return randomUUID().toString();
    }

    /**
     * 获取随机的大写的UUID字符串
     * 
     * @return 大写的UUID字符串
     */
    public static String randomUpperCaseGUID() {
        return randomGUID().toUpperCase();
    }

}
