package com.opengarden.test.chat.utils;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;

import com.opengarden.test.chat.BuildConfig;

/**
 * Created by salbury on 8/10/15.
 */
public class Utils {
    private static final String TAG = Utils.class.getSimpleName();
    private static final boolean DEBUG = BuildConfig.DEBUG;

    private static final long MIN_SPACE_IN_BYTES = 20971520;

    public static boolean isDataParitionSpaceLow() {
        StatFs stat = new StatFs(Environment.getDataDirectory().getPath());
        long blockSize = stat.getBlockSize(); //deprecated in API level 18...min is currently 16. could do a check.
        long available = stat.getAvailableBlocks() * blockSize;
        long total = stat.getBlockCount() * blockSize;

        // Log.i(TAG, "Disk usage: " + available + " / " + total);

        return total - available < MIN_SPACE_IN_BYTES;
    }

    /**
     *
     * Does a recursive delete, so be the careful!
     */
    public static boolean delete(File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (int i = 0; i < files.length; i++) {
                File f = files[i];
                if (f.isDirectory()) {
                    delete(f);
                } else {
                    if (DEBUG)
                        Log.d(TAG, "Deleting " + f.getAbsolutePath());
                    f.delete();
                }
            }
        }
        if (DEBUG)
            Log.d(TAG, "Deleting " + file.getAbsolutePath());
        return file.delete();
    }

    public static String formatDateTimeForPersist(long timeToFormat) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = sdf.format(new Date(timeToFormat));

        return date;
    }


    public static String formatDateTimeForDisplay(long timeToFormat) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
        String date = simpleDateFormat.format(new Date(timeToFormat));

        return date;
    }

    public static long getDateFromString(String dateString) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date;
        Calendar c = Calendar.getInstance();
        try {
            date = sdf.parse(dateString);
            c.setTime(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return c.getTimeInMillis();
    }

    public static boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        boolean online = cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnectedOrConnecting();
        if (DEBUG) {
            Log.i(TAG, "isOnline:" + online);
        }
        return online;
    }
}
