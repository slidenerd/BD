package slidenerd.vivz.bucketdrops.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import slidenerd.vivz.bucketdrops.R;
import slidenerd.vivz.bucketdrops.beans.Drop;
import slidenerd.vivz.bucketdrops.database.Database;
import slidenerd.vivz.bucketdrops.extras.Util;

/**
 * Created by vivz on 18/07/15.
 */
public class ConcreteAdapter extends AbstractAdapter<RecyclerView.ViewHolder> {
    //Number of footers that you want to display inside this RecyclerView
    public static final int FOOTER_COUNT = 1;
    private Context mContext;
    private LayoutInflater mLayoutInflater;
    //An object interested in processing events when an item from this RecyclerView is swiped
    private OnSwipeListener mSwipeListener;
    //An object interested in processing events when the footer of this RecyclerView is clicked
    private FooterClickListener mFooterClickListener;
    //An object interested in processing events when the AddDrop button of this RecyclerView is clicked
    private DropClickListener mDropClickListener;

    public ConcreteAdapter(Context context) {
        super(context);
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
    }

    public void setDropClickListener(DropClickListener listener) {
        mDropClickListener = listener;
    }

    public void setOnSwipeListener(OnSwipeListener listener) {
        mSwipeListener = listener;
    }

    public void setOnFooterClickListener(FooterClickListener listener) {
        mFooterClickListener = listener;
    }

    /**
     * if the current position is at outside the cursor's capacity then we are dealing with a FOOTER else we are dealing with an Item
     *
     * @param cursor   containing the data or rows from our SQLite database which we want to display inside our RecyclerView
     * @param position of the current item within the RecyclerView whose type we need to specify.
     * @return
     */
    @Override
    protected int getItemViewType(Cursor cursor, int position) {
        if (cursor != null && position == cursor.getCount()) {
            return Type.FOOTER.ordinal();
        } else {
            return Type.DROP.ordinal();
        }
    }

    /**
     * @return the number of footer elements in this RecylcerView
     */
    @Override
    public int getFooterCount() {
        return FOOTER_COUNT;
    }


    @SuppressLint("NewApi")
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position, Cursor cursor) {
        if (viewHolder instanceof DropHolder && cursor.moveToPosition(position)) {
            DropHolder dropHolder = (DropHolder) viewHolder;
            Drop drop = extractTodoFrom(cursor);
            dropHolder.mTextWhat.setText(drop.what);
            dropHolder.mTextWhen.setText(Util.getFormattedDate(drop.when));
            //For items whose status is marked as complete, we would like to highlight them with a different background
            Drawable background = null;
            if (drop.status) {
                background = new ColorDrawable(Color.argb(237, 142, 121, 187));
            } else {
                background = mContext.getResources().getDrawable(R.drawable.bg_bucket_item);
            }
            if (Util.isJellyBeanOrMore()) {
                dropHolder.mRoot.setBackgroundDrawable(background);
            } else {
                dropHolder.mRoot.setBackground(background);
            }
        }
    }

    /**
     * Get the item id of
     *
     * @param itemId of the item that was swiped inside the RecyclerView
     */
    @Override
    public void onSwipe(long itemId) {
        if (mSwipeListener != null) {
            mSwipeListener.onSwipeItem(itemId);
        }
    }

    /**
     * @param cursor moved to the current position containing the rows from the SQLite database which you want to display inside your RecyclerView
     * @return a Drop object constructed from the current row inside the Cursor.
     */
    private Drop extractTodoFrom(Cursor cursor) {
        String what = cursor.getString(cursor.getColumnIndex(Database.Helper.COL_WHAT));
        long added = cursor.getLong(cursor.getColumnIndex(Database.Helper.COL_ADDED));
        long when = cursor.getLong(cursor.getColumnIndex(Database.Helper.COL_WHEN));
        //the data is stored as 1 for completed and 0 for incomplete in the SQLite database, hence convert it appropriately to boolean
        boolean status = cursor.getInt(cursor.getColumnIndex(Database.Helper.COL_STATUS)) == 1 ? true : false;
        return new Drop(what, added, when, status);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == Type.FOOTER.ordinal()) {
            View root = mLayoutInflater.inflate(R.layout.bucket_footer, parent, false);
            FooterHolder viewHolder = new FooterHolder(root);
            return viewHolder;

        } else {
            View root = mLayoutInflater.inflate(R.layout.drop, parent, false);
            DropHolder dropHolder = new DropHolder(root);
            return dropHolder;
        }
    }


    /**
     * The types of items being displayed by our RecyclerView, items and footers
     */
    public enum Type {
        DROP, FOOTER;
    }

    /**
     * An interface that notifies your class when the footer is clicked inside the RecyclerView through its onClickFooter method
     */
    public interface FooterClickListener {
        public void onClickFooter();
    }

    /**
     * An interface that notifies your class when any item is clicked from your RecyclerView
     */
    public interface DropClickListener {
        /**
         * @param dropId is the itemId of the item that was clicked by the user inside the RecylerView
         */
        public void onClickDrop(long dropId);
    }

    /**
     * Created by vivz on 17/07/15.
     * An interface that notifies your class when the "Add Drop" button is clicked inside the RecyclerView to launch the dialog where the user can add new drops
     */
    public interface ShowDialogAddListener {
        public void onClickShowDialogAdd();
    }

    public class DropHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView mTextWhat;
        private TextView mTextWhen;
        private View mRoot;

        public DropHolder(View itemView) {
            super(itemView);
            mRoot = itemView;
            itemView.setOnClickListener(this);
            mTextWhat = (TextView) itemView.findViewById(R.id.text_what);
            mTextWhen = (TextView) itemView.findViewById(R.id.text_when);
        }

        @Override
        public void onClick(View v) {
            if (mDropClickListener != null) {
                //Notify interested classes about the item that was clicked at the current position
                mDropClickListener.onClickDrop(ConcreteAdapter.this.getItemId(getAdapterPosition()));
            }
        }
    }

    public class FooterHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private Button mBtnAddDrop;

        public FooterHolder(View itemView) {
            super(itemView);
            mBtnAddDrop = (Button) itemView.findViewById(R.id.btn_add_drop);
            mBtnAddDrop.setTypeface(Util.loadRalewayThin(mContext));
            mBtnAddDrop.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mFooterClickListener != null) {
                //Notify interested classes about the footer that was clicked at the specified position.
                mFooterClickListener.onClickFooter();
            }
        }
    }
}
