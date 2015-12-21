package slidenerd.vivz.bucketdrops.adapters;

/**
 * Created by vivz on 17/07/15.
 */
public interface OnSwipeListener {
    /**
     * @param position the position of the row_drop that was swiped within the RecyclerView
     */
    void onSwipe(int position);
}
