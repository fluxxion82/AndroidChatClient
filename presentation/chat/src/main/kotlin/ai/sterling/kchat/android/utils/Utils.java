package ai.sterling.kchat.android.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Utils {
    private static final String TAG = Utils.class.getSimpleName();

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
                    Log.d(TAG, "Deleting " + f.getAbsolutePath());
                    f.delete();
                }
            }
        }

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
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }
}
