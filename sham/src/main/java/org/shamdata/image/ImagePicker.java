package org.shamdata.image;

import java.net.URL;
import java.util.Map;
import java.util.Random;

public interface ImagePicker {

    URL nextImage();
    Map<String,URL> nextImageSet();

    void setRandom(Random random);
    void setBaseDir(String baseDir);
    void init();
}
