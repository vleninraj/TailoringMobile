package com.atlanta.tailoringmobile;

import com.atlanta.tailoringmobile.Models.LoginUser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

public class Common {

    public static String sConnectionID;
    public static String sformat="%.2f";
    public static String FormatDate( Date d)
    {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
        String currentDateandTime = sdf.format(new Date());
        return  currentDateandTime;
    }
    public static String getToken() {
        int length = 5;
        Calendar calendar = Calendar.getInstance();


        String TimeStamp = String.valueOf(calendar.getTimeInMillis());

        Random random = new Random();
        String CHARS = "abcdefghijkmnopqrstuvwxyzABCDEFGHJKLMNOPQRSTUVWXYZ234567890";
        StringBuilder token = new StringBuilder(length);
        for (int i = 0; i < 2; i++) {
            token.append(CHARS.charAt(random.nextInt(CHARS.length())));
        }
        return TimeStamp.toString();
    }
}
