package slidenerd.vivz.bucketdrops.adapters;

import android.support.v7.widget.RecyclerView;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmResults;

public abstract class AbstractRealmAdapter<T extends RealmObject, VH extends RecyclerView.ViewHolder>
        extends RecyclerView.Adapter<VH> {
    protected RealmResults<T> mRealmResults;

    public AbstractRealmAdapter(Realm realm) {
        mRealmResults = getData(realm);
        notifyItemRangeChanged(0, mRealmResults.size());
    }

    public int getHeaderCount() {
        return hasHeader() ? 1 : 0;
    }

    public int getFooterCount() {
        return hasFooter() ? 1 : 0;
    }

    public boolean isHeader(int position) {
        if (hasHeader()) {
            return position == 0;
        } else {
            return false;
        }
    }

    public boolean isFooter(int position) {
        if (hasFooter()) {
            return position >= getCount() + getHeaderCount();
        } else {
            return false;
        }
    }

    @Override
    public long getItemId(int i) {
        return i;
    }


    @Override
    public final int getItemViewType(int position) {
        if (isHeader(position)) {
            return ItemType.HEADER.ordinal();
        } else if (isFooter(position)) {
            return ItemType.FOOTER.ordinal();
        } else {
            return ItemType.ITEM.ordinal();
        }
    }

    public T getItem(int position) {
        if (!isHeader(position) && !isFooter(position) && !mRealmResults.isEmpty()) {
            return mRealmResults.get(position - getHeaderCount());
        }
        return null;
    }


    @Override
    public final int getItemCount() {
        return getHeaderCount() + getCount() + getFooterCount();
    }

    public final int getCount() {
        return mRealmResults.size();
    }

    public abstract boolean hasHeader();

    public abstract boolean hasFooter();

    public abstract RealmResults<T> getData(Realm realm);

    public void setData(RealmResults<T> results) {
        if (results != null) {
            mRealmResults = results;
            notifyDataSetChanged();
        }
    }

    public enum ItemType {
        HEADER, ITEM, FOOTER;
    }
}