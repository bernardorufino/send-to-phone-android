package com.brufino.sendtophone.app;

public class Preferences {

    public static final String GENERAL_PREFERENCES = Preferences.class.getPackage().toString() + ".GENERAL_PREFERENCES";

    public static final String KEY_USER_EMAIL = "user_email";

    // Prevents instantiation
    private Preferences() {
        throw new AssertionError("Cannot instantiate object from " + this.getClass());
    }
}
