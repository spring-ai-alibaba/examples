package com.alibaba.cloud.ai.rag.parallel.util;

import org.springframework.boot.ansi.AnsiColor;
import org.springframework.boot.ansi.AnsiOutput;
import org.springframework.boot.ansi.AnsiStyle;

/**
 * 控制台彩色输出工具类
 * 简化 System.out 和 System.err 的彩色输出
 *
 * @Author NGshiyu
 * @Description 控制台彩色输出工具
 * @CreateTime 2026/2/3
 */
public class ConsoleColor {

    /**
     * 彩色打印到 System.out
     *
     * @param color  颜色
     * @param format 格式化字符串
     * @param args   参数
     */
    public static void println(AnsiColor color, String format, Object... args) {
        System.out.println(AnsiOutput.toString(color, String.format(format, args), AnsiColor.DEFAULT));
    }

    /**
     * 彩色打印到 System.out（带样式）
     *
     * @param color  颜色
     * @param style  样式（加粗、下划线等）
     * @param format 格式化字符串
     * @param args   参数
     */
    public static void println(AnsiColor color, AnsiStyle style, String format, Object... args) {
        System.out.println(AnsiOutput.toString(color, style, String.format(format, args), AnsiStyle.NORMAL, AnsiColor.DEFAULT));
    }

    /**
     * 彩色打印到 System.err
     *
     * @param color  颜色
     * @param format 格式化字符串
     * @param args   参数
     */
    public static void errPrintln(AnsiColor color, String format, Object... args) {
        System.err.println(AnsiOutput.toString(color, String.format(format, args), AnsiColor.DEFAULT));
    }

    /**
     * 打印分隔线
     *
     * @param color   颜色
     * @param title   标题
     * @param message 内容
     */
    public static void printSeparator(AnsiColor color, String title, String message) {
        String separator = AnsiOutput.toString(
                color, "==============================", title,
                AnsiColor.DEFAULT, ":", message,
                color, "==============================",
                AnsiColor.DEFAULT
        );
        System.out.println(separator);
    }

    /**
     * 打印成功信息（绿色）
     */
    public static void success(String format, Object... args) {
        println(AnsiColor.GREEN, format, args);
    }

    /**
     * 打印信息（蓝色）
     */
    public static void info(String format, Object... args) {
        println(AnsiColor.BLUE, format, args);
    }

    /**
     * 打印警告信息（黄色）
     */
    public static void warn(String format, Object... args) {
        println(AnsiColor.YELLOW, format, args);
    }

    /**
     * 打印错误信息（红色）
     */
    public static void error(String format, Object... args) {
        errPrintln(AnsiColor.RED, format, args);
    }

    /**
     * 打印调试信息（青色）
     */
    public static void debug(String format, Object... args) {
        println(AnsiColor.CYAN, format, args);
    }

    // ========== 快捷打印方法 ==========

    /**
     * 红色文本
     */
    public static void red(String format, Object... args) {
        println(AnsiColor.RED, format, args);
    }

    /**
     * 绿色文本
     */
    public static void green(String format, Object... args) {
        println(AnsiColor.GREEN, format, args);
    }

    /**
     * 黄色文本
     */
    public static void yellow(String format, Object... args) {
        println(AnsiColor.YELLOW, format, args);
    }

    /**
     * 蓝色文本
     */
    public static void blue(String format, Object... args) {
        println(AnsiColor.BLUE, format, args);
    }

    /**
     * 品红文本
     */
    public static void magenta(String format, Object... args) {
        println(AnsiColor.MAGENTA, format, args);
    }

    /**
     * 青色文本
     */
    public static void cyan(String format, Object... args) {
        println(AnsiColor.CYAN, format, args);
    }

    /**
     * 亮青色文本
     */
    public static void brightCyan(String format, Object... args) {
        println(AnsiColor.BRIGHT_CYAN, format, args);
    }

    /**
     * 亮黄色文本
     */
    public static void brightYellow(String format, Object... args) {
        println(AnsiColor.BRIGHT_YELLOW, format, args);
    }

    /**
     * 亮绿色文本
     */
    public static void brightGreen(String format, Object... args) {
        println(AnsiColor.BRIGHT_GREEN, format, args);
    }
}
