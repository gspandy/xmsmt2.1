package net.wit.util;

import java.util.Random;

/**
 * 随机数工具类(种子数位长不超过10位)
 * @ClassName:RandomUtil
 * @Description:TODO(这里用一句话描述这个类的作用)
 * @author:chenxy
 * @date 2011-8-24 上午9:55:09
 * @since JDK Version 1.5
 */
public class RandomUtil {

    /**
     * 获取验证码
     * @return 验证码
     */
    private static int[] getIdentifyingCode(long seed, int length) {
        int[] array = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 0 };
        Random rand = new Random(seed);
        for (int i = 10; i > 1; i--) {
            int index = rand.nextInt(i);
            int tmp = array[index];
            array[index] = array[i - 1];
            array[i - 1] = tmp;
        }
        int padlength = 6;
        if (length + 2 >= 6) {
            padlength = ((length + 3) / 2) * 2;
        }
        int[] result = new int[padlength - length - 1];
        for (int i = 0; i < (padlength - length - 1); i++) {
            result[i] = array[i];
        }
        return result;
    }

    public static String encryptRandom(long seed) {
        StringBuffer seedtemp = new StringBuffer(String.valueOf(seed));
        // 种子类型
        int style = seedtemp.length();
        // 随机数
        int[] code = getIdentifyingCode(seed, style);
        for (int c : code) {
            seedtemp.append(c);
        }
        seedtemp.append(style);
        return seedtemp.toString();
    }

    public static long decryptRandom(String random) {
        char[] a = String.valueOf(random).toCharArray();
        int[] array = new int[a.length];
        for (int i = 0; i < a.length; i++) {
            array[i] = a[i] - 48;
        }
        if (array[a.length - 1] >= a.length) {
            throw new java.lang.IllegalArgumentException("随机码非法!");
        }
        long seed = Long.parseLong(random.substring(0, array[a.length - 1]));
        int[] code = getIdentifyingCode(seed, array[a.length - 1]);
        for (int i = array[a.length - 1], j = 0; i < a.length - 1; i++, j++) {
            if (array[i] != code[j]) {
                throw new java.lang.IllegalArgumentException("随机码非法!");
            }
        }
        return seed;
    }

}
