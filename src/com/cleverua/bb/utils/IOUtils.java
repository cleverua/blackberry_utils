package com.cleverua.bb.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Enumeration;

import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;

import net.rim.device.api.io.file.FileIOException;
import net.rim.device.api.system.Characters;

/**
 * A bunch of convenient methods for file IO manipulations.
 * @author Vit Khudenko, vit@cleverua.com
 */
public class IOUtils {

    public static final String CARD_ROOT = "file:///SDCard/";
    
    /**
     * Any file gets this ".rem" extension if SDCard Encryption is ON.
     * A media file gets the ".rem" extension if MediaFile Encryption is ON.
     */
    private static final String ENCR_FILE_EXTENSION = ".rem";
    
    private static final String TMP_EXT = ".tmp";
    private static final String URL_ROOT_SEPARATOR = ":///";
    
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
     * @throws IllegalArgumentException if the <code>url</code> is invalid.
     * @throws SecurityException if the security of the application does not have 
     * both read and write access for the connection's target.
     * @throws IOException if the target is a directory and it is not empty, 
     * the target is unaccessible, or an unspecified error occurs 
     * preventing deletion of the target.
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
     * @throws IllegalArgumentException if the <code>url</code> is invalid or 
     * if <code>newName</code> contains any path specification.
     * @throws SecurityException if the security of the application does not have 
     * both read and write access for the connection's target.
     * @throws IOException
     * <ul>
     * <li> 
     * if the connection's target for the <code>url</code> is not accessible, 
     * a file or directory already exists by the <code>newName</code>, 
     * or <code>newName</code> is an invalid filename for the platform 
     * (e.g. contains characters invalid in a filename on the platform).
     * </li>
     * <li>if the firewall disallows a connection that is not btspp or comm.</li>
     * </ul>
     * 
     * @throws NullPointerException if <code>newName</code> is null.
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
     * 
     * @throws IllegalArgumentException if the <code>sourceFileUrl</code> or 
     * <code>destinationFileUrl</code> is invalid.
     * @throws SecurityException if the security of the application does not have 
     * both read and write access for the connection's target.
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
            destination = (FileConnection) Connector.open(destinationFileUrl);
            
