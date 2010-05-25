package com.cleverua.bb.utils;

import java.io.IOException;
import java.io.InputStream;

import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;

import net.rim.device.api.io.FileNotFoundException;
import net.rim.device.api.math.Fixed32;
import net.rim.device.api.system.EncodedImage;

public class ImageUtils {

    /**
     * @return EncodedImage - a copy of eImage resized to new dimensions (toWidth x toHeight)
     */
    public static EncodedImage resize(EncodedImage eImage, int toWidth, int toHeight, 
            boolean keepAspectRatio) {
    
        int scaleX = Fixed32.div(Fixed32.toFP(eImage.getWidth()),  Fixed32.toFP(toWidth));
        int scaleY = Fixed32.div(Fixed32.toFP(eImage.getHeight()), Fixed32.toFP(toHeight));
        
        if (keepAspectRatio) {
            int scale = (scaleX > scaleY) ? scaleX : scaleY;
            return eImage.scaleImage32(scale, scale);
        } else {
            return eImage.scaleImage32(scaleX, scaleY);
        }
    }
    
    /**
     * Reads the image file data from the file system, creates an EncodedImage instance from 
     * the image file data and resizes the EncodedImage to the specified width/height keeping 
     * the aspect ratio of the original image.
     * 
     * @param imgUrl - url of a file to process
     * @param toWidth - desired width
     * @param toHeight - desired height
     * @return EncodedImage instance scaled to (toWidth x toHeight). 
     * Note that scaling is done with keeping the aspect ratio of the source image, so if aspect ratio
     * of desired size is not equal to source image aspect ratio then new dimensions will be filled in
     * the desired width/height.
     * @throws IOException
     * <ul> 
     * <li>if the firewall disallows a connection for <code>imgUrl</code>.</li>
     * <li>if an I/O error occurs, if the method is invoked on a directory, 
     * if the connection's target does not yet exist, 
     * or the connection's target is not accessible.</li>
     * </ul>
     * @throws FileNotFoundException if <code>imgUrl</code> is invalid 
     * for <code>Connector.open(imgUrl)</code>.
     */
    public static EncodedImage getResizedImage(String imgUrl, int toWidth, int toHeight) 
            throws IOException {

        InputStream in = null;
        FileConnection fc = null;
        
        try {
            try {
                fc = (FileConnection) Connector.open(imgUrl);
            } catch (IllegalArgumentException e) {
                // Logger.debug("getResizedImage: failed to open FileConnection for '" + imgUrl + "': " + e);
                throw new FileNotFoundException("Failed to open FileConnection for '" + imgUrl + '\'');
            }
        
            // Logger.debug("getResizedImage: going to open InputStream..");
            in = fc.openInputStream();

            // Logger.debug("getResizedImage: going to create byte[] data..");
            byte[] data = new byte[(int) fc.fileSize()];
        
            // Logger.debug("getResizedImage: byte[] data size = " + data.length + ", going to read..");
            in.read(data);

            // Logger.debug("getResizedImage: going to create EncodedImage..");
            EncodedImage eImage = EncodedImage.createEncodedImage(data, 0, data.length);
        
            // Logger.debug("getResizedImage: going to scale and return..");
            return resize(eImage, toWidth, toHeight, true);
        
        } finally {
            IOUtils.safelyCloseStream(in);
            IOUtils.safelyCloseStream(fc);
        }
    }
}
