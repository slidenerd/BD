package slidenerd.vivz.bucketdrops.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;

/**
 * Created by skyfishjy on 10/31/14.
 */

public abstract class AbstractAdapter<VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> implements ItemTouchHelperAdapter {

    private Context mContext;

    private Cursor mCursor;

    private boolean mDataValid;

    private int mRowIdColumn;

    // Do you want to show the footer in the recyclerview when there are no items? if true then show items, else dont show items
    private boolean mShowFooterWhenEmpty = false;

    public AbstractAdapter(Context context) {
        mContext = context;
    }

    private void createCursor(Cursor cursor) {
        mCursor = cursor;
        mDataValid = cursor != null;
        mRowIdColumn = mDataValid ? mCursor.getColumnIndex("_id") : -1;
        notifyDataSetChanged();
    }

    public void setShowFooterWhenEmpty(boolean showFooterWhenEmpty) {
        mShowFooterWhenEmpty = showFooterWhenEmpty;
    }

    public Cursor getCursor() {
        return mCursor;
    }

    public void setCursor(Cursor cursor) {
        if (mCursor == null) {
            createCursor(cursor);
        } else {
            changeCursor(cursor);
        }
    }

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

    @Override
    public int getItemViewType(int position) {
        return getItemViewType(mCursor, position);
    }

    protected abstract int getItemViewType(Cursor cursor, int position);

    public abstract int getFooterCount();

    @Override
    public long getItemId(int position) {
        if (mDataValid && mCursor != null && mCursor.moveToPosition(position)) {
            return mCursor.getLong(mRowIdColumn);
        }
        return 0;
    }

    @Override
    public void setHasStableIds(boolean hasStableIds) {
        super.setHasStableIds(true);
    }

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
            mRowIdColumn = newCursor.getColumnIndexOrThrow("_id");
            mDataValid = true;
            notifyDataSetChanged();
        } else {
            mRowIdColumn = -1;
            mDataValid = false;
            notifyDataSetChanged();
            //There is no notifyDataSetInvalidated() method in RecyclerView.Adapter
        }
        return oldCursor;
    }

    @Override
    public void onSwipe(int position) {
        if (mDataValid && mCursor != null && mCursor.moveToPosition(position)) {
            onSwipe(mCursor);
        }
    }

    public abstract void onSwipe(Cursor cursor);
}
