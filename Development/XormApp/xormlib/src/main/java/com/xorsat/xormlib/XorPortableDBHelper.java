package com.xorsat.xormlib;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by khawar on 4/21/16.
 */
public class XorPortableDBHelper extends SQLiteOpenHelper {
    private static String TAG = "XorPortableDBHelper"; // Tag just for the LogCat
    private static String DB_PATH = "";
    private static String DB_NAME = "database.db";// Database name
    private static final int DATABASE_VERSION = 1;
    private SQLiteDatabase mDataBase;
    private final Context mContext;

    private static XorPortableDBHelper mInstance = null;

    private XorPortableDBHelper(Context context) throws Exception {
        super(context, DB_NAME, null, DATABASE_VERSION);
        DB_PATH = "/data/data/" + context.getPackageName() + "/databases/";
        this.mContext = context;
        try {
            createDataBase();
            openDataBase();
        } catch (Exception e) {
            XorH.handle(e);
        }
    }

    public static XorPortableDBHelper getInstance(Context ctx, String dbName) throws Exception {
        try {
            if (mInstance == null) {
                DB_NAME = dbName;
                mInstance = new XorPortableDBHelper(ctx.getApplicationContext());
            }
        } catch (Exception e) {
            XorH.handle(e);
            //throw new Error("getInstance" + e.getMessage());
        }
        return mInstance;
    }

    public void createDataBase() {
        boolean mDataBaseExist = checkDataBase();
        if (!mDataBaseExist) {
            this.getReadableDatabase();
            this.close();
            try {
                copyDataBase();
                Log.e(TAG, "createDatabase database created");
            } catch (Exception e) {
                XorH.handle(e);
            }
        }
    }

    private boolean checkDataBase() {
        File dbFile = null;
        try {
            dbFile = new File(DB_PATH + DB_NAME);
        } catch (Exception e) {
            XorH.handle(e);
        }
        return dbFile.exists();
    }

    private void copyDataBase() {
        try {
            InputStream mInput = mContext.getAssets().open(DB_NAME);
            String outFileName = DB_PATH + DB_NAME;
            OutputStream mOutput = new FileOutputStream(outFileName);
            byte[] mBuffer = new byte[1024];
            int mLength;
            while ((mLength = mInput.read(mBuffer)) > 0) {
                mOutput.write(mBuffer, 0, mLength);
            }
            mOutput.flush();
            mOutput.close();
            mInput.close();
        } catch (IOException e) {
            XorH.handle(e);
        }
    }

    public boolean openDataBase() {
        try {
            String mPath = DB_PATH + DB_NAME;
            mDataBase = SQLiteDatabase.openOrCreateDatabase(
                    mContext.getDatabasePath(mPath), null);
            return mDataBase != null;
        } catch (Exception e) {
            XorH.handle(e);
        }
        return false;
    }

    @Override
    public synchronized void close() {
        try {
            if (mDataBase != null)
                mDataBase.close();
            super.close();
        } catch (Exception e) {
            throw new Error("close" + e.getMessage());
        }
    }

    @Override
    public void onCreate(SQLiteDatabase arg0) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}