package slidenerd.vivz.bucketdrops.extras;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import slidenerd.vivz.bucketdrops.beans.Drop;

/**
 * Created by vivz on 12/07/15.
 */
public class Sort {
    public static final int GREATER = 1;
    public static final int SMALLER = -1;
    public static final int EQUAL = 0;

    public static void byMostTimeRemaining(ArrayList<Drop> listDrops) {
        Collections.sort(listDrops, new Comparator<Drop>() {
            @Override
            public int compare(Drop lhs, Drop rhs) {

                long lhsWhen = lhs.when;
                long rhsWhen = rhs.when;
                if (lhsWhen == rhsWhen) {
                    return EQUAL;
                } else if (lhsWhen > rhsWhen) {
                    return SMALLER;
                } else {
                    return GREATER;
                }
            }
        });
    }

    public static void byLeastTimeRemaining(ArrayList<Drop> listDrops) {
        Collections.sort(listDrops, new Comparator<Drop>() {
            @Override
            public int compare(Drop lhs, Drop rhs) {
                long lhsWhen = lhs.when;
                long rhsWhen = rhs.when;
                if (lhsWhen == rhsWhen) {
                    return EQUAL;
                } else if (lhsWhen > rhsWhen) {
                    return GREATER;
                } else {
                    return SMALLER;
                }
            }
        });
    }

}
