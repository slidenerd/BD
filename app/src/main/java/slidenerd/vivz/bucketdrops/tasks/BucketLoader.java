package slidenerd.vivz.bucketdrops.tasks;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;

import slidenerd.vivz.bucketdrops.database.Database;

import static slidenerd.vivz.bucketdrops.adapters.SortOptions.KEY_SORT_OPTION;
import static slidenerd.vivz.bucketdrops.adapters.SortOptions.SHOW_COMPLETE;
import static slidenerd.vivz.bucketdrops.adapters.SortOptions.SHOW_INCOMPLETE;
import static slidenerd.vivz.bucketdrops.adapters.SortOptions.SORT_ASCENDING_DATE;
import static slidenerd.vivz.bucketdrops.adapters.SortOptions.SORT_DESCENDING_DATE;

public class BucketLoader extends CursorLoader {
    private Database mDatabase;
    private Bundle mArguments;

    public BucketLoader(Context context, Bundle arguments, Database database) {
        super(context);
        mDatabase = database;
        mArguments = arguments;
    }

    @Override
    public Cursor loadInBackground() {
        Cursor cursor = null;
        if (mArguments != null) {
            int sortOptions = mArguments.getInt(KEY_SORT_OPTION);
            switch (sortOptions) {
                case SORT_ASCENDING_DATE:
                    cursor = mDatabase.readAllSortedByDateAddedAsc();
                    break;
                case SORT_DESCENDING_DATE:
                    cursor = mDatabase.readAllSortedByDateAddedDesc();
                    break;
                case SHOW_COMPLETE:
                    cursor = mDatabase.readAllComplete();
                    break;
                case SHOW_INCOMPLETE:
                    cursor = mDatabase.readAllIncomplete();
                    break;
                default:
                    cursor = mDatabase.readAll();
                    break;
            }
        } else {
            cursor = mDatabase.readAll();
        }
        return cursor;
    }
};