package org.shamdata.image;


import java.net.URL;
import java.util.*;

public abstract class BaseImagePicker implements ImagePicker {

    public static final Collection<String> DEFAULT_FILE_IGNORE_PATTERNS = Collections.singleton("^\\..*");

    private Random random;
    protected String baseDir;

    private List<URL> files;
    private List<String> dirs;
    private Collection<String> filenameIgnorePatterns = DEFAULT_FILE_IGNORE_PATTERNS;

    public URL nextImage() {
        return files.get(random.nextInt(files.size()));
    }

    public Map<String, URL> nextImageSet() {
        return buildImageSet(dirs.get(random.nextInt(dirs.size())));
    }

    private Map<String, URL> buildImageSet(String dirName) {
        Map<String, URL> imageSet = new HashMap<String, URL>();
        for(String path: filterFilenames(listFiles(dirName))) {
            imageSet.put(getFilenameBase(path), toURL(path));
        }
        return imageSet;
    }

    private String getFilenameBase(String path) {
        String filename = path.substring(path.lastIndexOf('/') + 1);
        int dotPlace = filename.lastIndexOf('.');
        if(dotPlace != -1) {
            return filename.substring(0, dotPlace);
        } else {
            return filename;
        }
    }


    public void init() {
        if(random == null) {
            random = new Random();
        }
        scanFiles();
    }

    abstract Set<String> listFiles(String dirName);

    private Set<String> filterFilenames(Set<String> filePaths) {
        Set<String> filtered = new LinkedHashSet<String>();
        if(filePaths != null) {
            filenameLoop:
            for(String path : filePaths) {
                String strippedPath = path.endsWith("/") ? path.substring(0, path.length() - 1) : path;
                String filename = strippedPath.substring(strippedPath.lastIndexOf('/') + 1);
                for(String pattern : filenameIgnorePatterns) {
                    if(filename.matches(pattern)) {
                        continue filenameLoop;
                    }
                }
                filtered.add(path);
            }
        }
        return filtered;
    }

    private void scanFiles() {
        files = new ArrayList<URL>();
        dirs = new ArrayList<String>();
        Collection<String> fileNames = new LinkedHashSet<String>(filterFilenames(listFiles(baseDir)));
        fileNames.remove(baseDir.endsWith("/") ? baseDir : baseDir + "/");
        for(String filename : fileNames) {
            if(filename.endsWith("/")) {
                dirs.add(filename);
            } else {
                files.add(toURL(filename));
            }
        }
    }

    protected abstract URL toURL(String filename);

    public void setRandom(Random random) {
        this.random = random;
    }

    public void setBaseDir(String baseDir) {
        this.baseDir = baseDir;
    }

    public void setFilenameIgnorePatterns(Collection<String> patterns) {
        filenameIgnorePatterns = patterns;
    }
}
