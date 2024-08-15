package gitlet;

import java.io.File;
import java.io.Serializable;
import static gitlet.Utils.*;
import static gitlet.Repository.getRemoteDir;

public class Remote implements Serializable {
    private final String remoteName;
    private final String directory;

    public Remote(String name, String path) {
        this.remoteName = name;
        this.directory = path;
    }

    public String getName() {
        return remoteName;
    }

    public String getPath() {
        return directory;
    }

    public void saveToFile() {
        File file = join(getRemoteDir(), remoteName);
        createNewFile(file);
        writeObject(file, this);
    }

    public Boolean exists() {
        File file = join(directory, ".gitlet");
        return file.exists();
    }
}
