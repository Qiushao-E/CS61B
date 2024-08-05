package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.Map;
import java.util.TreeMap;
import static gitlet.Utils.*;
import static gitlet.Repository.STAGE_DIR;

public class Stage implements Serializable {
    private Map<String, String> pathToBlobs;
    private final String stageName;

    public Stage(String name) {
        pathToBlobs = new TreeMap<>();
        stageName = name;
    }

    public void addBlob(Blob blob) {
        String filePath = blob.getFilePath();
        String blobId = blob.getId();
        pathToBlobs.put(filePath, blobId);
    }

    public void addBlob(String filePath, String blobId) {
        pathToBlobs.put(filePath, blobId);
    }

    public void removeBlob(String filePath) {
        pathToBlobs.remove(filePath);
    }

    public void saveToFile() {
        File file = join(STAGE_DIR, stageName);
        createNewFile(file);
        writeObject(file, this);
    }

    public Map<String, String> getPathToBlobs() {
        return pathToBlobs;
    }

    public boolean isEmpty() {
        return pathToBlobs.isEmpty();
    }

    public void clear() {
        pathToBlobs.clear();
        saveToFile();
    }
}
