package org.shamdata.image;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

/**
 * An image picker that scans a file system directory.
 */
public class FileSystemImagePicker extends BaseImagePicker {

    public void init() {
        if(baseDir == null) {
            baseDir = ".";
        } else {
            File baseDirFile = new File(baseDir);
            if(!baseDirFile.exists()) {
                throw new IllegalArgumentException("Base dir " + baseDir + " does not exist");
            } else if(!baseDirFile.isDirectory()) {
                throw new IllegalArgumentException("File " + baseDir + " is not a directory");
            }
        }
        super.init();
    }

    @Override
    protected Set<String> listFiles(String dirName) {
        Set<String> paths = new LinkedHashSet<String>();
        File[] files = new File(dirName).listFiles();
        for(File file : files) {
            String path = file.toString();
            paths.add(file.isDirectory() ? path + '/' : path);
        }
        return paths;
    }

    /**
     * Returns a file URL pointing to the given file
     *
     * @param filename path to the file
     * @return a file URL poiting to the file
     */
    @Override
    protected URL toURL(String filename) {
        try {
            return new File(filename).toURI().toURL();
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException(e);
        }
    }

}
