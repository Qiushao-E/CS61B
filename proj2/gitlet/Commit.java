package gitlet;

// TODO: any imports you need here

import java.io.File;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import static gitlet.Utils.*;
import static gitlet.Repository.COMMIT_DIR;
/** Represents a gitlet commit object.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author Qiushao
 */
public class Commit implements Serializable {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */

    /** The message of this Commit. */
    private String message;
    private String id;
    private String parent;
    private List<String> blobs;
    private Map<String, String> pathToBlobs;
    private String timeStamp;
    private Date currentTime;

    /* TODO: fill in the rest of this class. */
    /**
     * the init commit instructor
     */
    public Commit() {
        this.message = "initial commit";
        this.parent = "null";
        this.blobs = new ArrayList<>();
        this.pathToBlobs = new TreeMap<>();
        this.currentTime = new Date(0);
        this.timeStamp = dateToTimeStamp(currentTime);
        this.id = gernerateId();
    }

    /**
     * the default commit instructor
     */
    public Commit(String message, String parent) {
        this.message = message;
        this.parent = parent;
        this.blobs = new ArrayList<>();
        this.pathToBlobs = new TreeMap<>();
        this.currentTime = new Date();
        this.timeStamp = dateToTimeStamp(currentTime);
        this.id = gernerateId();
    }

    private static String dateToTimeStamp(Date date) {
        DateFormat dateFormat = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy Z", Locale.US);
        return dateFormat.format(date);
    }

    private String gernerateId() {
        return sha1(message, parent, blobs.toString(), pathToBlobs.toString(), timeStamp);
    }

    public String getId() {
        return this.id;
    }

    public void saveToFile() {
        File commit = join(COMMIT_DIR, id);
        createNewFile(commit);
        writeObject(commit, this);
    }
}
