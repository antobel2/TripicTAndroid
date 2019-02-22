package com.club.coolkids.clientandroid.models;

import android.content.Context;
import android.net.Uri;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class ImageUploadElement {

    public Uri uri;
    public Context ctx;
    public EnuProgress progressState;

    @Override
    public String toString() {
        return "ImageElement{" +
                "uri=" + uri +
                '}';
    }

    public ImageUploadElement(Uri uri, Context ctx) {
        this.uri = uri;
        this.ctx = ctx;
        progressState = EnuProgress.notUploadedYet;
    }

}
