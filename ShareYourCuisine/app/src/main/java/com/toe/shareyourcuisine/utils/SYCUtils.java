package com.toe.shareyourcuisine.utils;

import android.content.Context;

import com.afollestad.materialdialogs.MaterialDialog;

import java.util.Calendar;
import java.util.Random;
import java.util.TimeZone;

/**
 * Created by HQu on 12/6/2016.
 */

public class SYCUtils {

    public static Long getCurrentEST() {
        TimeZone timeZone = TimeZone.getTimeZone("EST");
        Calendar calendar = Calendar.getInstance(timeZone);
        return calendar.getTimeInMillis();
    }

    public static String getRandomString() {
        int passwordSize = 10;
        char[] chars = "abcdefghijklmnopqrstuvwxyz0123456789".toCharArray();
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < passwordSize; i++) {
            char c = chars[random.nextInt(chars.length)];
            sb.append(c);
        }
        return sb.toString();
    }

}
