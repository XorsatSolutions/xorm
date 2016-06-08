package com.xorsat.xormlib;

import java.lang.reflect.Field;

/**
 * Created by khawar on 4/21/16.
 */
public class XorH {

    private static String TAG = "XorApi";

    protected static void handle(Exception ex) {
        throw new RuntimeException(ex);
    }

    protected static void handle(Exception ex, String tag) {
        throw new RuntimeException(ex);
    }


    protected static boolean isDouble(Field mField) {
        if (mField.getType().toString().equalsIgnoreCase("class java.lang.Double") || mField.getType().toString().equalsIgnoreCase("double")) {
            return true;
        }
        return false;
    }

    protected static boolean isInteger(Field mField) {
        if (mField.getType().toString().equalsIgnoreCase("class java.lang.Integer") || mField.getType().toString().equalsIgnoreCase("int")) {
            return true;
        }
        return false;
    }

    protected static boolean isString(Field mField) {
        if (mField.getType().toString().equalsIgnoreCase("class java.lang.String")) {
            return true;
        }
        return false;
    }

    protected static boolean isFloat(Field mField) {
        if (mField.getType().toString().equalsIgnoreCase("class java.lang.Float") || mField.getType().toString().equalsIgnoreCase("float")) {
            return true;
        }
        return false;
    }

    protected static boolean isLong(Field mField) {
        if (mField.getType().toString().equalsIgnoreCase("class java.lang.Long") || mField.getType().toString().equalsIgnoreCase("long")) {
            return true;
        }
        return false;
    }

    protected static boolean isBoolean(Field mField) {
        if (mField.getType().toString().equalsIgnoreCase("class java.lang.Boolean") || mField.getType().toString().equalsIgnoreCase("bool")) {
            return true;
        }
        return false;
    }

    protected static Double getDouble(String value) {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            handle(e);
        }
        return 0.0;
    }


    protected static Float getFloat(String value) {
        try {
            return Float.parseFloat(value);
        } catch (NumberFormatException e) {
            handle(e);
        }
        return 0.f;
    }

    protected static Integer getInteger(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            handle(e);
        }
        return 0;
    }

    protected static Long getLong(String value) {
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            handle(e);
        }
        return 0l;
    }

    protected static Boolean getBoolean(String value) {
        try {
            return Boolean.parseBoolean(value);
        } catch (NumberFormatException e) {
            handle(e);
        }
        return false;
    }

}
