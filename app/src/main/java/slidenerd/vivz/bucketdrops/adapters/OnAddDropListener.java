package slidenerd.vivz.bucketdrops.adapters;

import slidenerd.vivz.bucketdrops.beans.Drop;

/**
 * Created by vivz on 17/07/15.
 */
public interface OnAddDropListener {
    /**
     * @param drop which was added by the user after opening the dialog and hitting the add button
     */
    void onClickAddDrop(Drop drop);
}
