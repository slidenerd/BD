package slidenerd.vivz.bucketdrops.extras;

import io.realm.Realm;
import io.realm.RealmResults;
import slidenerd.vivz.bucketdrops.beans.Drop;

/**
 * Created by vivz on 23/09/15.
 */
public class DataStore {
    public static final RealmResults<Drop> getDropsByDateAscending(Realm realm) {
        return realm.where(Drop.class).findAllSorted("when", true);
    }

    public static final RealmResults<Drop> getDropsByDateDescending(Realm realm) {
        return realm.where(Drop.class).findAllSorted("when", false);
    }

    public static final RealmResults<Drop> getDropsComplete(Realm realm) {
        return realm.where(Drop.class).equalTo("completed", true).findAll();
    }

    public static final RealmResults<Drop> getDropsIncomplete(Realm realm) {
        return realm.where(Drop.class).equalTo("completed", false).findAll();
    }

    public static final RealmResults<Drop> getDropsDefault(Realm realm) {
        return realm.where(Drop.class).findAll();
    }
}
