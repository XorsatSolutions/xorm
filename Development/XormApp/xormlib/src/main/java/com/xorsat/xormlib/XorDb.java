package com.xorsat.xormlib;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by khawar on 4/21/16.
 * todo
 * make model implementable
 * insertorupdate bulk optimization
 */
public class XorDb {
    public SQLiteDatabase mSQLiteDatabase;
    public XorDbHelper mDbHelper;
    public XorPortableDBHelper mXorPortableDBHelper;

    private static XorDb XORDB;
    public static String OPERATOR_LIKE = " LIKE ";
    public static String OPERATOR_EQUALS = " = ";
    public static String GATE_AND = " AND ";
    public static String GATE_OR = " OR ";


    private XorDb(Context context, String dbName, List<Class<?>> classes) {
        try {
            mDbHelper = XorDbHelper.getInstance(context, dbName, classes);
            mSQLiteDatabase = mDbHelper.getWritableDatabase();
        } catch (Exception e) {
            XorH.handle(e);
        }
    }

    public static XorDb getInstance(Context context, String dbName, List<Class<?>> classes) {
        if (XORDB == null) {
            XORDB = new XorDb(context, dbName, classes);
        }
        return XORDB;
    }

    public static XorDb getInstance(Context context, String dbName) {
        if (XORDB == null) {
            XORDB = new XorDb(context, dbName);
        }
        return XORDB;
    }

    private XorDb(Context context, String dbName) {
        try {
            mXorPortableDBHelper = XorPortableDBHelper.getInstance(context, dbName);
            mSQLiteDatabase = mXorPortableDBHelper.getWritableDatabase();
        } catch (Exception e) {
            XorH.handle(e);
        }
    }

    // GET FUNCTIONS START

    public Object getList(Class mClass) {
        return getList(mClass, " SELECT * FROM " + getTableName(mClass));
    }

    public Object getList(Class mClass, String strQry) {
        ArrayList<Object> arrayListObject = new ArrayList<>();
        try {
            Cursor mCursor = mSQLiteDatabase.rawQuery(strQry, null);
            for (mCursor.moveToFirst(); !mCursor.isAfterLast(); mCursor.moveToNext()) {
                arrayListObject.add(getObjectFromCursor(mCursor, mClass));
            }
        } catch (Exception e) {
            XorH.handle(e);
        }
        return arrayListObject;
    }

    public Cursor getCursor(String strQry) {
        Cursor mCursor = null;
        try {
            mCursor = mSQLiteDatabase.rawQuery(strQry, null);
        } catch (Exception e) {
            XorH.handle(e);
        }
        return mCursor;
    }

    public Object getByAutoId(Class mClass, long _id) {
        Object mObject = new Object();
        String strWhere = " WHERE _id = " + _id;
        String strQry = " SELECT * FROM " + getTableName(mClass) + strWhere;
        try {
            ArrayList<Object> arrayListObject = (ArrayList<Object>) getList(mClass, strQry);
            mObject = arrayListObject.get(0);
        } catch (Exception e) {
            XorH.handle(e);
        }
        return mObject;
    }

