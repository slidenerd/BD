package slidenerd.vivz.bucketdrops.extras;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

public class BucketRecyclerView extends RecyclerView {
    /**
     * The View to display when the RecyclerView has no items at all
     */
    private View mEmptyView;

    private AdapterDataObserver mEmptyObserver = new AdapterDataObserver() {

        /**
         * Whenever this method is triggered, if the RecyclerView has some items to display, show it and hide the empty view otherwise do the reverse
         */
        @Override
        public void onChanged() {
            Adapter<?> adapter = getAdapter();
            if (adapter != null && mEmptyView != null) {
                if (adapter.getItemCount() == 0) {
                    mEmptyView.setVisibility(View.VISIBLE);
                    BucketRecyclerView.this.setVisibility(View.GONE);
                } else {
                    mEmptyView.setVisibility(View.GONE);
                    BucketRecyclerView.this.setVisibility(View.VISIBLE);
                }
            }

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

    @Override
    public void setAdapter(Adapter adapter) {
        super.setAdapter(adapter);

        if (adapter != null) {
            //Register an AdapterDataSetObserver to monitor the number of items in the RecyclerView whenever items are being added, removed
            adapter.registerAdapterDataObserver(mEmptyObserver);
        }
        mEmptyObserver.onChanged();
    }

    public void setEmptyView(View emptyView) {
        this.mEmptyView = emptyView;
    }
}