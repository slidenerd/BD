package slidenerd.vivz.bucketdrops.beans;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by vivz on 07/07/15.
 */
public class Drop extends RealmObject {
    //The date when this row_drop was created by the user, Example: July 13 2015 converted and stored in milliseconds
    @PrimaryKey
    private long added;
    //Example "I want to take a tour of the Bahamas"
    private String what;

    //The date by which the person actually wants to perform the task, Example "July 25, 2016 converted and stored in milliseconds"
    private long when;
    //Indicating whether the task was completed or not by the person, Example "completed"
    private boolean completed;

    public Drop() {

    }

    public Drop(String what, long added, long when, boolean completed) {
        this.what = what;
        this.added = added;
        this.when = when;
        this.completed = completed;
    }

    public long getAdded() {
        return added;
    }

    public void setAdded(long added) {
        this.added = added;
    }

    public String getWhat() {
        return what;
    }

    public void setWhat(String what) {
        this.what = what;
    }

    public long getWhen() {
        return when;
    }

    public void setWhen(long when) {
        this.when = when;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
}