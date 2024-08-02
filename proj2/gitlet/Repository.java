package gitlet;


import java.io.File;
import static gitlet.Utils.*;

// TODO: any imports you need here

/** Represents a gitlet repository.
 *  the repository structure:
 *  .gitlet/
 *      - objects/
 *          - commits/
 *              - ...(files of commits)
 *          - blobs/
 *              - ...(files of blobs)
 *      - branches/
 *          - master
 *          - ...(other branches)
 *      - HEAD
 *      - Stages/
 *          - addStage
 *          - removeStage
 *  does at a high level.
 *
 *  @author Qiushao
 */
public class Repository {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */

    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    /** The objects, commits, blobs directory. */
    public static final File OBJECT_DIR = join(GITLET_DIR, "objects");
    public static final File COMMIT_DIR = join(OBJECT_DIR, "commits");
    public static final File BLOB_DIR = join(OBJECT_DIR, "blobs");
    /** The branches directory. */
    public static final File BRANCH_DIR = join(GITLET_DIR, "branches");
    /** The stages directory. */
    public static final File STAGE_DIR = join(GITLET_DIR, "stages");
    /** The HEAD pointer. */
    public static File HEAD = join(GITLET_DIR, "HEAD");
    /* TODO: fill in the rest of this class. */
    /* The init command */
    public static void init() {
        checkTheDirectory();
        mkdir(GITLET_DIR);
        mkdir(OBJECT_DIR);
        mkdir(COMMIT_DIR);
        mkdir(BLOB_DIR);
        mkdir(BRANCH_DIR);
        mkdir(STAGE_DIR);
        Commit initCommit = new Commit();
        initCommit.saveToFile();
        initHEAD(initCommit.getId());
        initMaster(initCommit.getId());
    }

    private static void checkTheDirectory() {
        if (GITLET_DIR.exists()) {
            System.out.println("A Gitlet version-control system already exists in the current directory.");
            System.exit(0);
        }
    }

    private static void initHEAD(String id) {
        createNewFile(HEAD);
        writeContents(HEAD, id);
    }

    private static void setHEAD(String id) {
        writeContents(HEAD, id);
    }

    private static void initMaster(String id) {
        Branch master = new Branch(id);
        master.saveToFile();
    }

























}
