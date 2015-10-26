package slidenerd.vivz.bucketdrops.adapters;

import android.support.v7.widget.RecyclerView;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmResults;

public abstract class RealmAdapter<T extends RealmObject, VH extends RecyclerView.ViewHolder>
        extends RecyclerView.Adapter<VH> {
    private static final int HEADER_COUNT = 1;
    private static final int FOOTER_COUNT = 1;
    protected RealmResults<T> mRealmResults;

    public RealmAdapter(Realm realm) {
        mRealmResults = getData(realm);
    }

    public int getHeaderCount() {
        return hasHeader() ? HEADER_COUNT : 0;
    }

    public int getFooterCount() {
        return hasFooter() ? FOOTER_COUNT : 0;
    }

    public boolean isHeader(int position) {
        if (hasHeader()) {
            return position < HEADER_COUNT;
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