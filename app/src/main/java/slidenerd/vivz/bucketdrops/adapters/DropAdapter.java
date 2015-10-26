package slidenerd.vivz.bucketdrops.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import slidenerd.vivz.bucketdrops.R;
import slidenerd.vivz.bucketdrops.beans.Drop;
import slidenerd.vivz.bucketdrops.extras.Util;
import slidenerd.vivz.bucketdrops.home.BucketDropsApp;

import static slidenerd.vivz.bucketdrops.adapters.SortOptions.SHOW_COMPLETE;
import static slidenerd.vivz.bucketdrops.adapters.SortOptions.SHOW_INCOMPLETE;
import static slidenerd.vivz.bucketdrops.adapters.SortOptions.SORT_ASCENDING_DATE;
import static slidenerd.vivz.bucketdrops.adapters.SortOptions.SORT_DEFAULT;
import static slidenerd.vivz.bucketdrops.adapters.SortOptions.SORT_DESCENDING_DATE;

/**
 * Created by vivz on 18/07/15.
 */
public class DropAdapter extends MutableRealmAdapter<Drop, RecyclerView.ViewHolder> {
    private Context mContext;
    private LayoutInflater mLayoutInflater;
    //An object interested in processing events when the footer of this RecyclerView is clicked
    private FooterClickListener mFooterClickListener;
    //An object interested in processing events when the AddDrop button of this RecyclerView is clicked
    private ItemClickListener mItemClickListener;
    private Realm mRealm;

    public DropAdapter(Context context, Realm realm) {
        super(realm);
        mRealm = realm;
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
    }

    public void setDropClickListener(ItemClickListener listener) {
        mItemClickListener = listener;
    }

    public void setOnFooterClickListener(FooterClickListener listener) {
        mFooterClickListener = listener;
    }

    public RealmResults<Drop> getData(Realm realm) {
        int sortOption = BucketDropsApp.loadSortOption();
        RealmResults<Drop> realmResults = null;
        if (sortOption == SHOW_COMPLETE) {

            realmResults = realm.where(Drop.class).equalTo("completed", true).findAllAsync();
        } else if (sortOption == SHOW_INCOMPLETE) {
            realmResults = realm.where(Drop.class).equalTo("completed", false).findAllAsync();
        } else if (sortOption == SORT_ASCENDING_DATE) {
            realmResults = realm.where(Drop.class).findAllSortedAsync("when", true);
        } else if (sortOption == SORT_DESCENDING_DATE) {
            realmResults = realm.where(Drop.class).findAllSortedAsync("when", false);
        } else {
            realmResults = realm.where(Drop.class).findAllAsync();
        }
        if (realmResults == null || realmResults.isEmpty() && sortOption != SORT_DEFAULT) {
            realmResults = realm.where(Drop.class).findAllAsync();
        }
        realmResults.addChangeListener(new RealmChangeListener() {
            @Override
            public void onChange() {
                notifyDataSetChanged();
            }
        });
        return realmResults;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        if (viewHolder instanceof DropHolder) {
            DropHolder dropHolder = (DropHolder) viewHolder;
            Drop drop = getItem(position);
            dropHolder.setWhat(drop.getWhat());
            dropHolder.setWhen(drop.getWhen());
            dropHolder.setBackground(drop.isCompleted());
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ItemType.FOOTER.ordinal()) {
            View root = mLayoutInflater.inflate(R.layout.footer, parent, false);
            FooterHolder viewHolder = new FooterHolder(root);
            return viewHolder;

        } else {
            View root = mLayoutInflater.inflate(R.layout.item, parent, false);
            DropHolder dropHolder = new DropHolder(root);
            return dropHolder;
        }
    }

    @Override
    public boolean hasHeader() {
        return false;
    }

    @Override
    public boolean hasFooter() {
        return true;
    }

    public void markComplete(int position) {
        Drop drop = getItem(position);
        mRealm.beginTransaction();
        drop.setCompleted(true);
        notifyItemChanged(position);
        mRealm.commitTransaction();
    }

    /**
     * An interface that notifies your class when the footer is clicked inside the RecyclerView through its onClickFooter method
     */
    public interface FooterClickListener {
        void onClickFooter();
    }

    /**
     * An interface that notifies your class when any item is clicked from your RecyclerView
     */
    public interface ItemClickListener {
        /**
         * @param position is the position of the item that was clicked by the user inside the RecylerView
         */
        void onClickDrop(int position);
    }

    public class DropHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView mTextWhat;
        private TextView mTextWhen;
        private View mRoot;

        public DropHolder(View itemView) {
            super(itemView);
            mRoot = itemView;
            mTextWhat = (TextView) mRoot.findViewById(R.id.text_what);
            mTextWhen = (TextView) mRoot.findViewById(R.id.text_when);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            if (mItemClickListener != null) {
                //Notify interested classes about the item that was clicked at the current position
                mItemClickListener.onClickDrop(getAdapterPosition());
            }
        }

        public void setWhat(String text) {
            mTextWhat.setText(text);
        }

        public void setWhen(long when) {
            mTextWhen.setText(Util.getFormattedDate(when));
        }

        @SuppressLint("NewApi")
        public void setBackground(boolean isCompleted) {
            //For items whose status is marked as complete, we would like to highlight them with a different background
            Drawable background = null;
            if (isCompleted) {
                background = new ColorDrawable(Color.argb(237, 142, 121, 187));
            } else {
                background = mContext.getResources().getDrawable(R.drawable.bg_bucket_item);
            }
            if (Util.isJellyBeanOrMore()) {
                mRoot.setBackgroundDrawable(background);
            } else {
                mRoot.setBackground(background);
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
