package com.xorsat.xormlib;

import android.content.Context;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by khawar on 4/21/16.
 */
public class XorSync {
    private XorDb mXorDb;
    private XorJson mXorJson;
    private Context context;
    private static XorSync XORSYNC;

    private XorSync(Context context, XorDb mXorDb) {
        this.context = context;
        this.mXorJson = new XorJson();
        this.mXorDb = mXorDb;
    }

    public static XorSync getInstance(Context context, XorDb mXorDb) {
        if (XORSYNC == null) {
            XORSYNC = new XorSync(context, mXorDb);
        }
        return XORSYNC;
    }

    public XorResponse sync(String strUrl, Class target, JSONObject mJSONObjectParams, JSONObject mJSONObjectHeader, boolean isDeleteAll) {
        XorResponse mXorResponse = new XorResponse();
        try {
            //ArrayList<Object> arrayListObjects = new ArrayList<>();
            XorHttpHelper mHttpHelper = new XorHttpHelper();
            mXorResponse = mHttpHelper.httpPost(strUrl, mJSONObjectParams, mJSONObjectHeader);
            String strResponse = mXorResponse.getResponseObject().toString();
            mXorResponse = sync(strResponse, target, isDeleteAll);
        } catch (Exception e) {
            XorH.handle(e);
            mXorResponse.setResponseObject(e);
            mXorResponse.setResponseMessage(e.getMessage());
            mXorResponse.setResponseStatus(false);
        }
        return mXorResponse;
    }

    public XorResponse sync(String strJSONString, Class target, boolean isDeleteAll) {
        XorResponse mXorResponse = new XorResponse();
        try {
            //ArrayList<Object> arrayListObjects = new ArrayList<>();
//            XorHttpHelper mHttpHelper = new XorHttpHelper();
//            mXorResponse = mHttpHelper.httpPost(strUrl, mJSONObjectParams, mJSONObjectHeader);
//            String strResponse = mXorResponse.getResponseObject().toString();
//            if (mXorResponse.getResponseStatus()) {
            ArrayList<Object> arrayListObjects = mXorJson.parse(target, strJSONString);
            if (arrayListObjects != null && arrayListObjects.size() > 0) {
                if (isDeleteAll) {
                    mXorDb.deleteAll(target);
                }
                long result = mXorDb.insertOrUpdateBulk(arrayListObjects);
                mXorResponse.setResponseObject(result);
                mXorResponse.setResponseMessage(result + " records synced successfully!");
                mXorResponse.setResponseStatus(true);
            } else {
                mXorResponse.setResponseObject(0);
                mXorResponse.setResponseMessage("Data did not synced!");
                mXorResponse.setResponseStatus(false);
            }
            //}
        } catch (Exception e) {
            XorH.handle(e);
            mXorResponse.setResponseObject(e);
            mXorResponse.setResponseMessage(e.getMessage());
            mXorResponse.setResponseStatus(false);
        }
        return mXorResponse;
    }

    public XorResponse sync(String strUrl, Class target, JSONObject mJSONObjectParams, JSONObject mJSONObjectHeader) {
        return sync(strUrl, target, mJSONObjectParams, mJSONObjectHeader, false);
    }
}