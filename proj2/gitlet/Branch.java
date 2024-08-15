package gitlet;

import java.io.File;
import java.io.Serializable;
import static gitlet.Utils.*;
import static gitlet.Repository.getBranchDir;

public class Branch implements Serializable {
    private String commitPointer;
    private final String branchName;

    public Branch(Commit commit) {
        branchName = "master";
        commitPointer = commit.getId();
    }

    public Branch(String name, Commit commit) {
        branchName = name;
        commitPointer = commit.getId();
    }

    public void saveToFile() {
        File branch = join(getBranchDir(), branchName);
        createNewFile(branch);
        writeObject(branch, this);
    }

    public void setCommitPointer(Commit commit) {
        commitPointer = commit.getId();
    }

    public String getCommitId() {
        return commitPointer;
    }
}
