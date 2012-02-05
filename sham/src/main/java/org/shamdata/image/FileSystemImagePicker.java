package org.shamdata.image;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

public class FileSystemImagePicker implements ImagePicker {
    private Random random;
    private File baseDir;
    List<URL> files;
    List<File> dirs;

    public URL nextImage() {
        System.out.println(files);
        return files.get(random.nextInt(files.size()));
    }

    public Map<String, URL> nextImageSet() {
        return buildImageSet(dirs.get(random.nextInt(dirs.size())));
    }

    private Map<String, URL> buildImageSet(File dir) {
        Map<String, URL> imageSet = new HashMap<String, URL>();
        for(File file : dir.listFiles()) {
            if(file.isFile()) {
                imageSet.put(getFilenameBase(file), toURL(file));
            }
        }
        return imageSet;
    }

    private String getFilenameBase(File file) {
        String filename = file.getName();
        int dotPlace = filename.lastIndexOf('.');
        if(dotPlace != -1) {
            return filename.substring(0, dotPlace);
        } else {
            return filename;
        }
    }

    public void init() {
        System.out.println("FSIP.init");
        if(random == null) {
            random = new Random();
        }
        if(baseDir == null) {
            baseDir = new File(".");
        } else if(!baseDir.exists()) {
            throw new IllegalArgumentException("Base dir " + baseDir + " does not exist");
        } else if(!baseDir.isDirectory()) {
            throw new IllegalArgumentException("File " + baseDir + " is not a directory");
        }

        files = new ArrayList<URL>();
        dirs = new ArrayList<File>();
        System.out.println(baseDir);
        for(File file : baseDir.listFiles()) {
            if(file.isDirectory()) {
                dirs.add(file);
            } else {
                files.add(toURL(file));
            }
        }
    }

    private URL toURL(File file) {
        try {
            return file.toURI().toURL();
        } catch(MalformedURLException r ) {
            throw new IllegalArgumentException(r);
        }
    }

    public void setRandom(Random random) {
        this.random = random;
    }

    public void setBaseDir(String baseDir) {
        this.baseDir = new File(baseDir);
    }
}
