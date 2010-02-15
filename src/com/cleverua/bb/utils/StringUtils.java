package com.cleverua.bb.utils;

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

    private static class StrComparator implements Comparator {

        private static final StringComparator STRING_COMPARATOR = StringComparator.getInstance(true);

        public int compare(Object o1, Object o2) {
            return STRING_COMPARATOR.compare(o1.toString(), o2.toString());
        }
    }
}
