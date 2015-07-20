package slidenerd.vivz.bucketdrops.services;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

import br.com.goncalves.pugnotification.notification.PugNotification;
import slidenerd.vivz.bucketdrops.R;
import slidenerd.vivz.bucketdrops.beans.Drop;
import slidenerd.vivz.bucketdrops.database.Database;

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
        ArrayList<Drop> listDrops = mDatabase.getAllDrops();
        long now = System.currentTimeMillis();

        for (Drop current : listDrops) {
            if (has90PercentTimeElapsed(current.added, current.when, now)) {
                buildNotification(current);
            }
        }
    }

    private boolean has90PercentTimeElapsed(long added, long when, long now) {
        long difference = when - added;
        long ninetyPercentDifference = (long) (0.9 * difference);
        if (now > (added + ninetyPercentDifference)) {
            Log.d("VIVZ", "Time To Sound An Alarm");
            return true;
        } else {
            Log.d("VIVZ", "Alls well because the difference  is " + difference + " with 90% at " + ninetyPercentDifference + " and now is " + now + " added " + added + " when " + when);
            return false;
        }
    }

    private void buildNotification(Drop drop) {
        PugNotification.with(this)
                .load()
                .title("Remember, you wanted to")
                .message(drop.what)
                .bigTextStyle(drop.what + " on " + drop.when)
                .smallIcon(R.drawable.pugnotification_ic_launcher)
                .largeIcon(R.drawable.pugnotification_ic_launcher)
                .custom()
                .build();
    }
}
