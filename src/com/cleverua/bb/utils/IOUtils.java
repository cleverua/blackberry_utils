package com.cleverua.bb.utils;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import javax.microedition.io.file.FileConnection;


/**
 * A bunch of convenient methods for file IO manipulations.
 * @author Vit Khudenko, vit@cleverua.com
 */
public class IOUtils {
    
    /**
     * Safely closes {@link InputStream} stream.
     * 
     * <p>
     * If stream is not null, the method calls <code>close()</code> for the stream.<br />
     * If any {@link Exception} occurs during <code>close()</code> call, that exception is silently caught.
     * </p>
     * 
     * @param stream {@link InputStream} instance to be closed.
     */
    public static void safelyCloseStream(InputStream stream) {
        if (stream != null) {
            try { 
                stream.close();
                stream = null;
            } catch (Exception e) { /* that's ok */ }
        }
    }
    
    /**
     * Safely closes {@link OutputStream} stream.
     * 
     * <p>
     * If stream is not null, the method calls <code>close()</code> for the stream.<br />
     * If any {@link Exception} occurs during <code>close()</code> call, 
     * that exception is caught and the only action being made is putting 
     * a log message about the got exception.
     * </p>
     * 
     * <p>
     * <b>IMPORTANT:</b><br />
     * Despite <code>close()</code> call also invokes <code>flush()</code> first,
     * clients should not relay on this behavior and must call <code>flush()</code>
     * explicitly before calling <code>safelyCloseStream(OutputStream stream)</code> instead.
     * Otherwise any useful exception <code>flush()</code> may throw will not be passed to the client
     * and the stream will remain open.
     * </p>
     * 
     * @param stream {@link OutputStream} instance to be closed.
     */
    public static void safelyCloseStream(OutputStream stream) {
        if (stream != null) {
            try {
                stream.close();
                stream = null;
            } catch (Exception e) {
                Logger.debug(IOUtils.class, "got error in safelyCloseStream for OutputStream: " + e);
            }
        }
    }
    
    /**
     * Safely closes {@link FileConnection} stream.
     * 
     * <p>
     * If stream is not null, the method calls <code>close()</code> for the stream.<br />
     * If any {@link Exception} occurs during <code>close()</code> call, that exception is silently caught.
     * </p>
     * 
     * @param stream {@link FileConnection} instance to be closed.
     */
    public static void safelyCloseStream(FileConnection stream) {
        if (stream != null) {
            try {
                stream.close();
                stream = null;
            } catch (Exception e) { /* that's ok */ }
        }
    }
    
    /**
     * Safely closes {@link OutputStreamWriter} stream.
     * 
     * <p>
     * If stream is not null, the method calls <code>close()</code> for the stream.<br />
     * If any {@link Exception} occurs during <code>close()</code> call, 
     * that exception is caught and the only action being made is putting 
     * a log message about the got exception.
     * </p>
     * 
     * <p>
     * <b>IMPORTANT:</b><br />
     * Despite <code>close()</code> call also invokes <code>flush()</code> first,
     * clients should not relay on this behavior and must call <code>flush()</code>
     * explicitly before calling <code>safelyCloseStream(OutputStreamWriter stream)</code> instead.
     * Otherwise any useful exception <code>flush()</code> may throw will not be passed to the client
     * and the stream will remain open.
     * </p>
     * 
     * @param stream {@link OutputStreamWriter} instance to be closed.
     */
    public static void safelyCloseStream(OutputStreamWriter stream) {
        if (stream != null) {
            try {
                stream.close();
                stream = null;
            } catch (Exception e) { 
                Logger.debug(IOUtils.class, "got error in safelyCloseStream for OutputStreamWriter: " + e);
            }
        }
    }
}
