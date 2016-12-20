package com.toe.shareyourcuisine.utils;

import android.content.Context;

import com.afollestad.materialdialogs.MaterialDialog;

import java.util.Calendar;
import java.util.Random;
import java.util.TimeZone;
import java.util.UUID;

/**
 * Created by HQu on 12/6/2016.
 */

public class SYCUtils {

    public static Long getCurrentEST() {
        TimeZone timeZone = TimeZone.getTimeZone("EST");
        Calendar calendar = Calendar.getInstance(timeZone);
        return calendar.getTimeInMillis();
    }

}
