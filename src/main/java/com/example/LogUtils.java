package com.example;

// 简单的日志模块。
public class LogUtils {
    private static void log(String level, String tag, String fmt, Object... args) {
        System.err.format("[%s] %s: %s\n", level, tag, String.format(fmt, args));
    }

    public static void info(String tag, String fmt, Object... args) {
        log("INFO", tag, fmt, args);
    }
}
