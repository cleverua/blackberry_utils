package com.cleverua.bb.utils;

import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.util.Arrays;

/**
 * @author Vitaliy Khudenko, CleverUA, 2010
 */
public class SoftwareVersionUtils {

    /**
     * @return a String got by calling the
     * <code>net.rim.device.api.system.DeviceInfo.getSoftwareVersion()</code>,
     * e.g. "5.0.0.145".
     */
    public final static String SOFTWARE_VERSION = DeviceInfo.getSoftwareVersion();

    /**
     * @return An OsVersion instance that is a wrapper over the
     *         {@link #SOFTWARE_VERSION} String. It has handy methods for comparing
     *         to another OsVersion.
     *
     * @see {@link OsVersion#isEqualTo(OsVersion other)
     *      OsVersion.isEqualTo(OsVersion other)}
     * @see {@link OsVersion#isEqualTo(OsVersion other, int precision)
     *      OsVersion.isEqualTo(OsVersion other, int precision)} 
     * @see {@link OsVersion#isGreaterThan(OsVersion other)
     *      OsVersion.isGreaterThan(OsVersion other)}
     * @see {@link OsVersion#isGreaterThanOrEquals(OsVersion other)
     *      OsVersion.isGreaterThanOrEquals(OsVersion other)}
     * @see {@link OsVersion#isLessThan(OsVersion other)
     *      OsVersion.isLessThan(OsVersion other)}
     * @see {@link OsVersion#isLessThanOrEquals(OsVersion other)
     *      OsVersion.isLessThanOrEquals(OsVersion other)}
     * @see {@link OsVersion#isGreaterThan(OsVersion other, int precision)
     *      OsVersion.isGreaterThan(OsVersion other, int precision)}
     * @see {@link OsVersion#isGreaterThanOrEquals(OsVersion other, int precision)
     *      OsVersion.isGreaterThanOrEquals(OsVersion other, int precision)}
     * @see {@link OsVersion#isLessThan(OsVersion other, int precision)
     *      OsVersion.isLessThan(OsVersion other, int precision)}
     * @see {@link OsVersion#isLessThanOrEquals(OsVersion other, int precision)
     *      OsVersion.isLessThanOrEquals(OsVersion other, int precision)}
     */
    public final static OsVersion OS_VERSION = new OsVersion(SOFTWARE_VERSION);

    /**
     * This is an OS version string wrapper, that provides a bunch of utility
     * methods, e.g. for comparing OS versions.
     * 
     * This is an immutable object.
     */
    public static class OsVersion {

        /**
         * Normal version string consists of 4 parts delimited with the dot char
         * ('.'), e.g. "5.0.0.129".
         */
        public final static int PARTS_SIZE = 4;
        
        private static final String DELIMITER = ".";
        private static final int PART_WEIGHT_BASE = 10;
        private static final int MAX_PART_WEIGHT  = getMaxPartWeight();
        
        private String version;
        private int[] parts;

        /**
         * @param String
         *             version, e.g. "5.0.0.145".
         * 
         * @throws NullPointerException
         *             if passed version String is null.
         * @throws IllegalStateException
         *             if passed version String does not matches the "a.b.c.d"
         *             pattern (a group of 4 non-empty string parts joined by a dot).
         * @throws NumberFormatException
         *             if at least one of the parts got by splitting the passed
         *             version String with a dot is not a parsable integer.
         * 
         */
        public OsVersion(String version) {
            this.version = version.trim();
            final String[] strParts = StringUtils.split(version, DELIMITER);
            if (strParts.length != PARTS_SIZE) {
                throw new IllegalStateException(
                    "Software version \"" + version + "\" consists of unexpected number " +
                    "of parts (" + strParts.length + "). Expected is " + PARTS_SIZE + '.'
                );
            }
            parts = new int[PARTS_SIZE];
            for (int i = 0; i < PARTS_SIZE; i++) {
                parts[i] = Integer.parseInt(strParts[i]);
            }
        }
        
