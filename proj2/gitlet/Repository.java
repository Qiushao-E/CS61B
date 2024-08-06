package gitlet;



import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static gitlet.Utils.*;

// TODO: any imports you need here

/** Represents a gitlet repository.
 *  The repository structure:
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
    private static final File HEAD = join(GITLET_DIR, "HEAD");

    /** The current commit. */
    private static Commit currentCommit;

    /** The current branch. */
    private static final File CURRENT_BRANCH = join(GITLET_DIR, "BRANCH");

    /* TODO: fill in the rest of this class. */


    /**
     * The init command
     */
    public static void init() {
        checkIfTheDirectoryNotExist();
        mkdir(GITLET_DIR);
        mkdir(COMMIT_DIR);
        mkdir(BLOB_DIR);
        mkdir(BRANCH_DIR);
        mkdir(STAGE_DIR);
        Commit initCommit = new Commit();
        initCommit.saveToFile();
        currentCommit = initCommit;
        setHEAD();
        initMaster();
        initStage();
    }

    /**
     *  Check if the .gitlet/ directory exists. If yes, exit the program.
     *  Only serving for the init command.
     */
    private static void checkIfTheDirectoryNotExist() {
        if (GITLET_DIR.exists()) {
            System.out.println("A Gitlet version-control system already exists in the current directory.");
            System.exit(0);
        }
    }

    /**
     *  Check if the .gitlet/ directory exists. If no, exit the program.
     *  In contradiction with the checkIfTheDirectoryNotExist method.
     */
    private static void checkIfTheDirectoryExist() {
        if (!GITLET_DIR.exists()) {
            System.out.println("Not in an initialized Gitlet directory.");
            System.exit(0);
        }
    }

    /**
     * Set the HEAD pointer, pointing at the currentCommit.
     */
    private static void setHEAD() {
        createNewFile(HEAD);
        writeContents(HEAD, currentCommit.getId());
    }

    /**
     * Initial the first branch master, and point it at the initCommit.
     * Only serving for the init command.
     */
    private static void initMaster() {
        Branch master = new Branch(currentCommit);
        master.saveToFile();
        createNewFile(CURRENT_BRANCH);
        writeContents(CURRENT_BRANCH, "master");
    }

    /**
     *  Initial the stages, including addStage and removeStage.
     */
    private static void initStage() {
        Stage addStage = new Stage("addStage");
        Stage removeStage = new Stage("removeStage");
        addStage.saveToFile();
        removeStage.saveToFile();
    }

    /**
     *  Set the branch pointer at the current commit.
     */
    private static void setBranch() {
        File branchFile = join(BRANCH_DIR, readContentsAsString(CURRENT_BRANCH));
        Branch branch = readObject(branchFile, Branch.class);
        branch.setCommitPointer(currentCommit);
        branch.saveToFile();
    }

    /**
     * The add [fileName] command
     * @param fileName the name of the file to be added.
     */
    public static void add(String fileName) {
        checkIfTheDirectoryExist();
        File file = join(CWD, fileName);
        if (readRemoveStage().getPathToBlobs().containsKey(file.getPath())) {
            readRemoveStage().removeBlob(file.getPath());
        } else {
            checkFileExist(file);
            checkTheSame(file);
            Blob fileBlob = new Blob(file);
            fileBlob.saveToFile();
            Stage addStage = readAddStage();
            addStage.addBlob(fileBlob);
            addStage.saveToFile();
        }
    }

    private static void checkTheSame(File file) {
        if (readHEAD().getPathToBlobs().containsKey(file.getPath())) {
            Blob fileBlob = new Blob(file);
            if (Objects.equals(fileBlob.getId(), readHEAD().getPathToBlobs().get(file.getPath()))) {
                System.exit(0);
            }
        }
    }
    /**
     * Return the addStage.
     */
    private static Stage readAddStage() {
        File addStageFile = join(STAGE_DIR, "addStage");
        return readObject(addStageFile, Stage.class);
    }

    /**
     * Return the removeStage.
     */
    private static Stage readRemoveStage() {
        File removeStageFile = join(STAGE_DIR, "removeStage");
        return readObject(removeStageFile, Stage.class);
    }
    /**
     * Check if the file exists. If not, exit the program.
     * @param file the file to be checked
     */
    private static void checkFileExist(File file) {
        if (!file.exists()) {
            System.out.println("File does not exist.");
            System.exit(0);
        }
    }

    /**
     * The commit [message] command.
     * @param message the commit message.
     */
    public static void commit(String message) {
        checkIfTheDirectoryExist();
        checkStageIsEmpty();
        Commit newCommit = new Commit(message, readHEAD());
        Map<String, String> addBlobs = readAddStage().getPathToBlobs();
        Map<String, String> removeBlobs = readRemoveStage().getPathToBlobs();
        Map<String, String> commitBlobs = newCommit.getPathToBlobs();
        List<String> toAdd = new ArrayList<>();
        List<String> toRemove = new ArrayList<>();
        for (String addBlobPath: addBlobs.keySet()) {
            if (!commitBlobs.containsKey(addBlobPath)) {
                newCommit.addBlob(addBlobPath, addBlobs.get(addBlobPath));
            }
            for (String blobPath: commitBlobs.keySet()) {
                if (Objects.equals(blobPath, addBlobPath)
                        && !Objects.equals(commitBlobs.get(blobPath), addBlobs.get(addBlobPath))) {
                    toRemove.add(blobPath);
                    toAdd.add(blobPath);
                }
            }
        }
        for (String removeBlobPath: removeBlobs.keySet()) {
            for (String blobPath: commitBlobs.keySet()) {
                if (Objects.equals(removeBlobPath, blobPath)) {
                    toRemove.add(removeBlobPath);
                }
            }
        }
        for (String blobToRemove: toRemove) {
            newCommit.removeBlob(blobToRemove);
        }
        for (String blobToAdd: toAdd) {
            newCommit.addBlob(blobToAdd, addBlobs.get(blobToAdd));
        }
        newCommit.saveToFile();
        currentCommit = newCommit;
        setHEAD();
        setBranch();
        clearStage();
    }

    /**
     * Return the commit that HEAD pointed at.
     */
    private static Commit readHEAD() {
        File headCommit = join(COMMIT_DIR, readContentsAsString(HEAD));
        return readObject(headCommit, Commit.class);
    }

    /**
     * Check if the stage area is empty, if yes, exit the program.
     */
    private static void checkStageIsEmpty() {
        if (readAddStage().isEmpty() && readRemoveStage().isEmpty()) {
            System.out.println("No changes added to the commit.");
            System.exit(0);
        }
    }

    /**
     * Clear the stage area.
     */
    private static void clearStage() {
        readAddStage().clear();
        readRemoveStage().clear();
    }

    /**
     * The rm [fileName] command
     * @param fileName the name of the file to be removed
     */
    public static void remove(String fileName) {
        checkIfTheDirectoryExist();
        checkRemove(fileName);
        Map<String, String> addBlobs = readAddStage().getPathToBlobs();
        File file = join(CWD, fileName);
        String filePath = file.getPath();
        if (addBlobs.containsKey(filePath)) {
            readAddStage().removeBlob(filePath);
        } else {
            Map<String, String> currentCommitBlobs = readHEAD().getPathToBlobs();
            if (currentCommitBlobs.containsKey(filePath)) {
                String id = currentCommitBlobs.get(filePath);
                readRemoveStage().addBlob(filePath, id);
                readHEAD().removeBlob(filePath);
                if (file.exists()) {
                    restrictedDelete(file);
                }
            }
        }
    }

    /**
     * Check whether the rm command is legal.
     */
    private static void checkRemove(String fileName) {
        Map<String, String> addBlobs = readAddStage().getPathToBlobs();
        Map<String, String> currentCommitBlobs = readHEAD().getPathToBlobs();
        String filePath = join(CWD, fileName).getPath();
        if (!addBlobs.containsKey(filePath) && !currentCommitBlobs.containsKey(filePath)) {
            System.out.println("No reason to remove the file.");
            System.exit(0);
        }
    }

    /**
     * The log command.
     */
    public static void log() {
        checkIfTheDirectoryExist();
        currentCommit = readHEAD();
        while (!Objects.equals(currentCommit.getFirstParent(), "GOD")) {
            currentCommit.print();
            File next = join(COMMIT_DIR, currentCommit.getFirstParent());
            currentCommit = readObject(next, Commit.class);
        }
        currentCommit.print();
    }

    /**
     * The global-log command.
     */
    public static void globalLog() {
        checkIfTheDirectoryExist();
        List<String> commits = plainFilenamesIn(COMMIT_DIR);
        assert commits != null;
        for (String commitId: commits) {
            File commitFile = join(COMMIT_DIR, commitId);
            readObject(commitFile, Commit.class).print();
        }
    }

    /**
     * The find [commitMessage] command.
     * @param commitMessage the message that the printed commits contain
     */
    public static void find(String commitMessage) {
        checkIfTheDirectoryExist();
        List<String> commitToPrint = new ArrayList<>();
        List<String> commits = plainFilenamesIn(COMMIT_DIR);
        assert commits != null;
        for (String commitId: commits) {
            File commitFile = join(COMMIT_DIR, commitId);
            String message = readObject(commitFile, Commit.class).getMessage();
            if (Objects.equals(commitMessage, message)) {
                commitToPrint.add(commitId);
            }
        }
        if (commitToPrint.isEmpty()) {
            System.out.println("Found no commit with that message.");
        } else {
            for (String commitId: commitToPrint) {
                System.out.println(commitId);
            }
        }
    }

    /**
     * The status command.
     */
    public static void status() {
        checkIfTheDirectoryExist();
        List<String> branches = plainFilenamesIn(BRANCH_DIR);
        String currentBranch = readContentsAsString(CURRENT_BRANCH);
        System.out.println("=== Branches ===");
        assert branches != null;
        for (String branchName: branches) {
            if (Objects.equals(branchName, currentBranch)) {
                System.out.println("*" + branchName);
            } else {
                System.out.println(branchName);
            }
        }
        System.out.println("\n=== Staged Files ===");
        readAddStage().print();
        System.out.println("\n=== Removed Files ===");
        readRemoveStage().print();
        System.out.println("\n=== Modifications Not Staged For Commit ===");
        System.out.println("\n=== Untracked Files ===\n");
    }

    /**
     * The branch [branchName] command.
     * @param branchName the new branch name
     */
    public static void branch(String branchName) {
        checkIfTheDirectoryExist();
        List<String> branches = plainFilenamesIn(BRANCH_DIR);
        assert branches != null;
        if (branches.contains(branchName)) {
            System.out.println("A branch with that name already exists.");
            System.exit(0);
        }
        Branch branch = new Branch(branchName, readHEAD());
        branch.saveToFile();
    }

    /**
     * The rm-branch [branchName] command.
     * @param branchName the name of the branch to be removed.
     */
    public static void removeBranch(String branchName) {
        checkIfTheDirectoryExist();
        List<String> branches = plainFilenamesIn(BRANCH_DIR);
        assert branches != null;
        if (!branches.contains(branchName)) {
            System.out.println("A branch with that name does not exist.");
            System.exit(0);
        }
        String currentBranch = readContentsAsString(CURRENT_BRANCH);
        if (currentBranch.equals(branchName)) {
            System.out.println("Cannot remove the current branch.");
            System.exit(0);
        }
        File branchFile = join(BRANCH_DIR, branchName);
        branchFile.delete();
    }

    /**
     * The checkout -- [fileName] command.
     * @param fileName the name of the file
     */
    public static void checkoutHeadFile(String fileName) {
        checkIfTheDirectoryExist();
        checkoutFileHelper(readHEAD(), fileName);
    }

    /**
     * The checkout [commitId] -- [fileName] command.
     * @param commitId the commit id
     * @param fileName the name of the file
     */
    public static void checkoutCommitFile(String commitId, String fileName) {
        checkIfTheDirectoryExist();
        File commitFile = join(COMMIT_DIR, commitId);
        if (!commitFile.exists()) {
            System.out.println("No commit with that id exists.");
            System.exit(0);
        } else {
            Commit commit = readObject(commitFile, Commit.class);
            checkoutFileHelper(commit, fileName);
        }
    }

    /**
     * The checkout [branchName] command.
     * @param branchName the name of the branch
     */
    public static void checkoutBranch(String branchName) {
        checkIfTheDirectoryExist();
        File branchFile = join(BRANCH_DIR, branchName);
        if (!branchFile.exists()) {
            System.out.println("No such branch exists.");
            System.exit(0);
        }
        String currentBranch = readContentsAsString(CURRENT_BRANCH);
        if (currentBranch.equals(branchName)) {
            System.out.println("No need to checkout the current branch.");
            System.exit(0);
        }
        Branch branch = readObject(branchFile, Branch.class);
        File newCommitFile = join(COMMIT_DIR, branch.getCommitId());
        currentCommit = readObject(newCommitFile, Commit.class);
        checkoutBranchHelper(currentCommit, readHEAD());
        writeContents(CURRENT_BRANCH, branchName);
        setHEAD();
        clearStage();
    }

    /**
     * Change the CWD status, from the old commit to the new commit.
     * @param newCommit the new commit that will be checked out
     * @param oldCommit the old commit
     */
    private static void checkoutBranchHelper(Commit newCommit, Commit oldCommit) {
        Map<String, String> oldBlobs = oldCommit.getPathToBlobs();
        Map<String, String> newBlobs = newCommit.getPathToBlobs();
        for (String filePath: newBlobs.keySet()) {
            File file = new File(filePath);
            if (!oldBlobs.containsKey(filePath)) {
                if (file.exists()) {
                    System.out.println("There is an untracked file in the way;" +
                            " delete it, or add and commit it first.");
                    System.exit(0);
                }
            }
        }
        for (String filePath: newBlobs.keySet()) {
            File file = new File(filePath);
            checkoutFileHelper(newCommit, file.getName());
        }
        for (String filePath: oldBlobs.keySet()) {
            if (!newBlobs.containsKey(filePath)) {
                File deleteFile = new File(filePath);
                deleteFile.delete();
            }
        }
    }

    /**
     * Check out the file in the special commit, serving for the checkout command.
     * @param commit the special commit
     * @param fileName the name of the file
     */
    private static void checkoutFileHelper(Commit commit, String fileName) {
        File file = join(CWD, fileName);
        String filePath = file.getPath();
        if (!commit.getPathToBlobs().containsKey(filePath)) {
            System.out.println("File does not exist in that commit.");
            System.exit(0);
        }
        File blobFile = join(BLOB_DIR, commit.getPathToBlobs().get(filePath));
        Blob blob = readObject(blobFile, Blob.class);
        byte[] fileContent = blob.getBytes();
        if (file.exists()) {
            file.delete();
        }
        createNewFile(file);
        writeContents(file, fileContent);
    }

    /**
     * The reset [commitId] command.
     * @param commitId the id of the commit which we will reset to
     */
    public static void reset(String commitId) {
        File commitFile = join(COMMIT_DIR, commitId);
        if (!commitFile.exists()) {
            System.out.println("No commit with that id exists.");
            System.exit(0);
        }
        Commit newCommit = readObject(commitFile, Commit.class);
        Commit oldCommit = readHEAD();
        checkoutBranchHelper(newCommit, oldCommit);
        currentCommit = newCommit;
        setHEAD();
        setBranch();
        clearStage();
    }
}
