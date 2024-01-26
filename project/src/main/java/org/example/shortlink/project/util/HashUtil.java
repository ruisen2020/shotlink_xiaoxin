package org.example.shortlink.project.util;


import cn.hutool.core.lang.hash.MurmurHash;

/**
 * HASH 工具类
 */
public class HashUtil {
    public static void main(String[] args) {
        System.out.println(hashToBase62("https://www.yuque.com/magestack/"));
    }

    private static final char[] CHARS = new char[]{
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'
    };

    private static final int SIZE = CHARS.length;

    private static String convertDecToBase62(long num) {
        StringBuilder sb = new StringBuilder();
        while (num > 0) {
            int i = (int) (num % SIZE);
            sb.append(CHARS[i]);
            num /= SIZE;
        }
        return sb.reverse().toString();
    }

    public static String hashToBase62(String str) {
        long i = MurmurHash.hash32(str); // 生成的是32位无符号整数，最大也就是 2 ^ 32,也就是四十多亿
        long num = i < 0 ? Integer.MAX_VALUE - (long) i : i;
        System.out.println(num);
        return convertDecToBase62(num);
    }
}