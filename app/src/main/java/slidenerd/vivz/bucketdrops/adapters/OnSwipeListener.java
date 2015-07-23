package slidenerd.vivz.bucketdrops.adapters;

/**
 * Created by vivz on 17/07/15.
 */
public interface OnSwipeListener {
    /**
     * @param todoId the id of the item that was swiped within the RecyclerView
     */
    public void onSwipeItem(long todoId);
}
