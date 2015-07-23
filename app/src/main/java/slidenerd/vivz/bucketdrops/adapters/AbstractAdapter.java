package slidenerd.vivz.bucketdrops.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;

/**
 * Created by Vivz on 10/31/14.
 * An Adapter to be used as a base class for connecting SQLite databases with RecyclerViews. It manages a cursor and acts similar to a custom CursorAdapter.
 */

public abstract class AbstractAdapter<VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> implements ItemTouchHelperAdapter {

    private Context mContext;

    //The cursor object that will contain all the rows that you want to display inside the RecyclerView
    private Cursor mCursor;

    //A variable indicating if the data contained in the Cursor above is valid
    private boolean mDataValid;

    //the index of the column containing _id of an SQLite database table from which you want to load data inside the Cursor above
    private int mIndexColumnId;

    // Do you want to show the footer in the RecyclerView when its empty? if true then show items, else dont show items
    private boolean mShowFooterWhenEmpty = false;

    public AbstractAdapter(Context context) {
        mContext = context;
    }

    /**
     * Check if the data contained by the cursor is valid and try to extract the value of the column index _id
     * To indicate that your RecyclerView needs to refresh what it displays, call notifyDataSetChanged
     *
     * @param cursor the rows from a database table that you want to display inside your RecyclerView
     */
    private void createCursor(Cursor cursor) {
        mCursor = cursor;
        mDataValid = cursor != null;
        mIndexColumnId = mDataValid ? mCursor.getColumnIndex("_id") : -1;
        notifyDataSetChanged();
    }

    /**
     * @param showFooterWhenEmpty if true, display the footer of your RecyclerView when there are no items to display otherwise, if false dont show the footer
     */
    public void setShowFooterWhenEmpty(boolean showFooterWhenEmpty) {
        mShowFooterWhenEmpty = showFooterWhenEmpty;
    }

    public Cursor getCursor() {
        return mCursor;
    }

    /**
     * If you are setting the cursor for the first time, create it from scratch, else swap it or change it.
     *
     * @param cursor containing the rows from your SQLite table that you want to display inside your RecyclerView
     */
    public void setCursor(Cursor cursor) {
        if (mCursor == null) {
            createCursor(cursor);
        } else {
            changeCursor(cursor);
        }
    }

    /**
     * Check if we have valid data inside the adapter, if not then return 0, if we have valid data, then check the number of items and footers. If we have some items to display then add the number of items and footers to return the total number of items to be displayed inside the RecyclerView. If the number of items is 0, our RecyclerView is empty in which case, we need to decide if we want to display the footer or not. If we choose to display footers when the items are empty, then return the number of footers so that they may be displayed by your RecyclerView otherwise return 0
     *
     * @return the number of items to be displayed by the RecyclerView
     */
    @Override
    public int getItemCount() {
        if (mDataValid && mCursor != null) {
            //The number of data items in your RecyclerView
            int itemCount = mCursor.getCount();
            //The number of footer items in your RecyclerView
            int footerCount = getFooterCount();
            //If the cursor has nothing to display, what do you wanna do? display the footers or not?
            if (itemCount == 0) {
                //if we opted to show footers when the list is empty
                if (mShowFooterWhenEmpty) {
                    return getFooterCount();
                } else {
                    return 0;
                }
            } else {
                //If we have some items in our list (1...many) we wanna display the items + footer
                return mCursor.getCount() + getFooterCount();
            }
        }
        return 0;
    }

    /**
     * @param position of the current item within the RecyclerView whose type we need to specify.
     * @return an integer returning the type of an item at a given position
     */
    @Override
    public int getItemViewType(int position) {
        return getItemViewType(mCursor, position);
    }

    /**
     * @param cursor   containing the data or rows from our SQLite database which we want to display inside our RecyclerView
     * @param position of the current item within the RecyclerView whose type we need to specify.
     * @return
     */
    protected abstract int getItemViewType(Cursor cursor, int position);

    /**
     * @return the number of footers which you want to display inside the RecyclerView, 0 if you dont plan on displaying any footers.
     */
    public abstract int getFooterCount();

    /**
     * @param position of the current item within the RecyclerView whose item id we need to specify.
     * @return the index of the column _id from the SQLite database table whose rows you are trying to display inside the RecyclerView, 0 if you dont have valid data in your Cursor
     */
    @Override
    public long getItemId(int position) {
        if (mDataValid && mCursor != null && mCursor.moveToPosition(position)) {
            return mCursor.getLong(mIndexColumnId);
        }
        return 0;
    }

    @Override
    public void setHasStableIds(boolean hasStableIds) {
        super.setHasStableIds(true);
    }

    /**
     * @param viewHolder containing the View(s) that you want to fill with data at the specified position below
     * @param position   of the current item within the RecyclerView whose data needs to be bound to its View.
     * @param cursor
     */
    public abstract void onBindViewHolder(VH viewHolder, int position, Cursor cursor);

    @Override
    public void onBindViewHolder(VH viewHolder, int position) {
        if (!mDataValid) {
            throw new IllegalStateException("this should only be called when the cursor is valid");
        }
        onBindViewHolder(viewHolder, position, mCursor);
    }

    /**
     * Change the underlying cursor to a new cursor. If there is an existing cursor it will be
     * closed.
     */
    public void changeCursor(Cursor cursor) {
        Cursor old = swapCursor(cursor);
        if (old != null) {
            old.close();
        }
    }

    /**
     * If the new and old cursor are same, do nothing, otherwise store the old and new cursors respectively. If the new cursor is not null, notify that data has changed and mark data as valid
     * Swap in a new Cursor, returning the old Cursor.  Unlike
     * {@link #changeCursor(Cursor)}, the returned old Cursor is <em>not</em>
     * closed.
     */
    public Cursor swapCursor(Cursor newCursor) {
        if (newCursor == mCursor) {
            return null;
        }
        final Cursor oldCursor = mCursor;
        mCursor = newCursor;
        if (mCursor != null) {
            mIndexColumnId = newCursor.getColumnIndexOrThrow("_id");
            mDataValid = true;
            notifyDataSetChanged();
        } else {
            mIndexColumnId = -1;
            mDataValid = false;
            notifyDataSetChanged();
            //There is no notifyDataSetInvalidated() method in RecyclerView.Adapter
        }
        return oldCursor;
    }

    /**
     * @param position inside the RecyclerView at which the user has performed a swipe action. If the data is valid and the cursor is not null then move the cursor to the position where the user swiped.
     */
    @Override
    public void onSwipe(int position) {
        if (mDataValid && mCursor != null && mCursor.moveToPosition(position)) {
            long itemId = getItemId(mCursor.getPosition());
            onSwipe(itemId);
        }
    }

    /**
     * @param itemId of the item that was swiped inside the RecyclerView
     */
    public abstract void onSwipe(long itemId);
}
