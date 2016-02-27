package com.brufino.sendtophone.app;

public class Helper {

    private static final String ELLIPSIS = "...";

    public static String truncate(String string, int limit) {
        if (string.length() <= limit) {
            return string;
        }
        return string.substring(0, limit - ELLIPSIS.length()) + ELLIPSIS;
    }

    // Prevents instantiation
    private Helper() {
        throw new AssertionError("Cannot instantiate object from " + this.getClass());
    }
}
