package com.changenode;

public class ErrorHandler {
    public static class Severity {
        public static final int CRITICAL = 1;
        public static final int HIGH = 2;
        public static final int MODERATE = 3;
        public static final int MINOR = 4;
        public static final int IGNORE = 5;
    }
    public static void handle(Exception exc, int severity) {
        if (severity == Severity.CRITICAL) {
            critical(exc);
        } else if (severity == Severity.HIGH) {
            high(exc);
        } else if (severity == Severity.MODERATE) {
            mod(exc);
        } else if (severity == Severity.MINOR) {
            minor(exc);
        } else {
            ignore(exc);
        }
    }
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_BLACK = "\u001B[30m";
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_YELLOW = "\u001B[33m";
    private static final String ANSI_BLUE = "\u001B[34m";
    private static final String ANSI_PURPLE = "\u001B[35m";
    private static final String ANSI_CYAN = "\u001B[36m";
    private static final String ANSI_WHITE = "\u001B[37m";
    private static void critical(Exception exception) {
        System.out.println(ANSI_RED + "[CRITICAL ERROR]"+ANSI_RESET);
        System.out.println(exception.toString());
    }
    private static void high(Exception exception) {
        System.out.println(ANSI_YELLOW + "[SEVERE ERROR]"+ANSI_RESET);
        System.out.println(exception.toString());
    }
    private static void mod(Exception exception) {
        System.out.println(ANSI_YELLOW + "[SEVERE ERROR]"+ANSI_RESET);
        System.out.println(exception.toString());
    }
    private static void minor(Exception exception) {
        System.out.println(ANSI_CYAN + "[MINOR ERROR]"+ANSI_RESET);
        System.out.println(exception.toString());
    }
    private static void ignore(Exception exception) {
        System.out.println(exception.toString());
    }
    //TODO: all
}
