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

    /**
     * @return 1 to indicate that a drop is complete if the status is true otherwise 0
     */
    public String getStatusAsString() {
        return (status == true) ? String.valueOf(1) : String.valueOf(0);
    }

    /**
     * @param status is a String which is expected to be 1 or 0, if its 1 it means our drop was marked as complete by the user otherwise the user has not taken any action on the drop
     * @return true if the status is 1 or false otherwise
     */
    public boolean getStatusAsBoolean(String status) {
        try {
            return Integer.parseInt(status) == 1 ? true : false;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * @param status either 1 to indicate that our drop was marked complete by the user or 0 if the user has not taken any action on the drop
     * @return true if the status is 1 or false otherwise
     */
    public boolean getStatusAsBoolean(int status) {
        return status == 1 ? true : false;
    }

    @Override
    public String toString() {
        return "\nwhat: " + what + "\nadded: " + added + "\nwhen: " + when + "\nstatus: " + status;
    }

    /**
     * @return boolean indicating whether the drop is marked as complete by the user
     */
    public boolean isComplete() {
        return status == true;
    }
}