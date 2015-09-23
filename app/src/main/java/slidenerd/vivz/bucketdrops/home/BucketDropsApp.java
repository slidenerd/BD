package slidenerd.vivz.bucketdrops.home;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import slidenerd.vivz.bucketdrops.adapters.SortOptions;

/**
 * Created by vivz on 23/09/15.
 */
public class BucketDropsApp extends Application {

    private static SharedPreferences mPreferences;

    public static void storeSortOption(int sortOption) {
        mPreferences.edit().putInt(SortOptions.KEY, sortOption).apply();
    }

    public static int loadSortOption() {
        return mPreferences.getInt(SortOptions.KEY, SortOptions.SORT_DEFAULT);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        RealmConfiguration config = new RealmConfiguration.Builder(this).build();
        Realm.setDefaultConfiguration(config);
    }
}
