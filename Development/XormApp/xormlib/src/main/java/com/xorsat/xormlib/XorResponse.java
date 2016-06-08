package com.xorsat.xormlib;


import java.util.ArrayList;

/**
 * Created by khawar on 4/21/16.
 */
public class XorResponse extends ArrayList<Object> {
    private boolean responseStatus;

    private String responseMessage;

    private Object responseObject;

    public boolean getResponseStatus() {
        return responseStatus;
    }

    public void setResponseStatus(boolean responseStatus) {
        this.responseStatus = responseStatus;
    }

    public String getResponseMessage() {
        return responseMessage;
    }

    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }

    public Object getResponseObject() {
        return responseObject;
    }

    public void setResponseObject(Object responseObject) {
        this.responseObject = responseObject;
    }
}
