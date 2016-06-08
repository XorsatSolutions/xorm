package com.xorsat.xormlib;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Khawar on 4/11/15.
 */
public class XorHttpHelper {
    private static final String TAG = "HttpHelper";
    //public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    //public static final MediaType MEDIA_TYPE_MARKDOWN = MediaType.parse("text/x-markdown; charset=utf-8");
    //public static final MediaType MEDIA_TYPE_TEXT = MediaType.parse("text/plain; charset=utf-8");

    public XorResponse httpPost(String url, JSONObject mJSONObjectParams, JSONObject mJSONObjectHeader) throws IOException {
        XorResponse mXorResponse = new XorResponse();
        try {
            OkHttpClient client = new OkHttpClient();
            FormBody.Builder formBodyBuilder = new FormBody.Builder();
            Request.Builder requestBuilder = new Request.Builder();
            if (mJSONObjectParams != null) {
                for (int i = 0; i < mJSONObjectParams.length(); i++) {
                    String strKey = mJSONObjectParams.names().getString(i).toString();
                    String strValue = mJSONObjectParams.get(mJSONObjectParams.names().getString(i)).toString();
                    formBodyBuilder.add(strKey, strValue);
                }
            }
            RequestBody formBody = formBodyBuilder.build();
            requestBuilder.url(url);
            requestBuilder.post(formBody);
            if (mJSONObjectHeader != null) {
                for (int i = 0; i < mJSONObjectHeader.length(); i++) {
                    String strKey = mJSONObjectHeader.names().getString(i).toString();
                    String strValue = mJSONObjectHeader.get(mJSONObjectHeader.names().getString(i)).toString();
                    requestBuilder.addHeader(strKey, strValue);
                }
            }
            requestBuilder.addHeader("Content-Type", "application/json; Charset=UTF-8");
            Request request = requestBuilder.build();
            Call call = client.newCall(request);
            Response response = call.execute();
            String strResponse = response.body().string();
            mXorResponse.setResponseObject(strResponse);
            mXorResponse.setResponseMessage(response.message());
            mXorResponse.setResponseStatus(response.isSuccessful());
        } catch (Exception e) {
            XorH.handle(e);
            mXorResponse.setResponseObject(e);
            mXorResponse.setResponseMessage(e.getMessage());
            mXorResponse.setResponseStatus(false);
        }
        return mXorResponse;
    }

    public XorResponse httpPost(String url, JSONObject mJSONObjectParams) throws IOException {
        return httpPost(url, mJSONObjectParams, null);
    }

    public XorResponse httpPost(String url) throws IOException {
        return httpPost(url, null, null);
    }

    public XorResponse httpGet(String url) throws IOException {
        String strResponse = "";
        XorResponse mXorResponse = new XorResponse();
        try {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(url)
                    .build();
            Response response = client.newCall(request).execute();
            mXorResponse.setResponseObject(response.body().string());
            mXorResponse.setResponseMessage(response.message());
            mXorResponse.setResponseStatus(response.isSuccessful());
        } catch (Exception e) {
            XorH.handle(e);
            mXorResponse.setResponseObject(e);
            mXorResponse.setResponseMessage(e.getMessage());
            mXorResponse.setResponseStatus(false);
        }
        return mXorResponse;
    }

    //private static final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");
    public XorResponse httpPostMultipart(String url, ArrayList<XorFileMultipart> arrayListFiles, JSONObject mJSONObjectParams, JSONObject mJSONObjectHeader) throws IOException {
        XorResponse mXorResponse = new XorResponse();
        try {
            OkHttpClient client = new OkHttpClient();
            MultipartBody.Builder requestBodyBuilder = new MultipartBody.Builder();
            requestBodyBuilder.setType(MultipartBody.FORM);
            for (XorFileMultipart file : arrayListFiles) {
                if (file.getFilePath() != null) {
                    requestBodyBuilder.addFormDataPart(file.getName(), file.getFileName(), RequestBody.create(file.getFileType(), new File(file.getFilePath())));
                } else {
                    requestBodyBuilder.addFormDataPart(file.getName(), file.getFileName());
                }
            }
            RequestBody requestBody = requestBodyBuilder.build();
            Request.Builder requestBuilder = new Request.Builder();

            if (mJSONObjectParams != null) {
                for (int i = 0; i < mJSONObjectParams.length(); i++) {
                    String strKey = mJSONObjectParams.names().getString(i).toString();
                    String strValue = mJSONObjectParams.get(mJSONObjectParams.names().getString(i)).toString();
                    requestBodyBuilder.addFormDataPart(strKey, strValue);
                }
            }

            requestBuilder.post(requestBody);
            requestBuilder.url(url);
            if (mJSONObjectHeader != null) {
                for (int i = 0; i < mJSONObjectHeader.length(); i++) {
                    String strKey = mJSONObjectHeader.names().getString(i).toString();
                    String strValue = mJSONObjectHeader.get(mJSONObjectHeader.names().getString(i)).toString();
                    requestBuilder.addHeader(strKey, strValue);
                }
            }
            Request request = requestBuilder.build();
            Response response = client.newCall(request).execute();

            mXorResponse.setResponseObject(response.body().string());
            mXorResponse.setResponseMessage(response.message());
            mXorResponse.setResponseStatus(response.isSuccessful());

            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            //System.out.println(response.body().string());
        } catch (IOException e) {
            XorH.handle(e);
        } catch (JSONException e) {
            XorH.handle(e);
        }
        return mXorResponse;
    }

    public XorResponse httpPostMultipart(String url, ArrayList<XorFileMultipart> arrayListFiles) throws IOException {
        return httpPostMultipart(url, arrayListFiles, null, null);
    }
}