        /**
         * @return full version String, e.g. "5.0.0.145"
         */
        public String getVersion() {
            return version;
        }
        
        /**
         * @return version String of a passed precision. E.g. for a version
         *         "5.0.0.145" and precision of 2, the result is "5.0".
         * 
         * @throws IllegalArgumentException
         *             if precision is less than 1 or greater than
         *             {@link #PARTS_SIZE}
         */
        public String getVersion(int precision) {
            validatePrecision(precision);
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < precision; i++) {
                sb.append(parts[i]);
                if ((i + 1) != precision) {
                    sb.append('.');
                }
            }
            return sb.toString();
        }
        
        public String toString() {
            return "OsVersion " + version;
        }
        
        /**
         * For a version string "5.0.0.145" this method 
         * returns an Array of integers { 5, 0, 0, 145 }.
         */
        public int[] getParts() {
            return Arrays.copy(parts);
        }

        /**
         * <p>If <code>obj</code> is <code>null</code> - returns false.</p>
         * <p>If <code>obj</code> is not an instance of OSVersion class - returns
         * false.</p>
         * <p>If <code>obj</code> is the same as <code>this</code> - returns true.</p>
         * <p>Otherwise returns result of comparison of the underlying version
         * strings (using String.equals(Object anObject)).</p>
         * 
         */
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            
            if (obj == this) {
                return true;
            }
            
            if (!(obj instanceof OsVersion)) {
                return false;
            }
            
