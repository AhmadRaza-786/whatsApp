package com.example.whatsapp.helper;

import android.util.Base64;

public class Base64Custom {

    public static String encodeBase64(String text) {
        return Base64.encodeToString(text.getBytes(), Base64.NO_WRAP);

    }

    public static String decodeBase64(String textEncoded) {
        return new String(Base64.decode(textEncoded, Base64.NO_WRAP));
    }

}
