package slidenerd.vivz.bucketdrops.home;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import io.realm.Realm;
import io.realm.RealmConfiguration;

import static slidenerd.vivz.bucketdrops.extras.Constants.KEY;
import static slidenerd.vivz.bucketdrops.extras.Constants.SORT_ASCENDING_DATE;

/**
 * Created by vivz on 23/09/15.
 */
public class AppBucketDrops extends Application {

    private static SharedPreferences mPreferences;

    public static void storeSortOption(int sortOption) {
        mPreferences.edit().putInt(KEY, sortOption).apply();
    }

    public static int loadSortOption() {
        return mPreferences.getInt(KEY, SORT_ASCENDING_DATE);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        RealmConfiguration config = new RealmConfiguration.Builder(this).build();
        Realm.setDefaultConfiguration(config);
    }
}
