package gitlet;

import java.io.File;
import java.io.Serializable;
import static gitlet.Utils.*;
import static gitlet.Repository.BRANCH_DIR;

public class Branch implements Serializable {
    private String commitPointer;
    private String branchName;

    public Branch(String id) {
        branchName = "master";
        commitPointer = id;
    }

    public Branch(String name, String id) {
        branchName = name;
        commitPointer = id;
    }

    public void saveToFile() {
        File branch = join(BRANCH_DIR, branchName);
        createNewFile(branch);
        writeObject(branch, this);
    }
}
