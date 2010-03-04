package com.cleverua.bb.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;

/**
 * A bunch of convenient methods for file IO manipulations.
 * @author Vit Khudenko, vit@cleverua.com
 */
public class IOUtils {
    
    private static final String TMP_EXT = ".tmp";
    
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
    
    /**
     * Deletes the file or directory specified in the <code>url</code> parameter.
     * If the target does not exist, then the method does nothing.
     * 
     * @param url - URL to a file or a directory to be deleted, 
     * e.g. <code>"file:///SDCard/file.txt"</code>.
     * 
     * @throws IOException
     * <ul>
     * <li>if the <code>url</code> is invalid.</li>
     * <li>if the target is a directory and it is not empty, 
     * the target is unaccessible, or an unspecified error occurs 
     * preventing deletion of the target.</li>
     * </ul>
     */
    public static void delete(String url) throws IOException {
        FileConnection fc = null;
        try {
            fc = (FileConnection) Connector.open(url);
            if (fc.exists()) {
                fc.delete();
            }
        } finally {
            safelyCloseStream(fc);
        }
    }
    
    /**
     * Renames the selected file or directory to a new name in the same directory.
     * If the target does not exist, then the method does nothing.
     * 
     * @param url - URL to the file or directory to be renamed, 
     * e.g. <code>"file:///SDCard/file.txt"</code>.
     * 
     * @param newName - The new name of the file or directory. 
     * The name must not contain any path specification; 
     * the file or directory remains in its same directory as before this method call.
     * 
     * @throws IOException
     * <ul>
     * <li>if the <code>url</code> is invalid.</li>
     * <li>if the connection's target for the <code>url</code> is not accessible, 
     * a file or directory already exists by the <code>newName</code>, 
     * or <code>newName</code> is an invalid filename for the platform 
     * (e.g. contains characters invalid in a filename on the platform)</li>
     * </ul>
     * @throws NullPointerException if <code>newName</code> is null.
     * @throws IllegalArgumentException if <code>newName</code> contains any path specification.
     */
    public static void rename(String url, String newName) throws IOException {
        FileConnection fc = null;
        try {
            fc = (FileConnection) Connector.open(url);
            if (fc.exists()) {
                fc.rename(newName);
            }
        } finally {
            safelyCloseStream(fc);
        }
    }
    
    /**
     * Copies a file. If the destination file has been already present, then it is overwritten.
     * 
     * @param sourceFileUrl - url of the source file.
     * @param destinationFileUrl - url of the destination file.
     * @throws IOException
     */
    public static void copyFile(String sourceFileUrl, String destinationFileUrl) throws IOException {
        InputStream is  = null;
        OutputStream os = null;
        FileConnection source         = null;
        FileConnection destination    = null;
        FileConnection destinationTmp = null;
        
        try {
            
            source = (FileConnection) Connector.open(sourceFileUrl, Connector.READ);
            destination = (FileConnection) Connector.open(destinationFileUrl, Connector.READ_WRITE);
            
            if (destination.exists()) {
                // truncate does not work if file is encrypted via SDCard encryption (has ".rem" suffix)!
                // destination.truncate(0);
                
                String destinationFileName = destination.getName();
                String destinationTmpFileName = destinationFileUrl + TMP_EXT;
                
                destinationTmp = (FileConnection) Connector.open(destinationTmpFileName, Connector.READ_WRITE);
                
                if (destinationTmp.exists()) {
                    destinationTmp.delete(); /* just in case */
                }
                destinationTmp.create();
                
                try {
                    is = source.openInputStream();
                    os = destinationTmp.openOutputStream();
                    copyData(is, os);
                } catch (IOException e) {
                    try {
                        destinationTmp.delete();
                    } catch (IOException e1) { 
                        /* do nothing here */
                    }
                    throw e;
                } finally {
                    safelyCloseStream(is);
                    safelyCloseStream(os);
                }
                
                destination.delete();
                destinationTmp.rename(destinationFileName);
                
            } else {
                destination.create();
                is = source.openInputStream();
                os = destination.openOutputStream();
                copyData(is, os);
            }
            
        } finally {
             safelyCloseStream(is);
             safelyCloseStream(os);
             safelyCloseStream(source);
             safelyCloseStream(destination);
             safelyCloseStream(destinationTmp);
        }
    }
    
    private static void copyData(InputStream source, OutputStream destination) throws IOException {
        byte[] buf = new byte[1024];
        int len;
        while ((len = source.read(buf)) > 0) {
            destination.write(buf, 0, len);
        }
        destination.flush();
    }
}
