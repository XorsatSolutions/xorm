package com.xorsat.xormlib;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by khawar on 4/21/16.
 */
public class XorDbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    private static XorDbHelper mInstance = null;
    private List<Class<?>> classes;

    public static XorDbHelper getInstance(Context context, String dbName, List<Class<?>> classes) {
        if (mInstance == null) {
            mInstance = new XorDbHelper(context.getApplicationContext(), dbName, classes);
        }
        return mInstance;
    }

    private XorDbHelper(Context context, String dbName, List<Class<?>> classes) {
        super(context, dbName, null, DATABASE_VERSION);
        this.classes = classes;
    }

    public void onCreate(SQLiteDatabase db) {
        ArrayList<String> arrQry = GetSQLCreateQuries();
        for (String strQry : arrQry) {
            db.execSQL(strQry);
        }
    }

    public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
        onCreate(db);
    }

    public ArrayList<String> GetSQLCreateQuries() {
        ArrayList<String> arrayListSqlQrys = new ArrayList<>();
        String strQryForOneClass = "";
        for (Class mClass : classes) {

            if (mClass.isAnnotationPresent(XorType.class)) {
                Annotation annotation = mClass.getAnnotation(XorType.class);
                XorType mXorType = (XorType) annotation;
                String strTableName = mXorType.DbTableName();
                String strUniqueColumnName = mXorType.UniqueColumnName();
                strQryForOneClass = " CREATE TABLE " + strTableName + " ( _id INTEGER PRIMARY KEY , ";
                for (Field field : mClass.getDeclaredFields()) {
                    if (field.isAnnotationPresent(XorField.class)) {
                        Annotation fieldAnnotation = field.getAnnotation(XorField.class);
                        XorField mXorField = (XorField) fieldAnnotation;
                        String strColumnName = mXorField.DbColumnName();
                        String strDatatype = mXorField.DbDataType();
                        strQryForOneClass = strQryForOneClass + strColumnName + " " + strDatatype + " , ";
                    }
                }
                if (strUniqueColumnName.length() > 0) {
                    strQryForOneClass = strQryForOneClass + " UNIQUE (" + strUniqueColumnName + ") ON CONFLICT REPLACE ";
                } else {
                    strQryForOneClass = strQryForOneClass.substring(0, strQryForOneClass.lastIndexOf(","));
                }
            }

            strQryForOneClass = strQryForOneClass + " ) ";
            arrayListSqlQrys.add(strQryForOneClass);
        }
        return arrayListSqlQrys;
    }
}