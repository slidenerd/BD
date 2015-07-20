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
    public static final int FOOTER_COUNT = 1;
    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private OnSwipeListener mSwipeListener;
    private FooterClickListener mFooterClickListener;
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

    @Override
    protected int getItemViewType(Cursor cursor, int position) {
        if (cursor != null && position == cursor.getCount()) {
            return Type.FOOTER.ordinal();
        } else {
            return Type.DROP.ordinal();
        }
    }

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

    @Override
    public void onSwipe(Cursor cursor) {
        if (mSwipeListener != null) {
            long todoId = getItemId(cursor.getPosition());
            mSwipeListener.onSwipeItem(todoId);
        }
    }

    private Drop extractTodoFrom(Cursor cursor) {
        String what = cursor.getString(cursor.getColumnIndex(Database.Helper.COL_WHAT));
        long added = cursor.getLong(cursor.getColumnIndex(Database.Helper.COL_ADDED));
        long when = cursor.getLong(cursor.getColumnIndex(Database.Helper.COL_WHEN));
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


    public enum Type {
        DROP, FOOTER;
    }

    public interface FooterClickListener {
        public void onClickFooter();
    }

    public interface DropClickListener {
        public void onClickDrop(long dropId);
    }

    /**
     * Created by vivz on 17/07/15.
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
                mFooterClickListener.onClickFooter();
            }
        }
    }
}
