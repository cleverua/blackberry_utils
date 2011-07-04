package com.cleverua.bb.utils;

import net.rim.device.api.system.EventLogger;

/**
 * Application logger using BlackBerry's EventLogger.
 *
 * <p>
 * Before using this class, you must call {@link SysLog.setup(String, long)} with an
 * application specific {@code String} name and {@code long} GUID.
 * </p>
 */
public final class SysLog {

    /** EventLogger GUID. */
    private static long LOGGER_ID = null;

    /** Logger name; which shows on the Event Log screen. */
    private static String LOGGER_NAME = null;

    /** Boolean flag to test if the EventLogger has been registered. */
    private static boolean REGISTERED = false;

    public static void setup(final String loggerName, final long guid) {
        LOGGER_NAME = loggerName;
        LOGGER_ID   = guid;
        REGISTERED  = registerEventLogger();
    }

    /** Write {@code message} to the device event log. */
    public static void log(final String message) {
        if ( ! REGISTERED) throw new Exception("Must call 'SysLog.setup()' before using this class.");
        EventLogger.logEvent(LOGGER_ID, message.getBytes());
    }

    private static boolean registerEventLogger() {
        if (REGISTERED) return;
        if ( (LOGGER_ID == null) || (LOGGER_NAME == null) ) {
            return false
        }
        EventLogger.register(LOGGER_ID, LOGGER_NAME, EventLogger.VIEWER_STRING);
    }
}
