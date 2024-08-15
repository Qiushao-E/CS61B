package gitlet;

import java.io.File;
import java.io.Serializable;
import static gitlet.Utils.*;
import static gitlet.Repository.getBlobDir;

public class Blob implements Serializable {
    private String id;
    private byte[] bytes;
    private File file;
    private String filePath;
    private String blobFilePath;
    private File blobFile;

    public Blob(File file) {
        this.file = file;
        this.filePath = file.getPath();
        this.bytes = readContents(file);
        this.id = generateId();
        this.blobFile = join(getBlobDir(), id);
        this.blobFilePath = blobFile.getPath();
    }

    private String generateId() {
        return sha1(bytes, file.getName());
    }

    public String getId() {
        return id;
    }

    public String getFilePath() {
        return filePath;
    }

    public void saveToFile() {
        createNewFile(blobFile);
        writeObject(blobFile, this);
    }

    public byte[] getBytes() {
        return bytes;
    }
}
