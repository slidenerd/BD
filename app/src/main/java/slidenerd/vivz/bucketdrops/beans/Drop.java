package slidenerd.vivz.bucketdrops.beans;

/**
 * Created by vivz on 07/07/15.
 */
public class Drop {
    //Example "I want to take a tour of the Bahamas"
    public String what;
    //The date when this item was created by the user, Example: July 13 2015 converted and stored in milliseconds
    public long added;
    //The date by which the person actually wants to perform the task, Example "July 25, 2016 converted and stored in milliseconds"
    public long when;
    //Indicating whether the task was completed or not by the person, Example "completed"
    public boolean status;

    public Drop(String what, long added, long when, boolean status) {
        this.what = what;
        this.added = added;
        this.when = when;
        this.status = status;
    }

    public String getStatusAsString() {
        return (status == true) ? String.valueOf(1) : String.valueOf(0);
    }

    public boolean getStatusAsBoolean(String statusString) {
        boolean status = false;
        try {
            status = Integer.parseInt(statusString) == 1 ? true : false;
        } catch (NumberFormatException e) {

        }
        return status;
    }

    public boolean getStatusAsBoolean(int statusInt) {
        boolean status = false;
        status = statusInt == 1 ? true : false;
        return status;
    }

    @Override
    public String toString() {
        return "\nwhat: " + what + "\nadded: " + added + "\nwhen: " + when + "\nstatus: " + status;
    }

    public boolean isComplete() {
        return status == true;
    }
}