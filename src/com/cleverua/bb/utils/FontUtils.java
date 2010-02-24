package com.cleverua.bb.utils;

import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.FontFamily;

public class FontUtils { 
    
    /**
     * If the application doesn't set its own default font, 
     * then it automatically inherits this system font.
     */
    public static final Font SYSTEM_FONT = Font.getDefault();
    
    // application font parameters
    private static final String PREFERRED_APP_FONT_FAMILY = "BBAlpha Sans";
    private static final int APP_FONT_STYLE               = Font.PLAIN;
    private static final int APP_FONT_HEIGHT              = 22;
	
    /**
     * Sets the default font for this application in accordance with 
     * {@link PREFERRED_APP_FONT_FAMILY}, {@link APP_FONT_STYLE} and {@link APP_FONT_HEIGHT}.
     * 
     * <p>
     * If {@link PREFERRED_APP_FONT_FAMILY} is not supported by current device, 
     * then font family remains unchanged.
     * </p>
     */
    public static void setApplicationDefaultFont() {
        
        FontFamily fontFamily = SYSTEM_FONT.getFontFamily();
        
        FontFamily[] families = FontFamily.getFontFamilies();
        
        final int familiesLength = families.length;
        for (int i = 0; i < familiesLength; i++) {
            final FontFamily family = families[i];
            if (family.getName().equalsIgnoreCase(PREFERRED_APP_FONT_FAMILY)) {
                fontFamily = family;
                break;
            }
        }
        
        Font.setDefaultFont(fontFamily.getFont(APP_FONT_STYLE, APP_FONT_HEIGHT));
    }
    
    /**
     * @return Application's current default font. 
     * If no default is set for the application, return the system font.
     */
    public static Font getApplicationDefaultFont() {
        return Font.getDefault();
    }
}
