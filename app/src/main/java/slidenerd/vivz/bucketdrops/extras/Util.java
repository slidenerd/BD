package slidenerd.vivz.bucketdrops.extras;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.View;
import android.widget.ImageView;

import java.text.SimpleDateFormat;
import java.util.Date;

import slidenerd.vivz.bucketdrops.services.NotificationService;

/**
 * Created by vivz on 06/07/15.
 */
public class Util {
    /**
     * @param context the Activity or Broadcast Receiver from which you will invoke this method
     */
    public static void runBackgroundService(Context context) {
        //Create an AlarmManager instance and schedule the NotificationService to run every 4 hours
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, NotificationService.class);
        PendingIntent pendingIntent = PendingIntent.getService(context, 201, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME, 1000, 3600000, pendingIntent);
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

    public static String getFormattedDate(long milliseconds) {
        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yy");
        String outputDate = format.format(new Date(milliseconds));
        return outputDate;
    }

    public static Drawable getDrawable(Context context, int resourceId) {
        Drawable drawable;
        if (isLollipopOrMore()) {
            drawable = context.getResources().getDrawable(resourceId);
        } else {
            drawable = context.getDrawable(resourceId);
        }
        return drawable;
    }

    public static void setBackgroundDrawable(View view, int resourceId) {
        Drawable drawable = getDrawable(view.getContext(), resourceId);
        if (isJellyBeanOrMore()) {
            view.setBackground(drawable);
        } else {
            view.setBackgroundDrawable(drawable);
        }
    }

    public static void setImageDrawable(ImageView imageView, int resourceId) {
        Drawable drawable = getDrawable(imageView.getContext(), resourceId);
        imageView.setImageDrawable(drawable);
    }
}
