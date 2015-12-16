package slidenerd.vivz.bucketdrops.home;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import io.realm.Realm;
import io.realm.RealmConfiguration;

import static slidenerd.vivz.bucketdrops.extras.Constants.KEY;
import static slidenerd.vivz.bucketdrops.extras.Constants.SORT_DEFAULT;

/**
 * Created by vivz on 23/09/15.
 */
public class BucketDropsApp extends Application {

    private static SharedPreferences mPreferences;

    public static void storeSortOption(int sortOption) {
        mPreferences.edit().putInt(KEY, sortOption).apply();
    }

    public static int loadSortOption() {
        return mPreferences.getInt(KEY, SORT_DEFAULT);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        RealmConfiguration config = new RealmConfiguration.Builder(this).build();
        Realm.setDefaultConfiguration(config);
    }
}
