package slidenerd.vivz.bucketdrops.adapters;

/**
 * Created by vivz on 20/07/15.
 */

public interface SortOptions {
    String KEY = "sort_option";
    //The order in which the user added items to the bucket
    int SORT_DEFAULT = 0;
    //The items whose target completion date is the nearest
    int SORT_ASCENDING_DATE = 1;
    //The items whose target completion date is the farthest
    int SORT_DESCENDING_DATE = 2;
    //The items that are complete as marked by the user
    int SHOW_COMPLETE = 3;
    //The items that are incomplete
    int SHOW_INCOMPLETE = 4;
}
