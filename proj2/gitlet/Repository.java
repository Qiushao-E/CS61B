package gitlet;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static gitlet.Utils.*;

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
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */

    /** The current working directory. */
    private static File CWD = new File(System.getProperty("user.dir"));

    /** The .gitlet directory. */
    private static File GITLET_DIR = join(CWD, ".gitlet");

    /** The objects, commits, blobs directory. */
    private static File OBJECT_DIR = join(GITLET_DIR, "objects");
    private static File COMMIT_DIR = join(OBJECT_DIR, "commits");
    private static File BLOB_DIR = join(OBJECT_DIR, "blobs");

    /** The branches' directory. */
    private static File BRANCH_DIR = join(GITLET_DIR, "branches");

    /** The stages' directory. */
    private static File STAGE_DIR = join(GITLET_DIR, "stages");

    /** The remotes' directory. */
    private static File REMOTE_DIR = join(GITLET_DIR, "remotes");

    /** The HEAD pointer. */
    private static File HEAD = join(GITLET_DIR, "HEAD");

    /** The current commit. */
    private static Commit currentCommit;

    /** The current branch. */
    private static File CURRENT_BRANCH = join(GITLET_DIR, "BRANCH");

    public static File getCWD() {
        return CWD;
    }

    public static File getCommitDir() {
        return COMMIT_DIR;
    }

    public static File getBlobDir() {
        return BLOB_DIR;
    }

    public static File getBranchDir() {
        return BRANCH_DIR;
    }

    public static File getStageDir() {
        return STAGE_DIR;
    }

    public static File getRemoteDir() {
        return REMOTE_DIR;
    }
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
        mkdir(REMOTE_DIR);
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
            System.out.println("A Gitlet version-control system already exists "
                     + "in the current directory.");
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
     *  The Initial stages, including addStage and removeStage.
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
            if (Objects.equals(fileBlob.getId(),
                    readHEAD().getPathToBlobs().get(file.getPath()))) {
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
        commitHelper(newCommit);
        newCommit.saveToFile();
        currentCommit = newCommit;
        setHEAD();
        setBranch();
        clearStage();
    }

    /**
     * Set the commit map.
     * @param newCommit the new commit
     */
    private static void commitHelper(Commit newCommit) {
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
        while (!currentCommit.getParents().isEmpty()) {
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
        List<String> branches = listFiles(BRANCH_DIR.getPath());
        String currentBranch = readContentsAsString(CURRENT_BRANCH);
        System.out.println("=== Branches ===");
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
        modifiedFiles();
        System.out.println("\n=== Untracked Files ===");
        untrackedFiles();
        System.out.println("\n");
    }

    /**
     * Print out the modified files.
     */
    private static void modifiedFiles() {
        Map<String, String> currentFiles = readHEAD().getPathToBlobs();
        for (String filePath: currentFiles.keySet()) {
            File file = new File(filePath);
            if (!file.exists()) {
                if (!readRemoveStage().containsFile(filePath)) {
                    System.out.println(file.getName() + " (deleted)");
                }
                break;
            }
            Blob fileBlob = new Blob(file);
            if (!Objects.equals(fileBlob.getId(), currentFiles.get(filePath))) {
                if (!readAddStage().containsFile(filePath)) {
                    System.out.println(file.getName() + " (modified)");
                }
            }
        }
        for (String filePath: readAddStage().getPathToBlobs().keySet()) {
            File file = new File(filePath);
            if (!file.exists()) {
                System.out.println(file.getName() + " (deleted)");
                break;
            }
            Blob fileBlob = new Blob(file);
            if (!Objects.equals(fileBlob.getId(),
                    readAddStage().getPathToBlobs().get(filePath))) {
                System.out.println(file.getName() + " (modified)");
            }
        }
    }

    /**
     * Print out the untracked files.
     */
    private static void untrackedFiles() {
        List<String> files = plainFilenamesIn(CWD);
        if (files != null) {
            for (String fileName: files) {
                File file = join(CWD, fileName);
                String filePath = file.getPath();
                if (!readHEAD().getPathToBlobs().containsKey(filePath)
                        && !readAddStage().containsFile(filePath)
                        && !readRemoveStage().containsFile(filePath)) {
                    System.out.println(fileName);
                }
            }
        }
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
        File commitFile;
        if (commitId.length() < UID_LENGTH) {
            commitFile = shortIdCommit(commitId);
        } else {
            commitFile = join(COMMIT_DIR, commitId);
        }

        if (!commitFile.exists()) {
            System.out.println("No commit with that id exists.");
            System.exit(0);
        } else {
            Commit commit = readObject(commitFile, Commit.class);
            checkoutFileHelper(commit, fileName);
        }
    }

    /**
     * @param shortId the short id of the commit
     * @return the commit file
     */
    private static File shortIdCommit(String shortId) {
        List<String> commits = plainFilenamesIn(COMMIT_DIR);
        int length = shortId.length();
        assert commits != null;
        for (String commitId: commits) {
            if (commitId.substring(0, length).equals(shortId)) {
                return join(COMMIT_DIR, commitId);
            }
        }
        return null;
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
                    System.out.println("There is an untracked file in the way; "
                            + "delete it, or add and commit it first.");
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


    /**
     * The merge [branchName] command.
     * @param branchName the name of the branch which will be merged
     */
    public static void merge(String branchName) {
        checkIfTheDirectoryExist();
        checkMerge(branchName);
        Commit spiltCommit = getSpiltPoint(branchName);
        File branchFile = join(BRANCH_DIR, branchName);
        Branch branch = readObject(branchFile, Branch.class);
        String branchCommitId = branch.getCommitId();
        File branchCommitFile = join(COMMIT_DIR, branchCommitId);
        Commit branchCommit = readObject(branchCommitFile, Commit.class);
        easyMerge(branchCommit, spiltCommit);
        Map<String, String> result = mergeResult(spiltCommit, branchCommit, readHEAD());
        Boolean conflictFlag = checkMergeOperations(result);
        mergeOperations(result, branchCommit);
        mergeCommit(branchName, branchCommit);
        if (conflictFlag) {
            System.out.println("Encountered a merge conflict.");
        }
    }

    /**
     * Check if the merge operation is legal.
     * @param branchName the name of the branch which will be merged
     */
    private static void checkMerge(String branchName) {
        if (!readAddStage().isEmpty() || !readRemoveStage().isEmpty()) {
            System.out.println("You have uncommitted changes.");
            System.exit(0);
        }
        File branchFile = join(BRANCH_DIR, branchName);
        if (!branchFile.exists()) {
            System.out.println("A branch with that name does not exist.");
            System.exit(0);
        }
        String currentBranch = readContentsAsString(CURRENT_BRANCH);
        if (Objects.equals(currentBranch, branchName)) {
            System.out.println("Cannot merge a branch with itself.");
            System.exit(0);
        }
    }

    /**
     * @param branchName the name of the branch to be merged
     * @return the spilt point commit of the head branch and the given branch
     */
    private static Commit getSpiltPoint(String branchName) {
        File branchFile = join(BRANCH_DIR, branchName);
        Branch branch = readObject(branchFile, Branch.class);
        String commitId = branch.getCommitId();
        File commitFile = join(COMMIT_DIR, commitId);
        Commit commitA = readObject(commitFile, Commit.class);
        Commit commitB = readHEAD();
        Map<String, Integer> routeA = getRouteToInit(commitA);
        Map<String, Integer> routeB = getRouteToInit(commitB);
        String spiltPointCommitId = "";
        int minValue = Integer.MAX_VALUE;
        for (String commit: routeA.keySet()) {
            if (routeB.containsKey(commit)) {
                if (routeB.get(commit) < minValue) {
                    spiltPointCommitId = commit;
                    minValue = routeB.get(commit);
                }
            }
        }
        File spiltPointCommit = join(COMMIT_DIR, spiltPointCommitId);
        return readObject(spiltPointCommit, Commit.class);
    }

    /**
     * @param commit the start commit
     * @return a map, including the route to the initial commit
     */
    private static Map<String, Integer> getRouteToInit(Commit commit) {
        Map<String, Integer> route = new TreeMap<>();
        Queue<String> queue = new ArrayDeque<>();
        queue.add(commit.getId());
        route.put(commit.getId(), 0);
        while (!queue.isEmpty()) {
            String commitId = queue.poll();
            File commitFile = join(COMMIT_DIR, commitId);
            Commit thisCommit = readObject(commitFile, Commit.class);
            for (String parentCommit: thisCommit.getParents()) {
                if (route.containsKey(parentCommit)) {
                    break;
                } else {
                    queue.add(parentCommit);
                    route.put(parentCommit, route.get(commitId) + 1);
                }
            }
        }
        return route;
    }

    /**
     * @param branchCommit the commit that branch pointed at
     * @param spiltCommit the spilt point commit
     */
    private static void easyMerge(Commit branchCommit, Commit spiltCommit) {
        if (Objects.equals(spiltCommit.getId(), readHEAD().getId())) {
            checkoutBranchHelper(branchCommit, readHEAD());
            System.out.println("Current branch fast-forwarded.");
            System.exit(0);
        }
        if (Objects.equals(spiltCommit.getId(), branchCommit.getId())) {
            System.out.println("Given branch is an ancestor of the current branch.");
            System.exit(0);
        }
    }

    /**
     * @param spiltCommit the spilt point commit
     * @param branchCommit the commit that branch pointed at
     * @param current the current commit, a.k.a. the HEAD commit
     * @return the merge result, representing in a map. The key is file path,
     * the value is the blob id,
     * and the "0" represents not exist,
     * the "conflict" represents there exists conflicts
     */
    private static Map<String, String> mergeResult(Commit spiltCommit,
                                                   Commit branchCommit,
                                                   Commit current) {
        Map<String, String> allFiles = new TreeMap<>();
        Map<String, String> spiltCommitFiles = spiltCommit.getPathToBlobs();
        Map<String, String> branchCommitFiles = branchCommit.getPathToBlobs();
        Map<String, String> currentCommitFiles = current.getPathToBlobs();
        Map<String, String> result = new TreeMap<>();
        for (String filePath: spiltCommitFiles.keySet()) {
            allFiles.put(filePath, spiltCommitFiles.get(filePath));
        }
        for (String filePath: branchCommitFiles.keySet()) {
            allFiles.put(filePath, branchCommitFiles.get(filePath));
        }
        for (String filePath: currentCommitFiles.keySet()) {
            allFiles.put(filePath, currentCommitFiles.get(filePath));
        }
        for (String filePath: allFiles.keySet()) {
            if (!spiltCommitFiles.containsKey(filePath)) {
                spiltCommitFiles.put(filePath, "0");
            }
            if (!branchCommitFiles.containsKey(filePath)) {
                branchCommitFiles.put(filePath, "0");
            }
            if (!currentCommitFiles.containsKey(filePath)) {
                currentCommitFiles.put(filePath, "0");
            }
        }
        for (String filePath : allFiles.keySet()) {
            if (Objects.equals(spiltCommitFiles.get(filePath),
                    branchCommitFiles.get(filePath))
                    && !Objects.equals(spiltCommitFiles.get(filePath),
                    currentCommitFiles.get(filePath))) {
                result.put(filePath, currentCommitFiles.get(filePath));
            } else if (!Objects.equals(spiltCommitFiles.get(filePath),
                    branchCommitFiles.get(filePath))
                    && Objects.equals(spiltCommitFiles.get(filePath),
                    currentCommitFiles.get(filePath))) {
                result.put(filePath, branchCommitFiles.get(filePath));
            } else if (Objects.equals(currentCommitFiles.get(filePath),
                    branchCommitFiles.get(filePath))) {
                result.put(filePath, currentCommitFiles.get(filePath));
            } else if (!Objects.equals(spiltCommitFiles.get(filePath),
                    branchCommitFiles.get(filePath))
                    && !Objects.equals(spiltCommitFiles.get(filePath),
                    currentCommitFiles.get(filePath))
                    && !Objects.equals(branchCommitFiles.get(filePath),
                    currentCommitFiles.get(filePath))) {
                result.put(filePath, "conflict");
            }
        }
        return result;
    }

    /**
     * Check if the merge result is legal.
     * @param result the map that contains the merge result
     */
    private static Boolean checkMergeOperations(Map<String, String> result) {
        Map<String, String> currentCommitFiles = readHEAD().getPathToBlobs();
        for (String filePath: result.keySet()) {
            if (!currentCommitFiles.containsKey(filePath)) {
                File file = new File(filePath);
                if (file.exists()) {
                    System.out.println("There is an untracked file in the way; "
                            + "delete it, or add and commit it first.");
                    System.exit(0);
                }
            }
        }
        return result.containsValue("conflict");
    }

    /**
     * Do the merge operations.
     * @param result the map that contains the merge result
     */
    private static void mergeOperations(Map<String, String> result,
                                        Commit branchCommit) {
        Map<String, String> currentCommitFiles = readHEAD().getPathToBlobs();
        Map<String, String> branchCommitFiles = branchCommit.getPathToBlobs();
        for (String filePath: result.keySet()) {
            switch (result.get(filePath)) {
                case "0":
                    if (currentCommitFiles.containsKey(filePath)) {
                        File file = new File(filePath);
                        remove(file.getName());
                    }
                    break;
                case "conflict":
                    String currentBlobId = currentCommitFiles.get(filePath);
                    String branchBlobId = branchCommitFiles.get(filePath);
                    String content;
                    String current;
                    String branch;
                    if (!Objects.equals(currentBlobId, "0")
                            && !Objects.equals(branchBlobId, "0")) {
                        Blob currentBlob = readObject(join(BLOB_DIR,
                                currentBlobId), Blob.class);
                        Blob branchBlob = readObject(join(BLOB_DIR,
                                branchBlobId), Blob.class);
                        byte[] currentContents = currentBlob.getBytes();
                        byte[] branchContents = branchBlob.getBytes();
                        current = new String(currentContents,
                                StandardCharsets.UTF_8);
                        branch = new String(branchContents,
                                StandardCharsets.UTF_8);
                    } else if (Objects.equals(currentBlobId, "0")
                            && !Objects.equals(branchBlobId, "0")) {
                        Blob branchBlob = readObject(join(BLOB_DIR,
                                branchBlobId), Blob.class);
                        byte[] branchContents = branchBlob.getBytes();
                        current = "";
                        branch = new String(branchContents,
                                StandardCharsets.UTF_8);
                    } else if (!Objects.equals(currentBlobId, "0")
                            && Objects.equals(branchBlobId, "0")) {
                        Blob currentBlob = readObject(join(BLOB_DIR,
                                currentBlobId), Blob.class);
                        byte[] currentContents = currentBlob.getBytes();
                        current = new String(currentContents, StandardCharsets.UTF_8);
                        branch = "";
                    } else {
                        branch = "";
                        current = "";
                    }
                    content = "<<<<<<< HEAD\n" + current + "=======\n"
                            + branch + ">>>>>>>\n";
                    File file = new File(filePath);
                    if (file.exists()) {
                        file.delete();
                    }
                    createNewFile(file);
                    writeContents(file, content);
                    Blob fileBlob = new Blob(file);
                    fileBlob.saveToFile();
                    readAddStage().addBlob(filePath, fileBlob.getId());
                    break;
                default:
                    if (Objects.equals(currentCommitFiles.get(filePath), "0")) {
                        Blob blob = readObject(join(BLOB_DIR,
                                result.get(filePath)), Blob.class);
                        mergeFileHelper(blob);
                    } else if (!Objects.equals(result.get(filePath),
                            currentCommitFiles.get(filePath))) {
                        Blob blob = readObject(join(BLOB_DIR,
                                result.get(filePath)), Blob.class);
                        mergeFileHelper(blob);
                    } else {
                        break;
                    }
            }
        }
    }

    /**
     * Help create the file and add it to the add stage.
     * @param fileBlob the blob of the file
     */
    private static void mergeFileHelper(Blob fileBlob) {
        File file = new File(fileBlob.getFilePath());
        byte[] contents = fileBlob.getBytes();
        if (file.exists()) {
            file.delete();
        }
        createNewFile(file);
        writeContents(file, contents);
        readAddStage().addBlob(file.getPath(), fileBlob.getId());
    }

    /**
     * Make the merge commit.
     * @param branchName the branch name
     * @param branchCommit the branch commit
     */
    private static void mergeCommit(String branchName, Commit branchCommit) {
        String message = "Merged " + branchName + " into "
                + readContentsAsString(CURRENT_BRANCH) + ".";
        Commit mergeCommit = new Commit(message, readHEAD());
        commitHelper(mergeCommit);
        mergeCommit.addParent(branchCommit.getId());
        mergeCommit.saveToFile();
        currentCommit = mergeCommit;
        setHEAD();
        setBranch();
        clearStage();
    }

    /**
     * Change the CWD.
     * @param path the new path: .../xxx/yyy/zzz/dir
     */
    private static void changeCurrentWorkDirectory(String path) {
        CWD = new File(path);
        GITLET_DIR = join(CWD, ".gitlet");
        OBJECT_DIR = join(GITLET_DIR, "objects");
        COMMIT_DIR = join(OBJECT_DIR, "commits");
        BLOB_DIR = join(OBJECT_DIR, "blobs");
        BRANCH_DIR = join(GITLET_DIR, "branches");
        STAGE_DIR = join(GITLET_DIR, "stages");
        REMOTE_DIR = join(GITLET_DIR, "remotes");
        HEAD = join(GITLET_DIR, "HEAD");
        CURRENT_BRANCH = join(GITLET_DIR, "BRANCH");
    }

    /**
     * The add-remote [name] [path]/.gitlet command.
     * @param remoteName the remote name
     * @param remotePath the remote path: .../xxx/yyy/zzz/.gitlet
     */
    public static void addRemote(String remoteName, String remotePath) {
        checkIfTheDirectoryExist();
        List<String> remotes = plainFilenamesIn(REMOTE_DIR);
        if (remotes != null && remotes.contains(remoteName)) {
            System.out.println("A remote with that name already exists.");
            System.exit(0);
        }
        String newWorkDirectory = remotePath.substring(0, remotePath.length() - 8);
        Remote remote = new Remote(remoteName, newWorkDirectory);
        remote.saveToFile();
    }

    /**
     * The rm-remote [name] command.
     * @param remoteName the remote name
     */
    public static void removeRemote(String remoteName) {
        checkIfTheDirectoryExist();
        List<String> remotes = plainFilenamesIn(REMOTE_DIR);
        if (remotes == null || !remotes.contains(remoteName)) {
            System.out.println("A remote with that name does not exist.");
            System.exit(0);
        }
        File file = join(REMOTE_DIR, remoteName);
        file.delete();
    }

    /**
     * The fetch [remoteName] [remoteBranchName] command.
     * @param remoteName the remote name
     * @param remoteBranchName the remote branch name
     */
    public static void fetch(String remoteName, String remoteBranchName) {
        checkIfTheDirectoryExist();
        checkFetch(remoteName, remoteBranchName);
        //注意此时工作目录已被更改为远程
        File branchFile = join(BRANCH_DIR, remoteBranchName);
        Branch remoteBranch = readObject(branchFile, Branch.class);
        String remoteBranchCommitId = remoteBranch.getCommitId();
        File commitFile = join(COMMIT_DIR, remoteBranch.getCommitId());
        //1: 拿到远程分支，用BFS获取分支历史存在Map里
        Map<String, Integer> branchHistory = getRouteToInit(readObject(commitFile, Commit.class));
        //2: 回到本地工作目录
        changeCurrentWorkDirectory(System.getProperty("user.dir"));
        //3: 对不存在的commit和blob进行复制
        File remoteFile = join(REMOTE_DIR, remoteName);
        Remote remote = readObject(remoteFile, Remote.class);
        String from = remote.getPath();
        String to = System.getProperty("user.dir");
        for (String commitId: branchHistory.keySet()) {
            copyCommit(commitId, to, from);
        }
        //4: (如果不存在就)创建新的本地远程跟踪分支
        changeCurrentWorkDirectory(to);
        String localBranch = remoteName + "/" + remoteBranchName;
        File localBranchFile = join(BRANCH_DIR, localBranch);
        File localBranches = join(BRANCH_DIR, remoteName);
        mkdir(localBranches);
        changeCurrentWorkDirectory(from);
        File remoteCommitFile = join(COMMIT_DIR, remoteBranchCommitId);
        Commit remoteCommit = readObject(remoteCommitFile, Commit.class);
        changeCurrentWorkDirectory(to);
        if (!localBranchFile.exists()) {
            Branch newBranch = new Branch(localBranch, remoteCommit);
            newBranch.saveToFile();
        } else {
            Branch branch = readObject(localBranchFile, Branch.class);
            branch.setCommitPointer(remoteCommit);
            branch.saveToFile();
        }
        //5: 设置该分支为当前目录的HEAD commit，这一点和git不一样
    }

    /**
     * Check if the fetch operation is legal and change the CWD.
     * @param remoteName the remote name
     * @param remoteBranchName the remote branch name
     */
    private static void checkFetch(String remoteName, String remoteBranchName) {
        checkRemoteExists(remoteName);
        List<String> branches = plainFilenamesIn(BRANCH_DIR);
        if (branches == null || !branches.contains(remoteBranchName)) {
            System.out.println("That remote does not have that branch.");
            System.exit(0);
        }
    }

    /**
     * Check the remote ./.gitlet exists.
     * @param remoteName the remote name
     */
    private static void checkRemoteExists(String remoteName) {
        File remoteFile = join(REMOTE_DIR, remoteName);
        if (!remoteFile.exists()) {
            System.out.println("Remote directory not found.");
            System.exit(0);
        }
        Remote remote = readObject(remoteFile, Remote.class);
        if (!remote.exists()) {
            System.out.println("Remote directory not found.");
            System.exit(0);
        }
        changeCurrentWorkDirectory(remote.getPath());
    }

    /**
     * Copy the special commit and its blobs from remote directory to local directory.
     * @param commitId the commit id
     * @param to the destination path
     * @param from the origin path
     */
    private static void copyCommit(String commitId, String to, String from) {
        changeCurrentWorkDirectory(to);
        File commitFile = join(COMMIT_DIR, commitId);
        if (commitFile.exists()) {
            return;
        }
        changeCurrentWorkDirectory(from);
        File remoteCommitFile = join(COMMIT_DIR, commitId);
        Commit remoteCommit = readObject(remoteCommitFile, Commit.class);
        byte[] content = readContents(remoteCommitFile);
        changeCurrentWorkDirectory(to);
        createNewFile(commitFile);
        writeContents(commitFile, content);
        Commit commit = readObject(commitFile, Commit.class);
        //更改内部路径
        commit.changePath();
        commit.saveToFile();
        for (String blobId: remoteCommit.getPathToBlobs().values()) {
            copyBlob(blobId, to, from);
        }
    }

    /**
     * Copy the special blob.
     * @param blobId the blob id
     * @param to the destination path
     * @param from the origin path
     */
    private static void copyBlob(String blobId, String to, String from) {
        changeCurrentWorkDirectory(to);
        File blobFile = join(BLOB_DIR, blobId);
        if (blobFile.exists()) {
            return;
        }
        changeCurrentWorkDirectory(from);
        File remoteBlobFile = join(BLOB_DIR, blobId);
        byte[] content = readContents(remoteBlobFile);
        changeCurrentWorkDirectory(to);
        createNewFile(blobFile);
        writeContents(blobFile, content);
    }

    /**
     * The push [remoteName] [remoteBranchName] command.
     * @param remoteName the remote name
     * @param remoteBranchName the remote branch name
     */
    public static void push(String remoteName, String remoteBranchName) {
        checkIfTheDirectoryExist();
        //1: 检查另一边的目录是否存在
        checkRemoteExists(remoteName);
        changeCurrentWorkDirectory(System.getProperty("user.dir"));
        //2: 用BFS获取本地头部历史
        Commit headCommit = readHEAD();
        Map<String, Integer> localHistory = getRouteToInit(headCommit);
        //3: 检查远程分支是否存在，若是，获取那边的头部，检查是否在本地历史中，进入第五步
        File remoteFile = join(REMOTE_DIR, remoteName);
        Remote remote = readObject(remoteFile, Remote.class);
        changeCurrentWorkDirectory(remote.getPath());
        List<String> branches = plainFilenamesIn(BRANCH_DIR);
        assert branches != null;
        if (branches.contains(remoteBranchName)) {
            File branchFile = join(BRANCH_DIR, remoteBranchName);
            Branch branch = readObject(branchFile, Branch.class);
            if (!localHistory.containsKey(branch.getCommitId())) {
                System.out.println("Please pull down remote changes before pushing.");
                System.exit(0);
            }
        }
        //4: 若否，直接进入第五步
        //5: 向远程仓库复制commit和blob
        String from = System.getProperty("user.dir");
        String to = remote.getPath();
        for (String commitId: localHistory.keySet()) {
            copyCommit(commitId, to, from);
        }
        //6: 设置远程分支，记得改CWD
        changeCurrentWorkDirectory(remote.getPath());
        if (branches.contains(remoteBranchName)) {
            File branchFile = join(BRANCH_DIR, remoteBranchName);
            Branch branch = readObject(branchFile, Branch.class);
            branch.setCommitPointer(headCommit);
            branch.saveToFile();
            String currentBranch = readContentsAsString(CURRENT_BRANCH);
            if (currentBranch.equals(remoteBranchName)) {
                currentCommit = headCommit;
                setHEAD();
            }
        } else {
            Branch branch = new Branch(remoteBranchName, headCommit);
            branch.saveToFile();
        }
    }

    /**
     * The pull [branchName] [remoteBranchName] command.
     * @param remoteName the remote name
     * @param remoteBranchName the remote branch name
     */
    public static void pull(String remoteName, String remoteBranchName) {
        fetch(remoteName, remoteBranchName);
        String branchName = remoteName + "/" + remoteBranchName;
        merge(branchName);
    }
}
