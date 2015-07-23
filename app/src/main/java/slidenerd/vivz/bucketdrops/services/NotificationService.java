package slidenerd.vivz.bucketdrops.services;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import java.util.ArrayList;

import br.com.goncalves.pugnotification.notification.PugNotification;
import slidenerd.vivz.bucketdrops.R;
import slidenerd.vivz.bucketdrops.beans.Drop;
import slidenerd.vivz.bucketdrops.database.Database;
import slidenerd.vivz.bucketdrops.extras.Util;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class NotificationService extends IntentService {

    private Database mDatabase;

    public NotificationService() {
        super("NotificationService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mDatabase = new Database(this);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d("VIVZ", "I just ran, yay!");
        //Get the target dates for all records in ascending order
        //Get the dates when each record was added
        //Get the difference between the dates
        //Get today's date, is today's date more than 90% of the time difference between 2 dates?
        //If yes fire a notification for such items, else do nothing
        ArrayList<Drop> listIncompleteDrops = mDatabase.getIncompleteDrops();
        long now = System.currentTimeMillis();
        for (final Drop current : listIncompleteDrops) {

            //If the target date for the current drop is not already over and if 90% time has elapsed right now since the drop was added, then fire a notification for the same
            if (has90PercentTimeElapsed(current.added, current.when, now)) {
                fireNotification(current);
            }
        }
    }

    private boolean has90PercentTimeElapsed(long added, long when, long now) {
        //total duration between target and added date
        long difference = when - added;
        //90% of the total duration
        long ninetyPercentDifference = (long) (0.9 * difference);
        //if more than 90% time has elapsed since the drop was added, then return true else return false
        return (now > (added + ninetyPercentDifference)) ? true : false;
    }

    private void fireNotification(Drop drop) {
        PugNotification.with(this)
                .load()
                .title(drop.what)
                .message("You added this drop on " + Util.getFormattedDate(drop.when))
                .smallIcon(R.drawable.pugnotification_ic_launcher)
                .largeIcon(R.drawable.pugnotification_ic_launcher)
                .simple()
                .build();
    }
}
