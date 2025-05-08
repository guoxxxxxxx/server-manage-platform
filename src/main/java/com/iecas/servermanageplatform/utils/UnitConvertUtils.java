package com.iecas.servermanageplatform.utils;

/**
 * @Author: guo_x
 * @Date: 2025/5/7 15:23
 * @Description: 单位转化工具
 */
public class UnitConvertUtils {


    /**
     * 将存储空间转化为KB格式
     * @param memoryStr 带单位的存储空间, 如 5Gi,
     * @return 转换后的KB值
     */
    public static long parseToKB(String memoryStr){
        if (memoryStr == null || memoryStr.isEmpty()) {
            throw new IllegalArgumentException("内存字符串为空");
        }

        memoryStr = memoryStr.trim().toUpperCase();

        double number;
        long multiplier;

        try {
            if (memoryStr.endsWith("KI")) {
                number = Double.parseDouble(memoryStr.replace("KI", ""));
                multiplier = 1L;
            } else if (memoryStr.endsWith("MI")) {
                number = Double.parseDouble(memoryStr.replace("MI", ""));
                multiplier = 1024L;
            } else if (memoryStr.endsWith("GI")) {
                number = Double.parseDouble(memoryStr.replace("GI", ""));
                multiplier = 1024L * 1024L;
            } else if (memoryStr.endsWith("TI")) {
                number = Double.parseDouble(memoryStr.replace("TI", ""));
                multiplier = 1024L * 1024L * 1024L;
            } else if (memoryStr.endsWith("K")) {
                number = Double.parseDouble(memoryStr.replace("K", ""));
                multiplier = 1L;
            } else if (memoryStr.endsWith("M")) {
                number = Double.parseDouble(memoryStr.replace("M", ""));
                multiplier = 1024L;
            } else if (memoryStr.endsWith("G")) {
                number = Double.parseDouble(memoryStr.replace("G", ""));
                multiplier = 1024L * 1024L;
            } else if (memoryStr.endsWith("T")) {
                number = Double.parseDouble(memoryStr.replace("T", ""));
                multiplier = 1024L * 1024L * 1024L;
            } else {
                // 无单位默认按 KB
                number = Double.parseDouble(memoryStr);
                multiplier = 1L;
            }

            return (long) (number * multiplier);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("内存格式错误: " + memoryStr, e);
        }
    }
}
