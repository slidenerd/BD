package slidenerd.vivz.bucketdrops.extras;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

public class BucketRecyclerView extends RecyclerView {
    private View mEmptyView;

    private AdapterDataObserver mEmptyObserver = new AdapterDataObserver() {

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
            adapter.registerAdapterDataObserver(mEmptyObserver);
        }
        mEmptyObserver.onChanged();
    }

    public void setEmptyView(View emptyView) {
        this.mEmptyView = emptyView;
    }
}