            if (destination.exists()) {
                // truncate does not work if file is encrypted via SDCard encryption (has ".rem" suffix)!
                // destination.truncate(0);
                
                destinationTmp = (FileConnection) Connector.open(destinationFileUrl + TMP_EXT);
                
                if (destinationTmp.exists()) {
                    destinationTmp.delete(); /* just in case */
                }
                destinationTmp.create();
                
                try {
                    is = source.openInputStream();
                    os = destinationTmp.openOutputStream();
                    copyData(is, os);
                } catch (IOException e) {
                    safelyCloseStream(os);
                    try {
                        destinationTmp.delete();
                    } catch (IOException e1) { 
                        /* do nothing here */
                    }
                    throw e;
                }
                
                String destinationFileName = destination.getName();
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
    
    /**
     * Saves byte array data to file with a given url.
     * If the destination file has been already present, then it is overwritten.
     * 
     * @param data - Array of bytes to save.
     * @param url - url of the destination file.
     * 
     * @throws IllegalArgumentException if the <code>url</code> is invalid.
     * @throws SecurityException if the security of the application does not have 
     * both read and write access for the connection's target.
     * @throws IOException
     * <ul>
     * <li>if the target file system is not accessible or the data array size is greater 
     * than free memory that is available on the file system the file resides on.</li>
     * <li>if an I/O error occurs.</li>
     * <li>if url has a trailing "/" to denote a directory, or an unspecified error occurs preventing creation of the file.</li>
     * <li>if the firewall disallows a connection that is not btspp or comm.</li>
     * </ul>
     */
    public static void saveDataToFile(String url, byte[] data) throws IOException {
        FileConnection fc  = null;
        FileConnection tmp = null;
        OutputStream out   = null;
        
        try {
            fc = (FileConnection) Connector.open(url);
            
            // check for available space
            if (fc.availableSize() < data.length) {
                throw new FileIOException(FileIOException.FILESYSTEM_FULL);
            }
            
            if (fc.exists()) {

                tmp = (FileConnection) Connector.open(url + TMP_EXT);
                
                if (tmp.exists()) {
                    tmp.delete(); /* just in case */
                }
                tmp.create();
                
                try {
                    out = tmp.openOutputStream();
                    out.write(data);
                    out.flush();
                } catch (IOException e) {
                    safelyCloseStream(out);
                    try {
                        tmp.delete();
                    } catch (IOException e1) { 
                        /* do nothing here */
                    }
                    throw e;
                }
                
                String originalFileName = fc.getName();
                fc.delete();
                tmp.rename(originalFileName);
                
            } else {
                fc.create();
                out = fc.openOutputStream();
                out.write(data);
                out.flush();
            }

        } finally {
             safelyCloseStream(out);
             safelyCloseStream(fc);
             safelyCloseStream(tmp);
        }
    }
    
    /**
     * Saves data from InputStream to file with a given url.
     * If the destination file has been already present, then it is overwritten.
     * 
     * @param InputStream - to read the data to save from.
     * @param url - url of the destination file.
     * 
     * @throws IllegalArgumentException if the <code>url</code> is invalid.
     * @throws SecurityException if the security of the application does not have 
     * both read and write access for the connection's target.
     * @throws IOException
     * <ul>
     * <li>if the target file system is not accessible or the data size is greater 
     * than free memory that is available on the file system the file resides on.</li>
     * <li>if an I/O error occurs.</li>
     * <li>if url has a trailing "/" to denote a directory, or an unspecified error occurs preventing creation of the file.</li>
     * <li>if the firewall disallows a connection that is not btspp or comm.</li>
     * </ul>
     */
    public static void saveDataToFile(String url, InputStream is) throws IOException {
        FileConnection fc  = null;
        FileConnection tmp = null;
        OutputStream out   = null;
        
        try {
            fc = (FileConnection) Connector.open(url);
            
            if (fc.exists()) {

                tmp = (FileConnection) Connector.open(url + TMP_EXT);
                
                if (tmp.exists()) {
                    tmp.delete(); /* just in case */
                }
                tmp.create();
                
                try {
                    out = tmp.openOutputStream();
                    copyData(is, out);
                } catch (IOException e) {
                    safelyCloseStream(out);
                    try {
                        tmp.delete();
                    } catch (IOException e1) { 
                        /* do nothing here */
                    }
                    throw e;
                }
                
                String originalFileName = fc.getName();
                fc.delete();
                tmp.rename(originalFileName);
                
            } else {
                fc.create();
                out = fc.openOutputStream();
                copyData(is, out);
            }

        } finally {
             safelyCloseStream(out);
             safelyCloseStream(fc);
             safelyCloseStream(tmp);
             safelyCloseStream(is);
        }
    }
    
    /**
     * Reads file data and returns it as a byte array. 
     * File should be present, otherwise IOException is thrown. 
     * 
     * @param url - url of the source file.
     * @return Array of bytes.
     * 
     * @throws IllegalArgumentException if the <code>url</code> is invalid.
     * @throws SecurityException if the security of the application does not have 
     * read access for the file.
     * @throws IOException
     * <ul>
     * <li>if an I/O error occurs, if the method is invoked on a directory, 
     * the file does not yet exist, or the connection's target is not accessible.</li>
     * <li>if the firewall disallows a connection that is not btspp or comm.</li>
     * </ul>
     */
    public static byte[] getFileData(String url) throws IOException {
        FileConnection fc = null;
        InputStream in = null;
        
        try {
            fc = (FileConnection) Connector.open(url, Connector.READ);
            in = fc.openInputStream();
            byte[] data = new byte[(int) fc.fileSize()];
            in.read(data);
            return data;
        } finally {
            safelyCloseStream(in);
            safelyCloseStream(fc);
        }
    }
    
    /**
     * Creates a directory corresponding to passed <code>url</code> parameter. 
     * Directories in the specified <code>url</code> are not recursively created and 
     * must be explicitly created before subdirectories can be created. If the directory
     * is already exists, then the method does nothing. 
     * 
     * @param url - path to dir to create, e.g. <code>"file:///SDCard/my_new_dir/"</code>.
     * 
     * @throws IllegalArgumentException if the <code>url</code> is invalid.
     * @throws SecurityException if the security of the application does not have 
     * both read and write access for the connection's target.
     * @throws IOException 
     * <ul>
     * <li>if invoked on a non-existent file, the target file system 
     * is not accessible, or an unspecified error occurs preventing creation of the directory.</li>
     * <li>if the firewall disallows a connection that is not btspp or comm.</li>
     * </ul>
     */
    public static void createDir(String url) throws IOException {
        FileConnection fc = null;
        try {
            fc = (FileConnection) Connector.open(url);
            if (!fc.exists()) {
                fc.mkdir();
            }
        } finally {
            safelyCloseStream(fc);
        }
    }
    
    /**
     * Creates a directory corresponding to passed <code>url</code> parameter. 
     * Unlike {@link #createDir createDir(String url)} directories in the 
     * specified <code>url</code> are recursively created if needed. If the directory
     * is already exists, then the method does nothing. 
     * 
     * @param url - path to dir to create, e.g. <code>"file:///SDCard/my_new_dir/"</code>.
     * 
     * @throws IllegalArgumentException <code>url</code> is invalid.
     * @throws SecurityException if the security of the application does not have 
     * both read and write access for the connection's target.
     * @throws IOException
     * <ul>
     * <li>if invoked on a non-existent file, the target file system is 
     * not accessible, or an unspecified error occurs preventing creation of the directory.</li>
     * <li>if the firewall disallows a connection that is not btspp or comm.</li>
     * </ul>
     */
    public static void createDirIncludingAncestors(String url) throws IOException {
        final int index = url.indexOf(URL_ROOT_SEPARATOR);
        if (index == -1) {
            throw new IllegalArgumentException("Invalid url");
        }
        String rootOfPath = url.substring(0, index + 4); // e.g. "file:///"
        String restOfPath = url.substring(rootOfPath.length());
        int solidusIndex = -1;
        while (true) {
            solidusIndex = restOfPath.indexOf(Characters.SOLIDUS, solidusIndex + 1);
            if (solidusIndex < 0) {
                break;
            }
            createDir(rootOfPath + restOfPath.substring(0, solidusIndex + 1));
        }
    }
    
    /**
     * @param url - target path to check, e.g. <code>"file:///SDCard/my_new_dir/"</code>.
     * @return True if the target exists, is accessible, and is a directory, otherwise false.
     * 
     * @throws IllegalArgumentException if the <code>url</code> is invalid.
     * @throws SecurityException if the security of the application does not have 
     * read access for the connection's target.
     * 
     * @throws IOException if the firewall disallows a connection that is not btspp or comm.
     */
    public static boolean isDirectory(String url) throws IOException {
        FileConnection fc = null;
        try {
            fc = (FileConnection) Connector.open(url, Connector.READ);
            return fc.isDirectory();
        } finally {
            IOUtils.safelyCloseStream(fc);
        }
    }
    
    /**
     * Deletes the file or directory specified in the <code>url</code> parameter.
     * If the target does not exist, then the method does nothing.
     * 
     * <p>
     * The method was specifically designed to delete a directory 
     * assuming it may not be empty.
     * </p>
     * 
     * <p>The method also works if the directory contains encrypted files.</p>
     * 
     * @param url - URL to a file or a directory to be deleted, 
     * e.g. <code>"file:///SDCard/my_dir/"</code>.
     * 
     * @throws IllegalArgumentException if the <code>url</code> is invalid.
     * @throws SecurityException if the security of the application does not have 
     * both read and write access for the connection's target.
     * @throws IOException
     * <ul>
     * <li>the target is unaccessible, or an unspecified error occurs 
     * preventing deletion of the target.</li>
     * <li>if the firewall disallows a connection that is not btspp or comm.</li>
     * </ul>
     */
    public static void deleteDir(String url) throws IOException {
        FileConnection fc = null;
        try {
            if (isDirectory(url)) {
                fc = (FileConnection) Connector.open(url, Connector.READ);
                Enumeration e = fc.list();
                IOUtils.safelyCloseStream(fc);
                while (e.hasMoreElements()) {
                    deleteDir(url + removeEncExtension((String) e.nextElement()));
                }
            }
            fc = (FileConnection) Connector.open(url);
            if (fc.exists()) {
                fc.delete();
            }
        } finally {
            IOUtils.safelyCloseStream(fc);
        }
    }
    
    /**
     * Determines the size of a file on the file system.
     * 
     * @param url - URL to a file to be processed.
     * @return The size in bytes of the selected file, 
     * or -1 if the file does not exist or is not accessible.
     * 
     * 
     * @throws IllegalArgumentException if the <code>url</code> is invalid.
     * @throws SecurityException if the security of the application does not have 
     * read access for the connection's target.
     * @throws IOException
     * <ul>
     * <li>if the method is invoked on a directory.</li>
     * <li>if the firewall disallows a connection that is not btspp or comm.</li>
     * </ul>
     */
    public static long getFileSize(String url) throws IOException {
        FileConnection fc = null;
        try {
            fc = (FileConnection) Connector.open(url, Connector.READ);            
            return fc.fileSize();
        } finally {
            IOUtils.safelyCloseStream(fc);
        }
    }
    
    /**
     * If user enables SDCard Encryption, then files may get the ".rem" extension.
     * To get "clean" file url we may use this method. We need a clean url to pass
     * it to some other IO method.
     * 
     * @param url to clean from ".rem" extension.
     * @return url with ".rem" extension removed if found. Null remains null.
     */
    public static String removeEncExtension(String url) {
        if (url != null && url.endsWith(ENCR_FILE_EXTENSION)) {
            return url.substring(0, (url.length() - ENCR_FILE_EXTENSION.length()));
        }
        return url;
    }

    /**
     * Checks if the file or directory specified in the <code>url</code> 
     * passed to the method exists.
     * 
     * @param url - URL to a file or directory to be processed.
     * @return true if the target exists and is accessible, otherwise false.
     * 
     * @throws IllegalArgumentException if the <code>url</code> is invalid.
     * @throws IOException if the firewall disallows a connection that is not btspp or comm.
     * @throws SecurityException if the security of the application 
     * does not have read access for the <code>url</code>.
     */
    public static boolean isPresent(String url) throws IOException {
        FileConnection fc = null;
        try {
            fc = (FileConnection) Connector.open(url, Connector.READ);
            return fc.exists();
        } finally {
            IOUtils.safelyCloseStream(fc);
        }
    }
    
    /**
     * @return true if SDCard is present or false otherwise.
     */
    public static boolean isSDCardPresent() {
        try {
            return isPresent(CARD_ROOT);
        } catch (IOException e) {
            return false;
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
