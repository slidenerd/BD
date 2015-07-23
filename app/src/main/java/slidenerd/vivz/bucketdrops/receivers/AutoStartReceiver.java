package slidenerd.vivz.bucketdrops.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import slidenerd.vivz.bucketdrops.extras.Util;

public class AutoStartReceiver extends BroadcastReceiver {
    public AutoStartReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        //Triggered when the device reboots or restarts, launch the Notifcation Service here to decide which drops the user needs to be notified about
        Util.notifyUpcomingDrops(context);
    }
}
