package org.shamdata.image;

import org.shamdata.ShamGenerator;

import java.net.URL;
import java.util.Map;

public interface ImagePicker extends ShamGenerator {

    URL nextImage();
    Map<String,URL> nextImageSet();

    void setBaseDir(String baseDir);
    void init();
}
