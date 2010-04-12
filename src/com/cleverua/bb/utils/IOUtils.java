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

    /** String "file:///SDCard/" representing the URL to SD Card root. */
    public static final String CARD_ROOT = "file:///SDCard/";
    
    /** String "file:///store/" representing the URL to Device Memory root. */
    public static final String DEVICE_MEMORY_ROOT = "file:///store/";
    
    /**
     * Any file gets this ".rem" extension if SDCard Encryption is ON.
     * A media file gets the ".rem" extension if MediaFile Encryption is ON.
     */
    private static final String ENCR_FILE_EXTENSION = ".rem";
    
    private static final String TMP_EXT = ".tmp";
    private static final String URL_ROOT_SEPARATOR = ":///";
    
    /** Currently it is "file:///SDCard/encription_test.txt". */
    private static final String TEST_CARD_ENCRYPTION_FILE = CARD_ROOT + "encription_test.txt";
    
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
     * @return true if SDCard exists and is accessible or false otherwise.
     * 
     * @throws SecurityException if the security of the application 
     * does not have read access for the {@link #CARD_ROOT} url.
     * @throws IllegalArgumentException if the {@link #CARD_ROOT} url 
     * happens to be invalid for the current device.
     */
    public static boolean isSDCardAccessible() {
        try {
            return isPresent(CARD_ROOT);
        } catch (IOException e) {
            return false;
        }
    }
    
    /**
     * @return true if Device Memory exists and is accessible or false otherwise.
     * 
     * @throws SecurityException if the security of the application 
     * does not have read access for the {@link #DEVICE_MEMORY_ROOT} url.
     * @throws IllegalArgumentException if the {@link #DEVICE_MEMORY_ROOT} url 
     * happens to be invalid for the current device.
     */
    public static boolean isDeviceMemoryAccessible() {
        try {
            return isPresent(DEVICE_MEMORY_ROOT);
        } catch (IOException e) {
            return false;
        }
    }
    
    /**
     * Determines the size in bytes on a file system of all of the files 
     * that are contained in a directory.
     * 
     * @param url - URL to a directory to be processed.
     * @param includeSubDirs - if set to true, the method determines the size of the given directory 
     * and all subdirs recursively. If false, the method returns the size of the files 
     * in the directory only.
     * 
     * @return The size in bytes occupied by the files included in the directory, 
     * or -1 if the directory does not exist or is not accessible.
     * 
     * @throws IllegalArgumentException if the <code>url</code> is invalid.
     * @throws IOException 
     * <ul>
     * <li>if the method is invoked on a file.</li>
     * <li>if the firewall disallows a connection that is not btspp or comm.</li>
     * </ul>
     * @throws SecurityException if the security of the application does not have 
     * read access for the directory.
     */
    public static long getDirectorySize(String url, boolean includeSubDirs) throws IOException {
        FileConnection fc = null;
        try {
            fc = (FileConnection) Connector.open(url, Connector.READ);
            return fc.directorySize(includeSubDirs);
        } finally {
            IOUtils.safelyCloseStream(fc);
        }
    }
    
    /**
     * Determines the free memory that is available on the file system the file or directory resides on.
     * This may only be an estimate and may vary based on platform-specific file system 
     * blocking and metadata information.
     * 
     * @param url - URL to a file or directory to be processed.
     * 
     * @return The available size in bytes on a file system, 
     * or -1 if the file system is not accessible.
     * 
     * @throws IllegalArgumentException if the <code>url</code> is invalid.
     * @throws IOException if the firewall disallows a connection that is not btspp or comm.
     * @throws SecurityException if the security of the application does 
     * not have read access to the root volume.
     */
    public static long getAvailableFileSystemSize(String url) throws IOException {
        FileConnection fc = null;
        try {
            fc = (FileConnection) Connector.open(url, Connector.READ);
            return  fc.availableSize();
        } finally {
            IOUtils.safelyCloseStream(fc);
        }
    }
    
    /**
     * Some devices may provide localized error messages. So it may be uncomfortable
     * for the support team to understand a localized error message. 
     * This method is aimed to help providing error messages in English.
     * 
     * @param e - instance of FileIOException to be processed.
     * @return A human readable English string error message for the FileIOException.
     */
    public static String getFileIOExceptionInfo(FileIOException e) {
        final int errorCode = e.getErrorCode();
        if (errorCode == FileIOException.CONTENT_BUILT_IN) {
            return "The operation is not allowed because the content is built-in (preloaded)";
        } else if (errorCode == FileIOException.DIRECTORY_ALREADY_EXISTS) {
            return "The directory already exists";
        } else if (errorCode == FileIOException.DIRECTORY_FULL) {
            return "The directory is full";
        } else if (errorCode == FileIOException.DIRECTORY_NOT_EMPTY) {
            return "The directory is not empty";
        } else if (errorCode == FileIOException.DIRECTORY_NOT_FOUND) {
            return "The directory cannot be found";
        } else if (errorCode == FileIOException.FILE_BUSY) {
            return "The operation failed because file is currently opened";
        } else if (errorCode == FileIOException.FILE_HANDLES_OPEN) {
            return "The file handle used is already open";
        } else if (errorCode == FileIOException.FILE_NOT_OPEN) {
            return "The file is no longer open";
        } else if (errorCode == FileIOException.FILE_SYSTEM_UNAVAILABLE) {
            return "The file system is unavailable";
        } else if (errorCode == FileIOException.FILE_TOO_LARGE) {
            return "File exceeds the maximum file size of the destination";
        } else if (errorCode == FileIOException.FILENAME_ALREADY_EXISTS) {
            return "The filename already exists";
        } else if (errorCode == FileIOException.FILENAME_NOT_FOUND) {
            return "The filename cannot be found";
        } else if (errorCode == FileIOException.FILENAME_TOO_LONG) {
            return "The filename is too long";
        } else if (errorCode == FileIOException.FILESYSTEM_EMPTY) {
            return "The file system is empty";
        } else if (errorCode == FileIOException.FILESYSTEM_FULL) {
            return "The file system is full";
        } else if (errorCode == FileIOException.FS_ALREADY_MOUNTED) {
            return "The file system is already mounted";
        } else if (errorCode == FileIOException.FS_LOCKED_BY_OTHER_DEVICE) {
            return "The operation failed because the sdcard is already locked by another device";
        } else if (errorCode == FileIOException.FS_NOT_MOUNTED) {
            return "The file system is not mounted";
        } else if (errorCode == FileIOException.FS_VERIFICATION_FAILED) {
            return "The file system failed to be mounted because of a verification error";
        } else if (errorCode == FileIOException.GENERAL_ERROR) {
            return "A general error occurred";
        } else if (errorCode == FileIOException.INVALID_CHARACTERS) {
            return "The string specified contains invalid characters";
        } else if (errorCode == FileIOException.INVALID_HANDLE) {
            return "The file system handle used in the file operation is currently invalid";
        } else if (errorCode == FileIOException.INVALID_OPERATION) {
            return "The operation requested is invalid";
        } else if (errorCode == FileIOException.INVALID_PARAMETER) {
            return "The file system received an invalid parameter";
        } else if (errorCode == FileIOException.IS_A_DIRECTORY) {
            return "The filename requested is a directory";
        } else if (errorCode == FileIOException.MEDIUM_NOT_FORMATTED) {
            return "The medium is not formatted";
        } else if (errorCode == FileIOException.NO_FREE_HANDLES) {
            return "There are no more free handles";
        } else if (errorCode == FileIOException.NO_SUCH_ROOT) {
            return "The root specified is not available";
        } else if (errorCode == FileIOException.NOT_A_DIRECTORY) {
            return "The filename requested is not a directory";
        } else if (errorCode == FileIOException.NOT_A_FILE) {
            return "The filename requested is not a file";
        } else if (errorCode == FileIOException.OS_BUSY) {
            return "The operating system is busy";
        } else if (errorCode == FileIOException.STREAM_ALREADY_OPENED) {
            return "The requested stream is already opened";
        } else {
            return "no additional info";
        }
    }
    
    /**
     * Determines the total size of the file system the connection's target resides on.
     * 
     * @param url - URL to a file or directory to be processed.
     * @return The total size of the file system in bytes, or -1 if the file system is not accessible.
     * 
     * @throws IllegalArgumentException if the <code>url</code> is invalid.
     * @throws IOException if the firewall disallows a connection that is not btspp or comm.
     * @throws SecurityException if the security of the application does 
     * not have read access to the root volume.
     */
    public static long getTotalFileSystemSize(String url) throws IOException {
        FileConnection fc = null;
        try {
            fc = (FileConnection) Connector.open(url, Connector.READ);
            return fc.totalSize();
        } finally {
            IOUtils.safelyCloseStream(fc);
        }
    }
    
    /**
     * Determines the used memory of a file system the connection's target resides on. 
     * This may only be an estimate and may vary based on platform-specific 
     * file system blocking and metadata information.
     * 
     * @param url - URL to a file or directory to be processed.
     * @return The used size of bytes on a file system the connection's target resides on, 
     * or -1 if the file system is not accessible.
     * 
     * @throws SecurityException - if the security of the application does not have 
     * read access to the root volume.
     * @throws IOException if the firewall disallows a connection that is not btspp or comm.
     * @throws IllegalArgumentException if the <code>url</code> is invalid.
     */
    public static long getUsedFileSystemSize(String url) throws IOException {
        FileConnection fc = null;
        try {
            fc = (FileConnection) Connector.open(url, Connector.READ);
            return fc.usedSize();
        } finally {
            IOUtils.safelyCloseStream(fc);
        }
    }
    
    /**
     * Determines the used memory of a DeviceMemory's file system. 
     * This may only be an estimate and may vary based on platform-specific 
     * file system blocking and metadata information.
     * 
     * @return The used size of bytes on a DeviceMemory's file system, 
     * or -1 if the file system is not accessible.
     * 
     * @throws SecurityException - if the security of the application does not have 
     * read access to the DeviceMemory root volume.
     * @throws IOException if the firewall disallows a connection that is not btspp or comm.
     * @throws IllegalArgumentException if the {@link #DEVICE_MEMORY_ROOT} 
     * happens to be invalid for the current device.
     */
    public static long getUsedDeviceMemory() throws IOException {
        return getUsedFileSystemSize(DEVICE_MEMORY_ROOT);
    }
    
    /**
     * Determines the free memory that is available on the DeviceMemory's file system.
     * This may only be an estimate and may vary based on platform-specific file system 
     * blocking and metadata information.
     * 
     * @return The available size in bytes on the DeviceMemory's file system, 
     * or -1 if the file system is not accessible.
     * 
     * @throws IllegalArgumentException if {@link #DEVICE_MEMORY_ROOT} 
     * happens to be invalid for the current device.
     * @throws IOException if the firewall disallows a connection that is not btspp or comm.
     * @throws SecurityException if the security of the application does 
     * not have read access to the DeviceMemory root volume.
     */
    public static long getAvailableDeviceMemory() throws IOException {
        return getAvailableFileSystemSize(DEVICE_MEMORY_ROOT);
    }
    
    /**
     * Determines the total size of the DeviceMemory's file system.
     * 
     * @return The total size of the DeviceMemory's file system in bytes, 
     * or -1 if the file system is not accessible.
     * 
     * @throws IllegalArgumentException if {@link #DEVICE_MEMORY_ROOT} 
     * happens to be invalid for the current device.
     * @throws IOException if the firewall disallows a connection that is not btspp or comm.
     * @throws SecurityException if the security of the application does 
     * not have read access to the DeviceMemory root volume.
     */
    public static long getTotalDeviceMemory() throws IOException {
        return getTotalFileSystemSize(DEVICE_MEMORY_ROOT);
    }
    
    /**
     * Determines the used memory of a SDCard's file system. 
     * This may only be an estimate and may vary based on platform-specific 
     * file system blocking and metadata information.
     * 
     * @return The used size of bytes on a SDCard's file system, 
     * or -1 if the file system is not accessible.
     * 
     * @throws SecurityException - if the security of the application does not have 
     * read access to the SDCard root volume.
     * @throws IOException if the firewall disallows a connection that is not btspp or comm.
     * @throws IllegalArgumentException if the {@link #CARD_ROOT} 
     * happens to be invalid for the current device.
     */
    public static long getUsedSDCardSize() throws IOException {
        return getUsedFileSystemSize(CARD_ROOT);
    }
    
    /**
     * Determines the free memory that is available on the SDCard's file system.
     * This may only be an estimate and may vary based on platform-specific file system 
     * blocking and metadata information.
     * 
     * @return The available size in bytes on the SDCard's file system, 
     * or -1 if the file system is not accessible.
     * 
     * @throws IllegalArgumentException if {@link #CARD_ROOT} 
     * happens to be invalid for the current device.
     * @throws IOException if the firewall disallows a connection that is not btspp or comm.
     * @throws SecurityException if the security of the application does 
     * not have read access to the SDCard's root volume.
     */
    public static long getAvailableSDCardSize() throws IOException {
        return getAvailableFileSystemSize(CARD_ROOT);
    }
    
    /**
     * Determines the total size of the SDCard's file system.
     * 
     * @return The total size of the SDCard's file system in bytes, 
     * or -1 if the file system is not accessible.
     * 
     * @throws IllegalArgumentException if {@link #CARD_ROOT} 
     * happens to be invalid for the current device.
     * @throws IOException if the firewall disallows a connection that is not btspp or comm.
     * @throws SecurityException if the security of the application does 
     * not have read access to the SDCard's root volume.
     */
    public static long getTotalSDCardSize() throws IOException {
        return getTotalFileSystemSize(CARD_ROOT);
    }
    
    /**
     * @param e - {@link Throwable} to process.
     * @return True if <code>e</code> is an instance of {@link FileIOException} and 
     * its error code is {@link FileIOException#FILESYSTEM_FULL}, otherwise - false. 
     */
    public static boolean isFileSystemFullException(Throwable e) {
        return (e instanceof FileIOException) && 
            (((FileIOException)e).getErrorCode() == FileIOException.FILESYSTEM_FULL);
    }
    
    /**
     * This method creates a file for {@link #TEST_CARD_ENCRYPTION_FILE} url 
     * (if already exists - deletes its first) and then checks for the got file extension.
     * It was observed that if SDCard Encryption is ON, then newly created files get ".rem" extension.
     * The method relies on this OS behavior to detect the SDCard Encryption state.
     * 
     * @return True if SDCard Encryption is ON, otherwise false.
     *  
     * @throws IllegalArgumentException if the {@link #TEST_CARD_ENCRYPTION_FILE} url is invalid.
     * @throws SecurityException if the security of the application does not have 
     * both read and write access for the connection's target.
     * @throws IOException if the target the target is unaccessible, or an unspecified error occurs 
     * preventing deletion of the target, or if the firewall disallows a connection that is 
     * not btspp or comm.
     */
    public static boolean isSDCardEncryptionEnabled() throws IOException {
        FileConnection fc = null;
        try {
            fc = (FileConnection) Connector.open(TEST_CARD_ENCRYPTION_FILE);
            if (fc.exists()) { 
                fc.delete(); 
            }
            fc.create();
            return fc.getName().endsWith(ENCR_FILE_EXTENSION);
        } finally {
            try {
                if (fc != null && fc.exists()) { fc.delete(); }
            } catch (Exception e) {
                Logger.debug("Failed to cleanup after IOUtils.isSDCardEncryptionEnabled(): " + e);
            } finally {
                safelyCloseStream(fc);
            }
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