    private Object getObjectFromCursor(Cursor mCursor, Class mClass) {
        Object newObject = new Object();
        try {
            newObject = (Object) mClass.newInstance();
            Field[] fieldsArray = mClass.getDeclaredFields();
            for (Field mField : fieldsArray) {
                String strColumnName = getColumnName(mField);
                if (strColumnName.length() > 0) {
                    Method[] methods = mClass.getMethods();
                    for (Method method : methods) {
                        if (isSetter(method)) {
                            if (method.getName().toString().substring(3).equalsIgnoreCase(mField.getName())) {

//                                if (mField.getType() == String.class) {
//                                    method.invoke(newObject, mCursor.getString(mCursor.getColumnIndex(strColumnName)));
//                                } else if (mField.getType() == Integer.class) {
//                                    method.invoke(newObject, mCursor.getInt(mCursor.getColumnIndex(strColumnName)));
//
//                                } else if (mField.getType() == Long.class) {
//                                    method.invoke(newObject, mCursor.getLong(mCursor.getColumnIndex(strColumnName)));
//
//                                } else if (mField.getType() == Float.class) {
//                                    method.invoke(newObject, mCursor.getLong(mCursor.getColumnIndex(strColumnName)));
//
//                                } else if (mField.getType() == Boolean.class) {
//                                    method.invoke(newObject, mCursor.getInt(mCursor.getColumnIndex(strColumnName)));
//
//                                } else if (mField.getType() == Double.class) {
//                                    method.invoke(newObject, mCursor.getDouble(mCursor.getColumnIndex(strColumnName)));
//
//                                } else {
//                                    method.invoke(newObject, mCursor.getString(mCursor.getColumnIndex(strColumnName)));
//                                }
                                //method.invoke(newObject, mCursor.getString(mCursor.getColumnIndex(strColumnName)));

                                //String strValue = mJSONObject.getString(strKeyName);
                                if (XorH.isInteger(mField)) {
                                    method.invoke(newObject, mCursor.getInt(mCursor.getColumnIndex(strColumnName)));
                                } else if (XorH.isDouble(mField)) {
                                    method.invoke(newObject, mCursor.getDouble(mCursor.getColumnIndex(strColumnName)));
                                } else if (XorH.isLong(mField)) {
                                    method.invoke(newObject, mCursor.getLong(mCursor.getColumnIndex(strColumnName)));
                                } else if (XorH.isBoolean(mField)) {
                                    method.invoke(newObject, mCursor.getInt(mCursor.getColumnIndex(strColumnName)));
                                } else if (XorH.isFloat(mField)) {
                                    method.invoke(newObject, mCursor.getLong(mCursor.getColumnIndex(strColumnName)));
                                } else if (XorH.isString(mField)) {
                                    method.invoke(newObject, mCursor.getString(mCursor.getColumnIndex(strColumnName)));
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

    // Search Start
    public Object getListSearch(Class mClass, String where) {
        String strQry = " SELECT * FROM " + getTableName(mClass) + " " + where;
        return getList(mClass, strQry);
    }

    public Object getListSearch(Object object, String gate) {       // for wildcard search
        String strWhere = getWhere(object, gate, " LIKE ", true);
        String strQry = " SELECT * FROM " + getTableName(object.getClass()) + " " + strWhere;
        return getList(object.getClass(), strQry);
    }


    public Object getListSearch(Object object, String gate, String operator, boolean isString) {
        String strWhere = getWhere(object, gate, operator, isString);
        String strQry = " SELECT * FROM " + getTableName(object.getClass()) + " " + strWhere;
        return getList(object.getClass(), strQry);
    }
    // Search Ends

    // GET FUNCTIONS ENDS

    // EXECUTE RAW QUERY FUNCTIONS START
    public void execSQL(Class mClass, String sql) {
        try {
            String strTableName = getTableName(mClass);
            if (strTableName.length() > 0) {
                mSQLiteDatabase.execSQL(sql);
            }
        } catch (Exception e) {
            XorH.handle(e);
        }
    }
    // EXECUTE RAW QUERY FUNCTIONS ENDS

    // INSERT FUNCTIONS START
    public void insertBulk(ArrayList<Object> array_list) throws Exception {
        try {
            for (Object mObject : array_list) {
                insert(mObject);
            }
        } catch (Exception e) {
            XorH.handle(e);
        }
    }

//    public long insertOrUpdateBulk(ArrayList<Object> array_list) throws Exception {
//        long result = 0;
//        try {
//            for (Object mObject : array_list) {
//                result += insertOrUpdate(mObject);
//            }
//        } catch (Exception e) {
//            XorH.handle(e);
//        }
//        return result;
//    }

    public long insertOrUpdateBulk(ArrayList<Object> array_list) throws Exception {
        long count = 0;
        try {
            Object mObjectModel = array_list.get(0);
            mSQLiteDatabase.beginTransaction();
            String sql = "Insert or Replace into " + getTableName(mObjectModel.getClass())
                    + " ( " + getColumnsNamesCommaSeperated(mObjectModel) + " ) " +
                    "values ( " + getQuestionMarksCommaSeperated(mObjectModel) + " ) ";
            SQLiteStatement mSQLiteStatementInsert = mSQLiteDatabase.compileStatement(sql);
            for (Object mObject : array_list) {
                mSQLiteStatementInsert.clearBindings();
                ContentValues mContentValues = getContentValuesFromObject(mObject);
                int bindingNumber = 1;
                for (Map.Entry entry : mContentValues.valueSet()) {
                    mSQLiteStatementInsert.bindString(bindingNumber, entry.getValue().toString());
                    bindingNumber++;
                }
                mSQLiteStatementInsert.execute();
                count++;
            }
            mSQLiteDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            XorH.handle(e);
        } finally {
            mSQLiteDatabase.endTransaction();
        }
        return count;
    }

    private String getColumnsNamesCommaSeperated(Object mObject) {
        String result = "";
        try {
            ContentValues mContentValues = getContentValuesFromObject(mObject);
            for (Map.Entry entry : mContentValues.valueSet()) {
                result += "," + entry.getKey().toString();
            }
        } catch (Exception e) {
            XorH.handle(e);
        }
        return result.substring(1);
    }

    private String getQuestionMarksCommaSeperated(Object mObject) {
        String result = "?";
        try {
            ContentValues mContentValues = getContentValuesFromObject(mObject);
            for (Map.Entry entry : mContentValues.valueSet()) {
                result += ",?";
            }
        } catch (Exception e) {
            XorH.handle(e);
        }
        return result.substring(2);
    }

    public long insertOrUpdate(Object mObject) throws Exception {
        long newId = 0;
        try {
            ContentValues mContentValues = getContentValuesFromObject(mObject);
            String strTableName = getTableName(mObject.getClass());
            String uniqueColumnName = getTableUniqueColumnName(mObject.getClass());
            if (strTableName.length() > 0) {
                newId = mSQLiteDatabase.insertWithOnConflict(strTableName, uniqueColumnName, mContentValues, SQLiteDatabase.CONFLICT_REPLACE);
            }
        } catch (Exception e) {
            XorH.handle(e);
        }
        return newId;
    }

    public long insert(Object mObject) throws Exception {
        long newId = 0;
        try {
            ContentValues mContentValues = getContentValuesFromObject(mObject);
            String strTableName = getTableName(mObject.getClass());
            if (strTableName.length() > 0) {
                newId = mSQLiteDatabase.insert(strTableName, null, mContentValues);
            }
        } catch (Exception e) {
            XorH.handle(e);
        }
        return newId;
    }

    private ContentValues getContentValuesFromObject(Object mObject) throws Exception {
        ContentValues mContentValues = new ContentValues();
        try {
            Field[] fieldsArray = mObject.getClass().getDeclaredFields();
            for (Field mField : fieldsArray) {
                String strColumnName = getColumnName(mField);
                if (strColumnName.length() > 0) {
                    Method[] methods = mObject.getClass().getMethods();
                    for (Method method : methods) {
                        if (isGetter(method)) {
                            if (method.getName().toString().substring(3).equalsIgnoreCase(mField.getName())) {

//                                if (mField.getType() == String.class) {
//                                    String returnValue = (String) method.invoke(mObject, null);
//                                    mContentValues.put(strColumnName, returnValue);
//                                } else if (mField.getType() == Integer.class) {
//                                    int returnValue = (int) method.invoke(mObject, null);
//                                    mContentValues.put(strColumnName, returnValue);
//                                } else if (mField.getType() == Long.class) {
//                                    Long returnValue = (Long) method.invoke(mObject, null);
//                                    mContentValues.put(strColumnName, returnValue);
//                                } else if (mField.getType() == Float.class) {
//                                    Float returnValue = (Float) method.invoke(mObject, null);
//                                    mContentValues.put(strColumnName, returnValue);
//                                } else if (mField.getType() == Boolean.class) {
//                                    Boolean returnValue = (Boolean) method.invoke(mObject, null);
//                                    mContentValues.put(strColumnName, returnValue);
//                                } else if (mField.getType() == Double.class) {
//                                    Double returnValue = (Double) method.invoke(mObject, null);
//                                    mContentValues.put(strColumnName, returnValue);
//                                } else {
//                                    mContentValues.put(strColumnName, "");
//                                }
                                if (XorH.isInteger(mField)) {
                                    Integer returnValue = (Integer) method.invoke(mObject, null);
                                    mContentValues.put(strColumnName, returnValue);
                                } else if (XorH.isDouble(mField)) {
                                    Double returnValue = (Double) method.invoke(mObject, null);
                                    mContentValues.put(strColumnName, returnValue);
                                }else if (XorH.isLong(mField)) {
                                    Long returnValue = (Long) method.invoke(mObject, null);
                                    mContentValues.put(strColumnName, returnValue);
                                } else if (XorH.isBoolean(mField)) {
                                    Boolean returnValue = (Boolean) method.invoke(mObject, null);
                                    mContentValues.put(strColumnName, returnValue);
                                } else if (XorH.isFloat(mField)) {
                                    Float returnValue = (Float) method.invoke(mObject, null);
                                    mContentValues.put(strColumnName, returnValue);
                                } else if (XorH.isString(mField)) {
                                    String returnValue = (String) method.invoke(mObject, null);
                                    mContentValues.put(strColumnName, returnValue);
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            XorH.handle(e);
        }
        return mContentValues;
    }

    // INSERT FUNCTIONS ENDS

    // UPDATE FUNCTIONS START

    public long updateByUniqueColumn(Object mObject) {
        long newId = 0;
        try {
            ContentValues mContentValues = getContentValuesFromObject(mObject);
            String strTableName = getTableName(mObject.getClass());
            if (strTableName.length() > 0) {
                newId = mSQLiteDatabase.update(strTableName, mContentValues,
                        getTableUniqueColumnName(mObject.getClass()) + " =  " + getPrimaryKeyValue(mObject), null);
            }
        } catch (Exception e) {
            XorH.handle(e);
        }
        return newId;
    }

    public long updateByPrimaryKey(Object mObject) {
        long newId = 0;
        try {
            ContentValues mContentValues = getContentValuesFromObject(mObject);
            String strTableName = getTableName(mObject.getClass());
            if (strTableName.length() > 0) {
                newId = mSQLiteDatabase.update(strTableName, mContentValues,
                        getPrimaryKeyName() + " =  " + getPrimaryKeyValue(mObject), null);
            }
        } catch (Exception e) {
            XorH.handle(e);
        }
        return newId;
    }
    // UPDATE FUNCTIONS ENDS


    // DELETE FUNCTIONS START
    public void deleteAll(Class mClass) {
        try {
            String strTableName = getTableName(mClass);
            if (strTableName.length() > 0) {
                mSQLiteDatabase.execSQL(" DELETE FROM " + strTableName);
            }
        } catch (Exception e) {
            XorH.handle(e);
        }
    }

    public void delete(Object mObject) {
        try {
            String strTableName = getTableName(mObject.getClass());
            if (strTableName.length() > 0) {
                mSQLiteDatabase.execSQL(" DELETE FROM " + strTableName + " WHERE " + getTableUniqueColumnName(mObject.getClass()) + " =  " + getPrimaryKeyValue(mObject));
            }
        } catch (Exception e) {
            XorH.handle(e, "delete");
        }
    }
    // DELETE FUNCTIONS ENDS


    // GENERAL FUNCTIONS START
    private static boolean isSetter(Method method) {
        try {
            if (!method.getName().startsWith("set")) return false;
            if (method.getParameterTypes().length != 1) return false;
        } catch (Exception e) {
            XorH.handle(e, "isSetter");
        }
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

    private String getTableName(Class mClass) {
        String strTableName = "";
        try {
            if (mClass.isAnnotationPresent(XorType.class)) {
                Annotation annotation = mClass.getAnnotation(XorType.class);
                XorType mXorType = (XorType) annotation;
                strTableName = mXorType.DbTableName();
            }
        } catch (Exception e) {
            XorH.handle(e, "getTableName");
        }
        return strTableName;
    }

    private String getTableUniqueColumnName(Class mClass) {
        String strTableName = "";
        try {
            if (mClass.isAnnotationPresent(XorType.class)) {
                Annotation annotation = mClass.getAnnotation(XorType.class);
                XorType mXorType = (XorType) annotation;
                strTableName = mXorType.UniqueColumnName();
            }
        } catch (Exception e) {
            XorH.handle(e, "getTableUniqueColumnName");
        }
        return strTableName;
    }

    private String getColumnName(Field mField) {
        String strColumnName = "";
        try {
            if (mField.isAnnotationPresent(XorField.class)) {
                Annotation fieldAnnotation = mField.getAnnotation(XorField.class);
                XorField mXorField = (XorField) fieldAnnotation;
                strColumnName = mXorField.DbColumnName();
            }
        } catch (Exception e) {
            XorH.handle(e, "getColumnName");
        }
        return strColumnName;
    }

    private String getPrimaryKeyName() {
        String strPrimaryKeyName = "_id";
//        Field[] fieldsArray = mClass.getDeclaredFields();
//        for (Field mField : fieldsArray) {
//            if (mField.isAnnotationPresent(XorField.class)) {
//                Annotation fieldAnnotation = mField.getAnnotation(XorField.class);
//                XorField mXorField = (XorField) fieldAnnotation;
//                if (mXorField.IsPrimaryKey()) {
//                    strPrimaryKeyName = mXorField.DbColumnName();
//                }
//            }
//        }
        return strPrimaryKeyName;
    }


    private String getPrimaryKeyValue(Object mObject) {
        String strPrimaryKeyValue = "";
        try {
            Field[] fieldsArray = mObject.getClass().getDeclaredFields();
            for (Field mField : fieldsArray) {
                String strColumnName = getColumnName(mField);
                if (strColumnName.length() > 0) {
                    if (mField.isAnnotationPresent(XorField.class)) {
                        Annotation fieldAnnotation = mField.getAnnotation(XorField.class);
                        XorField mXorField = (XorField) fieldAnnotation;
                        if (mXorField.IsPrimaryKey()) {
                            Method[] methods = mObject.getClass().getMethods();
                            for (Method method : methods) {
                                if (isGetter(method)) {
                                    if (method.getName().toString().substring(3).equalsIgnoreCase(mField.getName())) {
                                        strPrimaryKeyValue = (String) method.invoke(mObject, null);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            XorH.handle(e, "getPrimaryKeyValue");
        }
        return strPrimaryKeyValue;
    }

    private ArrayList<String> getColumnsNames(Class mClass) {
        ArrayList<String> mArrayListColumnNames = new ArrayList<>();
        try {
            Field[] fieldsArray = mClass.getDeclaredFields();
            for (Field mField : fieldsArray) {
                String strColumnName = getColumnName(mField);
                if (strColumnName.length() > 0) {
                    if (mField.isAnnotationPresent(XorField.class)) {
                        Annotation fieldAnnotation = mField.getAnnotation(XorField.class);
                        XorField mXorField = (XorField) fieldAnnotation;
                        strColumnName = mXorField.DbColumnName();
                        mArrayListColumnNames.add(strColumnName);
                    }
                }
            }
        } catch (Exception e) {
            XorH.handle(e, "getColumnsNames");
        }
        return mArrayListColumnNames;
    }

    private String getWhere(Object _object, String gate, String operator, boolean isString) {
        StringBuilder mStringBuilderWhere = new StringBuilder();
        mStringBuilderWhere.append(" WHERE ");
        try {
            Field[] fields = _object.getClass().getDeclaredFields();
            for (Field field : fields) {
                try {
                    for (Method method : _object.getClass().getMethods()) {
                        if ((method.getName().startsWith("get")) && (method.getName().length() == (field.getName().length() + 3))) {
                            if (method.getName().toLowerCase().endsWith(field.getName().toLowerCase())) {
                                try {
                                    String strValue = (String) method.invoke(_object);
                                    if (strValue != null) {

                                        if (operator.toLowerCase().contains("like")) {
                                            mStringBuilderWhere.append(getColumnName(field) + " " + operator + " '%" + strValue + "%' ");
                                        } else {
                                            if (isString) {
                                                strValue = "'" + strValue + "'";
                                            }
                                            mStringBuilderWhere.append(getColumnName(field) + " " + operator + " " + strValue);
                                        }


//                                        if (operator.toLowerCase().contains("like")) {
//                                            mStringBuilderWhere.append(getColumnName(field) + " " + operator + " '%" + strValue + "%' ");
//                                        } else if (operator.toLowerCase().contains("=")) {
//                                            mStringBuilderWhere.append(getColumnName(field) + " " + operator + " '" + strValue + "' ");
//                                        } else {
//                                            mStringBuilderWhere.append(getColumnName(field) + " " + operator + " " + strValue);
//                                        }
                                        mStringBuilderWhere.append(" " + gate + " ");
                                    }
                                } catch (IllegalAccessException e) {
                                    //XorL.e("Could not determine method: " + method.getName());
                                } catch (InvocationTargetException e) {
                                    //L.e("Could not determine method: " + method.getName());
                                }
                            }
                        }
                    }

                } catch (Exception ex) {
                    System.out.println(ex);
                }
            }
            String s = mStringBuilderWhere.toString();
            String strTruncatedWhere = s.substring(0, s.length() - (gate.length() + 2));
            return strTruncatedWhere;
        } catch (Exception e) {
            XorH.handle(e);
        }
        return "";
    }


    public void generateData(Class mClass) {

    }

//    private String getWhere(Object _object, String gate) {
//        return getWhere(_object, gate, "LIKE", true);
//    }

// GENERAL FUNCTIONS ENDS
}
