package com.akash2099.firebaseapp;

public class UploadVideo {
    private String mName;
    private String mVideoUri;

    public UploadVideo() {

    }

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public String getmVideoUri() {
        return mVideoUri;
    }

    public void setmVideoUri(String mVideoUri) {
        this.mVideoUri = mVideoUri;
    }

    public UploadVideo(String mName, String mVideoUri) {
        if (mName.trim().equals("")) {
            mName = "no name available";
        }
        this.mName = mName;
        this.mVideoUri = mVideoUri;
    }
}
