package com.cleverua.bb.utils;

/**
 * <p>The class provides convenient math rounding methods the RIM API 4.5 does not provide.</p>
 * <p><b>IMPORTANT:</b> for API 4.6.0+ use <b>net.rim.device.api.util.MathUtilities</b> class instead.</p>
 */
public class MathUtils {
    
    /**
     * <p>In RIM API 4.5 there is no such a helpful method.</p>
     * <p><b>IMPORTANT:</b> for API 4.6.0+ use <b>net.rim.device.api.util.MathUtilities</b> class instead.</p>
     * 
     * <p>
     * Returns the result of rounding the argument to an integer. The result is
     * equivalent to {@code (int) Math.floor(x+0.5)}.
     * </p>
     * 
     * Special cases:
     * <ul>
     * <li>{@code round(+0.0) = +0.0}</li>
     * <li>{@code round(-0.0) = +0.0}</li>
     * <li>{@code round(anything > Integer.MAX_VALUE) = Integer.MAX_VALUE}</li>
     * <li>{@code round(anything < Integer.MIN_VALUE) = Integer.MIN_VALUE}</li>
     * <li>{@code round(+infinity) = Integer.MAX_VALUE}</li>
     * <li>{@code round(-infinity) = Integer.MIN_VALUE}</li>
     * <li>{@code round(NaN) = +0.0}</li>
     * </ul>
     * 
     * @param x a floating-point value to be rounded to an {@code int}
     * @return the closest integer to the argument.
     */
    public static int round(float x) {
        // Check for NaN first.
        // (x != x) does the same as Float.isNaN(x), but a bit more effective.
        return (x != x) ? 0 : (int) Math.floor(x + 0.5d);
    }
    
    /**
     * <p>In RIM API 4.5 there is no such a helpful method.</p>
     * <p><b>IMPORTANT:</b> for API 4.6.0+ use <b>net.rim.device.api.util.MathUtilities</b> class instead.</p>
     * 
     * <p>
     * Returns the result of rounding the argument to an integer. The result is
     * equivalent to {@code (long) Math.floor(x+0.5)}.
     * </p>
     * 
     * Special cases:
     * <ul>
     * <li>{@code round(+0.0) = +0.0}</li>
     * <li>{@code round(-0.0) = +0.0}</li>
     * <li>{@code round(anything > Long.MAX_VALUE) = Long.MAX_VALUE}</li>
     * <li>{@code round(anything < Long.MIN_VALUE) = Long.MIN_VALUE}</li>
     * <li>{@code round(+infinity) = Long.MAX_VALUE}</li>
     * <li>{@code round(-infinity) = Long.MIN_VALUE}</li>
     * <li>{@code round(NaN) = +0.0}</li>
     * </ul>
     * 
     * @param x a floating-point value to be rounded to a {@code long}
     * @return the closest integer to the argument.
     */
    public static long round(double x) {
        // Check for NaN first.
        // (x != x) does the same as Double.isNaN(x), but a bit more effective.
        return (x != x) ? 0L : (long) Math.floor(x + 0.5d);
    }
}
