package slidenerd.vivz.bucketdrops.services;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import br.com.goncalves.pugnotification.notification.PugNotification;
import io.realm.Realm;
import io.realm.RealmResults;
import slidenerd.vivz.bucketdrops.R;
import slidenerd.vivz.bucketdrops.beans.Drop;
import slidenerd.vivz.bucketdrops.extras.Util;

import static slidenerd.vivz.bucketdrops.extras.Constants.COMPLETED;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * helper methods.
 */
public class NotificationService extends IntentService {

    public static final String NAME = "Bucket Drops Notification Service";

    public NotificationService() {
        super(NAME);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d("VIVZ", "I just ran, yay!");
        //Get the target dates for all records in ascending order
        //Get the dates when each record was added
        //Get the difference between the dates
        //Get today's date, is today's date more than 90% of the time difference between 2 dates?
        //If yes fire a notification for such items, else do nothing
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            RealmResults<Drop> realmResults = realm.where(Drop.class).equalTo(COMPLETED, false).findAll();
            long now = System.currentTimeMillis();
            for (final Drop current : realmResults) {

                //If the target date for the current row_drop is not already over and if 90% time has elapsed right now since the row_drop was added, then fire a notification for the same
                if (has90PercentTimeElapsed(current.getAdded(), current.getWhen(), now)) {
                    fireNotification(current);
                }
            }
        } finally {
            if (realm != null) {
                realm.close();
            }
        }

    }

    private boolean has90PercentTimeElapsed(long added, long when, long now) {
        //total duration between target and added date
        if (now > when) {
            return false;
        } else {
            long difference = when - added;
            //90% of the total duration
            long ninetyPercentDifference = (long) (0.9 * difference);
            //if more than 90% time has elapsed since the row_drop was added, then return true else return false
            return (now > (added + ninetyPercentDifference)) ? true : false;
        }
    }

    private void fireNotification(Drop drop) {
        PugNotification.with(this)
                .load()
                .title(drop.getWhat())
                .message(getString(R.string.notification_added) + Util.getFormattedDate(drop.getWhen()))
                .smallIcon(R.drawable.pugnotification_ic_launcher)
                .largeIcon(R.drawable.pugnotification_ic_launcher)
                .simple()
                .build();
    }
}
