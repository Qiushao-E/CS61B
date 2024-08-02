package gitlet;

import java.io.File;
import java.io.Serializable;

public class Blob implements Serializable {
    private String id;
    private byte[] bytes;
    private File fileName;
    private String filePath;
}
