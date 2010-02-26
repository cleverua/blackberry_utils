package com.cleverua.bb.utils;

import java.util.Enumeration;

import net.rim.device.api.util.Comparator;
import net.rim.device.api.util.StringComparator;

public class StringUtils {

    public static StrComparator STRING_COMPARATOR = new StrComparator();

    private static final String EMPTY = "";

    public static boolean isBlank(String str) {
        return (str == null || str.length() == 0);
    }

    public static boolean isNotBlank(String str) {
        return !isBlank(str);
    }

    public static String safe(String str) {
        return str == null ? EMPTY : str;
    }

    public static String safe(String str, String defaultValue) {
        return str == null ? defaultValue : str;
    }

    public static int linesCount(String str) {
        if (str.length() == 0) {
            return 0;
        }
        
        int result = 1;
        for (int indx = 0; (indx = str.indexOf('\n', indx)) != -1; indx++, result++);
        
        return result;
    }

    // http://supportforums.blackberry.com/rim/board/message?board.id=java_dev&message.id=34183
    public static String replaceAll(String source, String pattern, String replacement) {
        if (source == null) {
            return EMPTY;
        }

        StringBuffer sb = new StringBuffer();
        int idx = -1;
        int patIdx = 0;

        while ((idx = source.indexOf(pattern, patIdx)) != -1) {
            sb.append(source.substring(patIdx, idx));
            sb.append(replacement);
            patIdx = idx + pattern.length();
        }
        sb.append(source.substring(patIdx));

        return sb.toString();
    }

    public static String removeBefore(String str, String substrToDelete) {
        if (str == null) {
            return str;
        }

        int index = str.indexOf(substrToDelete);
        if (index == -1) {
            return str;
        }
        return str.substring(index + substrToDelete.length());
    }

    /**
     * Returns a string containing the tokens joined by delimiters.
     * 
     * @param tokens an {@link Enumeration} of objects to be joined. 
     * Strings will be formed from the objects by calling <code>object.toString()</code>.
     * 
     * @see {@link java.util.Hashtable#elements() Hashtable.elements()}, 
     * {@link java.util.Hashtable#keys() Hashtable.keys()}, 
     * {@link java.util.Vector#elements() Vector.elements()}
     */
    public static String join(String delimiter, Enumeration tokens) {
        StringBuffer sb = new StringBuffer();
        boolean firstTime = true;
        while (tokens.hasMoreElements()) {
            Object token = tokens.nextElement();
            if (firstTime) {
                firstTime = false;
            } else {
                sb.append(delimiter);
            }
            sb.append(token);
        }
        return sb.toString();
    }
    
    /**
     * Returns a string containing the tokens joined by delimiters.
     * 
     * @param tokens an Array of objects to be joined. 
     * Strings will be formed from the objects by calling <code>object.toString()</code>.
     */
    public static String join(String delimiter, Object[] tokens) {
        StringBuffer sb = new StringBuffer();
        boolean firstTime = true;
        final int len = tokens.length;
        for (int i = 0; i < len; i++) {
            Object token = tokens[i];
            if (firstTime) {
                firstTime = false;
            } else {
                sb.append(delimiter);
            }
            sb.append(token);
        }
        return sb.toString();
    }
    
    private static class StrComparator implements Comparator {

        private static final StringComparator STRING_COMPARATOR = StringComparator.getInstance(true);

        public int compare(Object o1, Object o2) {
            return STRING_COMPARATOR.compare(o1.toString(), o2.toString());
        }
    }
}
