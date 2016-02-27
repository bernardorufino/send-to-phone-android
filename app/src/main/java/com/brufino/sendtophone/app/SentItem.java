package com.brufino.sendtophone.app;

import java.io.Serializable;

public class SentItem implements Serializable {

    public enum Type { URL, TEXT }

    private String mTitle;
    private String mData;
    private Type mType;
    private boolean mRead;

    public SentItem(String title, String data, Type type) {
        mTitle = title;
        mData = data;
        mType = type;
        mRead = false;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getData() {
        return mData;
    }

    public void setData(String data) {
        mData = data;
    }

    public Type getType() {
        return mType;
    }

    public void setType(Type type) {
        mType = type;
    }

    public boolean isRead() {
        return mRead;
    }

    public void setRead(boolean read) {
        mRead = read;
    }
}
