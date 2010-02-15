package com.cleverua.bb.example;

import net.rim.device.api.ui.UiApplication;

/**
 * The application is created just to test the utils during their development.
 * It does not provide any examples of utils usage.
 */
public class UtilsTestingApplication extends UiApplication {
    
    private static UtilsTestingApplication application;

    public static void main(String[] args) {
        application = new UtilsTestingApplication();
        application.pushScreen(new PlaceholderScreen());
        application.enterEventDispatcher();
    }
}
