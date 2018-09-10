package com.stephenmbaalu.filmfan.Helpers;

import android.content.Context;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.TypedValue;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class AppUtil {

    public static String getfullImageUrl(String size, String path) {
        return AppConstants.FILE_BASEURL + size + path;
    }

    public static boolean hasTokenExpired(String expiryTimeUtc) {

          //"expires_at": "2018-09-09 07:46:15 UTC",
        DateFormat utcFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        utcFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        //Date date = null;
        String withUTc=expiryTimeUtc.substring(0,expiryTimeUtc.lastIndexOf(" "));
       // System.out.println(expiryTimeUtc+" No "+withUTc);
        try {
            Date  date = utcFormat.parse(withUTc);
            Date now=new Date();
            if(now.compareTo(date)>0){
                return  true;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return  false;
    }

    public static boolean checkInternetConenction(Context c) {
        ConnectivityManager check = (ConnectivityManager) c
                .getApplicationContext().getSystemService(
                        Context.CONNECTIVITY_SERVICE);
        if (check != null) {
            NetworkInfo[] info = check.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        /*Toast.makeText(getApplicationContext(),
                                "Internet is connected", Toast.LENGTH_SHORT)
								.show();*/
                        return true;
                    }
        }
        return false;
    }

    /**
     * Converting dp to pixel
     */
    public static int dpToPx(int dp,Context context) {
        Resources r = context.getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }


}
