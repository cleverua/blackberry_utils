package com.cleverua.bb.utils;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Date;

import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;

import net.rim.device.api.i18n.SimpleDateFormat;
import net.rim.device.api.system.DeviceInfo;

/**
 * File logging facility. Uses a file on SDCard to store log entries.
 * It's very raw and not optimal, it MUST NOT BE USED EXCEPT FOR DEVELOPMENT PURPOSES!
 * 
 * In order to turn it off just modify LOG_ENABLED boolean constant and rebuild the project.
 */
public class Logger {

    // enable if having trouble debugging
    public static final boolean LOG_ENABLED = true;

    public static final String LOG_FILE_NAME = "application.log";
    public static final String LOG_FILE      = "file:///SDCard/" + LOG_FILE_NAME;
    // or to put in DeviceMemory: "file:///store/home/user/" + LOG_FILE_NAME;

    private static final String DEBUG       = "DEBUG";
    private static final String DATE_START  = " [ ";
    private static final String DATE_END    = " ]: ";
    private static final String DELIMITER_1 = " : ";
    private static final String DELIMITER_2 = ": ";
    private static final String NL          = "\n";

    private static final int MAX_LOG_FILE_SIZE = 10 * 1024 * 1024; /* 10MB */

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public synchronized static void debug(String msg) {

        final String message = getMessage(msg);

        // always write log file if on simulator 
        if (DeviceInfo.isSimulator()) {
            writeToFile(message);
            SysLog.log(message);
            return;
        }

        // if on real device do logging only if logger is enabled globally (LOG_ENABLED)
        if (LOG_ENABLED) {
            writeToFile(message);

            // Write the log message to the device EventLogger.
            SysLog.log(message);
        }
    }

    public synchronized static void debug(Object o, String msg) {
        debug(new StringBuffer(o.getClass().toString()).append(DELIMITER_2).append(msg).toString());
    }

    private static String getMessage(String msg) {
        return new StringBuffer(DEBUG)
        .append(DATE_START).append(DATE_FORMAT.format(new Date())).append(DATE_END)
        .append(Thread.currentThread().toString()).append(DELIMITER_1)
        .append(msg).append(NL).toString();
    }

    private synchronized static void writeToFile(String msg) {
        FileConnection fc = null;
        OutputStream out = null;
        OutputStreamWriter writer = null;
        try {
            fc = (FileConnection) Connector.open(LOG_FILE, Connector.READ_WRITE);
            if (!fc.exists()) {
                fc.create();
            }

            if (fc.fileSize() > MAX_LOG_FILE_SIZE && LOG_ENABLED) {
                fc.delete();
                fc.create();
            }

            out = fc.openOutputStream(fc.availableSize());
            writer = new OutputStreamWriter(out);
            writer.write(msg);
            writer.flush();

        } catch (IOException e) {
            System.out.println("Failed to write to log file, " + e);
        } finally {
            IOUtils.safelyCloseStream(writer);
            IOUtils.safelyCloseStream(out);
            IOUtils.safelyCloseStream(fc);
        }
    }
}
