package com.xorsat.xormlib;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;

/**
 * Created by khawar on 4/21/16.
 */
public class XorJson {

    public ArrayList<Object> parse(Class mClass, String strJson) {
        ArrayList<Object> array_list = new ArrayList<>();
        try {
            if (strJson.length() > 0 && mClass.isAnnotationPresent(XorType.class)) {
                Annotation annotation = mClass.getAnnotation(XorType.class);
                XorType mXorType = (XorType) annotation;
                String strArrayName = mXorType.JsonArrayName();
                JSONObject mJSONObjectMain = new JSONObject(strJson);
                JSONArray mJSONArray = mJSONObjectMain.getJSONArray(strArrayName);
                for (int i = 0; i < mJSONArray.length(); i++) {
                    JSONObject mJSONObject = mJSONArray.getJSONObject(i);
                    Object item = getObjectFromJson(mClass, mJSONObject);
                    array_list.add(item);
                }
            }

        } catch (Exception e) {
            XorH.handle(e);
        }
        return array_list;
    }

    public Object parseArrayWithoutKey(Class mClass, String strJson) {
        ArrayList<Object> array_list = new ArrayList<>();
        try {
//            Annotation annotation = mClass.getAnnotation(XorType.class);
//            XorType mXorType = (XorType) annotation;
//            String strArrayName = mXorType.JsonArrayName();
//            JSONObject mJSONObjectMain = new JSONObject(strJson);
            JSONArray mJSONArray = new JSONArray(strJson);
            for (int i = 0; i < mJSONArray.length(); i++) {
                JSONObject mJSONObject = mJSONArray.getJSONObject(i);
                Object item = getObjectFromJson(mClass, mJSONObject);
                array_list.add(item);
            }


        } catch (Exception e) {
            XorH.handle(e);
        }
        return array_list;
    }

    public Object getObjectFromJson(Class mClass, JSONObject mJSONObject) throws Exception {
        Object newObject = new Object();
        try {
            newObject = (Object) mClass.newInstance();
            Field[] fieldsArray = mClass.getDeclaredFields();
            for (Field mField : fieldsArray) {
                if (mField.isAnnotationPresent(XorField.class)) {
                    Annotation fieldAnnotation = mField.getAnnotation(XorField.class);
                    XorField mXorField = (XorField) fieldAnnotation;
                    String strKeyName = mXorField.JsonKeyName();
                    Method[] methods = mClass.getMethods();
                    for (Method method : methods) {
                        if (isSetter(method)) {
                            if (method.getName().toString().substring(3).equalsIgnoreCase(mField.getName())) {
                                if (mJSONObject.has(strKeyName) || !mJSONObject.isNull(strKeyName)) {
                                    String strValue = mJSONObject.getString(strKeyName);
                                    if (XorH.isInteger(mField)) {
                                        method.invoke(newObject, XorH.getInteger(strValue));
                                    } else if (XorH.isDouble(mField)) {
                                        method.invoke(newObject, XorH.getDouble(strValue));
                                    } else if (XorH.isLong(mField)) {
                                        method.invoke(newObject, XorH.getLong(strValue));
                                    } else if (XorH.isBoolean(mField)) {
                                        method.invoke(newObject, XorH.getBoolean(strValue));
                                    } else if (XorH.isFloat(mField)) {
                                        method.invoke(newObject, XorH.getFloat(strValue));
                                    } else if (XorH.isString(mField)) {
                                        method.invoke(newObject, strValue);
                                    }

                                } else {
                                    method.invoke(newObject, "");
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            XorH.handle(e);
        }
        return newObject;
    }

    public Object getObjectFromJson(Class mClass, String mJSONString) throws Exception {
        Object newObject = new Object();
        try {
            JSONObject mJSONObject = new JSONObject(mJSONString);
            newObject = getObjectFromJson(mClass, mJSONObject);
        } catch (JSONException e) {
            XorH.handle(e);
        }
        return newObject;
    }

    public JSONObject getJsonFromObject(Object mObject) throws Exception {
        JSONObject mJSONObject = new JSONObject();
        try {
            Field[] fieldsArray = mObject.getClass().getDeclaredFields();
            for (Field mField : fieldsArray) {
                String strColumnName = getJsonKeyName(mField);
                if (strColumnName.length() > 0) {
                    Method[] methods = mObject.getClass().getMethods();
                    for (Method method : methods) {
                        if (isGetter(method)) {
                            if (method.getName().toString().substring(3).equalsIgnoreCase(mField.getName())) {
                                if (mField.getType() == String.class) {
                                    String returnValue = (String) method.invoke(mObject, null);
                                    mJSONObject.put(strColumnName, returnValue);
                                } else {
                                    long returnValue = (long) method.invoke(mObject, null);
                                    mJSONObject.put(strColumnName, returnValue);
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            XorH.handle(e);
        }
        return mJSONObject;
    }

    public static boolean isSetter(Method method) {
        if (!method.getName().startsWith("set")) return false;
        if (method.getParameterTypes().length != 1) return false;
        return true;
    }

    private static boolean isGetter(Method method) {
        try {
            if (!method.getName().startsWith("get")) return false;
            if (method.getParameterTypes().length != 0) return false;
            if (void.class.equals(method.getReturnType())) return false;
        } catch (Exception e) {
            XorH.handle(e, "isGetter");
        }
        return true;
    }

    private String getJsonArrayName(Class mClass) {
        String strJsonArrayName = "";
        if (mClass.isAnnotationPresent(XorType.class)) {
            Annotation annotation = mClass.getAnnotation(XorType.class);
            XorType mXorType = (XorType) annotation;
            strJsonArrayName = mXorType.JsonArrayName();
        }
        return strJsonArrayName;
    }

    private String getJsonKeyName(Field mField) {
        String strJsonKeyName = "";
        if (mField.isAnnotationPresent(XorField.class)) {
            Annotation fieldAnnotation = mField.getAnnotation(XorField.class);
            XorField mXorField = (XorField) fieldAnnotation;
            strJsonKeyName = mXorField.JsonKeyName();
        }
        return strJsonKeyName;
    }
}
