package slidenerd.vivz.bucketdrops.extras;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Build;
import android.util.DisplayMetrics;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;

import io.realm.RealmResults;
import slidenerd.vivz.bucketdrops.R;
import slidenerd.vivz.bucketdrops.beans.Drop;
import slidenerd.vivz.bucketdrops.services.NotificationService;

/**
 * Created by vivz on 06/07/15.
 */
public class Util {
    /**
     * @param context the Activity or Broadcast Receiver from which you will invoke this method
     */
    public static void notifyUpcomingDrops(Context context) {
        //Create an AlarmManager instance and schedule the NotificationService to run every 4 hours
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, NotificationService.class);
        PendingIntent pendingIntent = PendingIntent.getService(context, 201, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME, 1000, 14400000, pendingIntent);
    }

    /**
     * This method converts dp unit to equivalent pixels, depending on device density.
     *
     * @param dp      SimpleItemTouchHelperCallback value in dp (density independent pixels) unit. Which we need to convert into pixels
     * @param context Context to read resources and device specific display metrics
     * @return SimpleItemTouchHelperCallback float value to represent px equivalent to dp depending on device density
     */

    public static float convertDpToPixel(float dp, Context context) {
        Resources resources = context.getResources();
        //pixels = dps * density of device / 160
        //if density of device = 160, pixels = dps
        DisplayMetrics displayMetrics = resources.getDisplayMetrics();
        float pixels = dp * (displayMetrics.densityDpi) / 160.0F;
        return pixels;
    }

    /**
     * This method converts device specific pixels to density independent pixels.
     *
     * @param px      SimpleItemTouchHelperCallback value in px (pixels) unit. Which we need to convert into db
     * @param context Context to read resources and device specific display metrics
     * @return SimpleItemTouchHelperCallback float value to represent dp equivalent to px value
     */
    public static float convertPixelsToDp(float px, Context context) {
        Resources resources = context.getResources();
        //dps = pixels / (density of device / 160)
        //if density of device = 160, dps = pixels
        DisplayMetrics displayMetrics = resources.getDisplayMetrics();
        float dp = px / (displayMetrics.densityDpi / 160.0F);
        return dp;
    }

    public static Typeface loadRalewayThin(Context context) {

        Typeface customFont = Typeface.createFromAsset(context.getAssets(), "fonts/raleway_thin.ttf");
        return customFont;
    }

    public static Typeface loadRalewayRegular(Context context) {
        Typeface customFont = Typeface.createFromAsset(context.getAssets(), "fonts/raleway_regular.ttf");
        return customFont;
    }

    public static boolean isJellyBeanOrMore() {
        return Build.VERSION.SDK_INT > 16;
    }

    public static boolean isLollipopOrMore() {
        return Build.VERSION.SDK_INT > 20;
    }

    public static boolean isIceCreamSandwichOrMore() {
        return Build.VERSION.SDK_INT > 13;
    }

    public static String getFormattedDate(long milliseconds) {
        DateTimeFormatter formatter = DateTimeFormat.shortDate();
        String outputDate = formatter.print(milliseconds);
        return outputDate;
    }

    public static Bitmap getScaledVersion(Context context, int width, int height) {
        int reqHeight = height;
        int reqWidth = width;
        BitmapFactory.Options options = new BitmapFactory.Options();

        // First decode with inJustDecodeBounds=true to check dimensions
        options.inJustDecodeBounds = true;
        Bitmap bitmapOriginal = BitmapFactory.decodeResource(context.getResources(), R.drawable.background, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(context.getResources(), R.drawable.background, options);
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static ArrayList<Drop> duplicateDrops(RealmResults<Drop> realmDrops) {
        ArrayList<Drop> listDrops = new ArrayList<>();
        for (Drop realmDrop : realmDrops) {
            Drop drop = new Drop(realmDrop.getWhat(), realmDrop.getAdded(), realmDrop.getWhen(), realmDrop.isCompleted());
            listDrops.add(drop);
        }
        return listDrops;
    }
}
