package com.toe.shareyourcuisine.utils;

import java.util.Calendar;
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
}
