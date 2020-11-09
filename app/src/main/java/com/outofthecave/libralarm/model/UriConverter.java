package com.outofthecave.libralarm.model;

import android.net.Uri;

import androidx.annotation.Nullable;
import androidx.room.TypeConverter;

public class UriConverter {
    @Nullable
    @TypeConverter
    public static String uriToString(@Nullable Uri uri) {
        if (uri == null) {
            return null;
        }
        return uri.toString();
    }

    @Nullable
    @TypeConverter
    public static Uri toUri(@Nullable String uriString) {
        if (uriString == null) {
            return null;
        }
        return Uri.parse(uriString);
    }
}