            return ((OsVersion) obj).getVersion().equals(version);
        }

        /**
         * @return true if this version is equal to other version, false
         *         otherwise.
         * 
         * @throws NullPointerException
         *             if <code>other</code> is <code>null</code>.
         */
        public boolean isEqualTo(OsVersion other) {
            return getResultOfComparison(other, PARTS_SIZE) == 0;
        }
        
        /**
         * @return true if this version is equal to other version, false
         *         otherwise.
         * 
         *         <p>
         *         Precision is used to compare only some part of the
         *         version. For instance, if precision is 2, then for
         *         "4.5.0.245" and "4.6.1.45", only "4.5" and "4.6" will be
         *         compared.
         *         </p>
         * 
         * @throws NullPointerException
         *             if <code>other</code> is <code>null</code>.
         *             
         * @throws IllegalArgumentException
         *             if <code>precision</code> is less than 1 or greater than
         *             {@link #PARTS_SIZE}
         */
        public boolean isEqualTo(OsVersion other, int precision) {
            validatePrecision(precision);
            return getResultOfComparison(other, precision) == 0;
        }
        
        /**
         * @return true if this version is greater than other version, false
         *         otherwise.
         * 
         * @throws NullPointerException
         *             if <code>other</code> is <code>null</code>.
         */
        public boolean isGreaterThan(OsVersion other) {
            return getResultOfComparison(other, PARTS_SIZE) > 0;
        }

        /**
         * @return true if this version is greater than or equal to other
         *         version, false otherwise.
         * 
         * @throws NullPointerException
         *             if <code>other</code> is <code>null</code>.
         */
        public boolean isGreaterThanOrEquals(OsVersion other) {
            return getResultOfComparison(other, PARTS_SIZE) >= 0;
        }
        
        /**
         * @return true if this version is less than other version, false
         *         otherwise.
         * 
         * @throws NullPointerException
         *             if <code>other</code> is <code>null</code>.
         */
        public boolean isLessThan(OsVersion other) {
            return getResultOfComparison(other, PARTS_SIZE) < 0;
        }
        
        /**
         * @return true if this version is less than or equal to other
         *         version, false otherwise.
         * 
         * @throws NullPointerException
         *             if <code>other</code> is <code>null</code>.
         */
        public boolean isLessThanOrEquals(OsVersion other) {
            return getResultOfComparison(other, PARTS_SIZE) <= 0;
        }

        /**
         * @return true if this version is greater than other version, false
         *         otherwise.
         *         
         *         <p>
         *         Precision is used to compare only some part of the
         *         version. For instance, if precision is 2, then for
         *         "4.5.0.245" and "4.6.1.45", only "4.5" and "4.6" will be
         *         compared.
         *         </p>
         * 
         * @throws NullPointerException
         *             if <code>other</code> is <code>null</code>.
         * 
         * @throws IllegalArgumentException
         *             if <code>precision</code> is less than 1 or greater than
         *             {@link #PARTS_SIZE}
         */
        public boolean isGreaterThan(OsVersion other, int precision) {
            validatePrecision(precision);
            return getResultOfComparison(other, precision) > 0;
        }

        /**
         * @return true if this version is greater than or equal to other
         *         version, false otherwise.
         * 
         *         <p>
         *         Precision is used to compare only some part of the version.
         *         For instance, if precision is 2, then for "4.5.0.245" and
         *         "4.6.1.45", only "4.5" and "4.6" will be compared.
         *         </p>
         * 
         * @throws NullPointerException
         *             if <code>other</code> is <code>null</code>.
         * 
         * @throws IllegalArgumentException
         *             if <code>precision</code> is less than 1 or greater than
         *             {@link #PARTS_SIZE}
         */
        public boolean isGreaterThanOrEquals(OsVersion other, int precision) {
            validatePrecision(precision);
            return getResultOfComparison(other, precision) >= 0;
        }

        /**
         * @return true if this version is less than other version, false
         *         otherwise.
         *         
         *         <p>
         *         Precision is used to compare only some part of the
         *         version. For instance, if precision is 2, then for
         *         "4.5.0.245" and "4.6.1.45", only "4.5" and "4.6" will be
         *         compared.
         *         </p>
         * 
         * @throws NullPointerException
         *             if <code>other</code> is <code>null</code>.
         * 
         * @throws IllegalArgumentException
         *             if <code>precision</code> is less than 1 or greater than
         *             {@link #PARTS_SIZE}
         */
        public boolean isLessThan(OsVersion other, int precision) {
            validatePrecision(precision);
            return getResultOfComparison(other, precision) < 0;
        }

        /**
         * @return true if this version is less than or equal to other
         *         version, false otherwise.
         * 
         *         <p>
         *         Precision is used to compare only some part of the version.
         *         For instance, if precision is 2, then for "4.5.0.245" and
         *         "4.6.1.45", only "4.5" and "4.6" will be compared.
         *         </p>
         * 
         * @throws NullPointerException
         *             if <code>other</code> is <code>null</code>.
         * 
         * @throws IllegalArgumentException
         *             if <code>precision</code> is less than 1 or greater than
         *             {@link #PARTS_SIZE}
         */
        public boolean isLessThanOrEquals(OsVersion other, int precision) {
            validatePrecision(precision);
            return getResultOfComparison(other, precision) <= 0;
        }

        private void validatePrecision(int precision) {
            if (precision < 1 || precision > PARTS_SIZE) {
                throw new IllegalArgumentException(
                    "Invalid precision (" + precision + "). " +
                    "Valid precision should be in [1.." + PARTS_SIZE + "] range."
                );
            }
        }
        
        private int getResultOfComparison(OsVersion other, int precision) {
            final int[] otherParts = other.getParts();
            int result = 0;
            for (int i = 0, weight = MAX_PART_WEIGHT; i < precision; i++, weight /= PART_WEIGHT_BASE) {
                if (parts[i] != otherParts[i]) {
                    result += (parts[i] > otherParts[i] ? 1 : -1) * weight;
                }
            }
            return result;
        }
        
        private static int getMaxPartWeight() {
            int result = 1;
            for (int i = 1; i < PARTS_SIZE; i++) {
                result *= PART_WEIGHT_BASE;
            }
            return result;
        }
    }
}
