/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 3.0.10
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package co.humaniq.faceenginemobile.bindings;

public final class Version {
    public final static Version VERSION_MAJOR = new Version("VERSION_MAJOR", WrapperJNI.VERSION_MAJOR_get());
    public final static Version VERSION_MINOR = new Version("VERSION_MINOR", WrapperJNI.VERSION_MINOR_get());
    public final static Version VERSION_PATCH = new Version("VERSION_PATCH", WrapperJNI.VERSION_PATCH_get());
    private static Version[] swigValues = {VERSION_MAJOR, VERSION_MINOR, VERSION_PATCH};
    private static int swigNext = 0;
    private final int swigValue;
    private final String swigName;

    private Version(String swigName) {
        this.swigName = swigName;
        this.swigValue = swigNext++;
    }

    private Version(String swigName, int swigValue) {
        this.swigName = swigName;
        this.swigValue = swigValue;
        swigNext = swigValue + 1;
    }

    private Version(String swigName, Version swigEnum) {
        this.swigName = swigName;
        this.swigValue = swigEnum.swigValue;
        swigNext = this.swigValue + 1;
    }

    public static Version swigToEnum(int swigValue) {
        if (swigValue < swigValues.length && swigValue >= 0 && swigValues[swigValue].swigValue == swigValue)
            return swigValues[swigValue];
        for (Version swigValue1 : swigValues)
            if (swigValue1.swigValue == swigValue)
                return swigValue1;
        throw new IllegalArgumentException("No enum " + Version.class + " with value " + swigValue);
    }

    public final int swigValue() {
        return swigValue;
    }

    public String toString() {
        return swigName;
    }
}

