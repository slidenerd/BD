package slidenerd.vivz.bucketdrops.adapters;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

public class SimpleTouchCallback extends ItemTouchHelper.Callback {

    private final OnSwipeListener mSwipeListener;

    public SimpleTouchCallback(OnSwipeListener adapter) {
        mSwipeListener = adapter;
    }

    /**
     * @return false if you dont want to enable drag else return true
     */
    @Override
    public boolean isLongPressDragEnabled() {
        return false;
    }

    /**
     * @return true of you want to enable swipe in your RecyclerView else return false
     */
    @Override
    public boolean isItemViewSwipeEnabled() {
        return true;
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        //We want to let the person swipe to the right on devices that run LTR and let the person swipe from right to left on devices that run RTL
        int swipeFlags = ItemTouchHelper.END;
        return makeMovementFlags(0, swipeFlags);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                          RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        mSwipeListener.onSwipe(viewHolder.getAdapterPosition());
    }
}