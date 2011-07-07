package com.cleverua.bb.utils;

import net.rim.device.api.system.EventLogger;

import java.io.UnsupportedEncodingException;

public final class SysLog {

    /** EventLogger GUID. */
    private static final long LOGGER_ID = 1001101;
    
    /** Logger name; which shows on the Event Log screen. */
    private static final String LOGGER_NAME = "Sample System Logger";

    /** Boolean flag to test if the EventLogger has been registered. */
    private static boolean registered = EventLogger.register(LOGGER_ID,
                                            LOGGER_NAME,
                                            EventLogger.VIEWER_STRING);

    public static void log(final String message) {
        if (!registered) {
            throw new RuntimeException("Failed to register System Logger!");
        }
        
        try {
            EventLogger.logEvent(LOGGER_ID, message.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("UTF-8 not supported, it's weird!");
        }
    }

}
