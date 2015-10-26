package slidenerd.vivz.bucketdrops.widgets;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.View;

import slidenerd.vivz.bucketdrops.adapters.RealmAdapter;

public class BucketRecyclerView extends RecyclerView {
    /**
     * The View to display when the RecyclerView has no items at all
     */
    private View mEmptyView;
    private Toolbar mToolbar;
    private AdapterDataObserver mEmptyObserver = new AdapterDataObserver() {

        /**
         * Whenever this method is triggered, if the RecyclerView has some items to display, show it and hide the empty view otherwise do the reverse
         */
        @Override
        public void onChanged() {
            checkIfEmpty();
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount) {
            checkIfEmpty();
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            checkIfEmpty();
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            checkIfEmpty();
        }
    };

    public BucketRecyclerView(Context context) {
        super(context);
    }

    public BucketRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BucketRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    private void checkIfEmpty() {
        RealmAdapter adapter = (RealmAdapter) getAdapter();
        if (adapter != null && mEmptyView != null && mToolbar != null) {
            if (adapter.getCount() == 0) {
                mEmptyView.setVisibility(View.VISIBLE);
                BucketRecyclerView.this.setVisibility(View.GONE);
                mToolbar.setVisibility(View.GONE);


            } else {
                mEmptyView.setVisibility(View.GONE);
                BucketRecyclerView.this.setVisibility(View.VISIBLE);
                mToolbar.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void setAdapter(Adapter adapter) {
        super.setAdapter(adapter);

        if (!(adapter instanceof RealmAdapter)) {
            throw new IllegalArgumentException("Adapter must be a subclass of abstract realm adapter");
        }
        if (adapter != null) {
            //Register an AdapterDataSetObserver to monitor the number of items in the RecyclerView whenever items are being added, removed
            adapter.registerAdapterDataObserver(mEmptyObserver);
        }
        mEmptyObserver.onChanged();
    }

    public void setEmptyView(View emptyView) {
        this.mEmptyView = emptyView;
    }

    public void setToolbar(Toolbar mToolbar) {
        this.mToolbar = mToolbar;
    }
}