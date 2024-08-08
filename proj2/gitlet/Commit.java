package gitlet;

import java.io.File;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import static gitlet.Utils.*;
import static gitlet.Repository.COMMIT_DIR;
/** Represents a gitlet commit object.
 *  does at a high level.
 *
 *  @author Qiushao
 */
public class Commit implements Serializable {
    /**
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */

    /** The message of this Commit. */
    private final String message;
    private String id;
    private final List<String> parents;
    private List<String> blobs;
    private Map<String, String> pathToBlobs;
    private final String timeStamp;
    private final Date currentTime;

    /**
     * the init commit instructor
     */
    public Commit() {
        this.message = "initial commit";
        this.parents = new ArrayList<>();
        parents.add("GOD");
        this.blobs = new ArrayList<>();
        this.pathToBlobs = new TreeMap<>();
        this.currentTime = new Date(0);
        this.timeStamp = dateToTimeStamp(currentTime);
        generateId();
    }

    /**
     * the default commit instructor, creating a commit with a parent commit.
     */
    public Commit(String message, Commit parent) {
        this.message = message;
        this.parents = new ArrayList<>();
        this.parents.add(parent.getId());
        this.blobs = parent.blobs;
        this.pathToBlobs = parent.pathToBlobs;
        this.currentTime = new Date();
        this.timeStamp = dateToTimeStamp(currentTime);
        generateId();
    }

    private static String dateToTimeStamp(Date date) {
        DateFormat dateFormat = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy Z", Locale.US);
        return dateFormat.format(date);
    }

    private void generateId() {
        this.id =  sha1(message, parents.toString(), blobs.toString(), pathToBlobs.toString(), timeStamp);
    }

    public String getId() {
        return this.id;
    }

    public Map<String, String> getPathToBlobs() {
        return pathToBlobs;
    }

    public void saveToFile() {
        generateId();
        File commit = join(COMMIT_DIR, id);
        createNewFile(commit);
        writeObject(commit, this);
    }

    public void addBlob(String blobPath, String commitId) {
        pathToBlobs.put(blobPath, commitId);
    }

    public void removeBlob(String blobPath) {
        pathToBlobs.remove(blobPath);
    }

    public void print() {
        System.out.println("===");
        System.out.println("commit " + id);
        if (parents.size() > 1) {
            String message = "Merge: " + parents.get(0).substring(0, 7) + " " + parents.get(1).substring(0, 7);
            System.out.println(message);
        }
        System.out.println("Date: " + timeStamp);
        System.out.println(message);
        System.out.println();
    }

    public String getFirstParent() {
        return parents.get(0);
    }

    public String getMessage() {
        return message;
    }

    public void addParent(String parent) {
        this.parents.add(parent);
    }
}
