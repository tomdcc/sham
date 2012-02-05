package org.shamdata;

import org.shamdata.person.Person;
import org.shamdata.person.PersonGenerator;
import org.shamdata.text.MarkovGenerator;
import org.shamdata.text.SpewGenerator;
import org.shamdata.image.FileSystemImagePicker;
import org.shamdata.image.ImagePicker;
import org.shamdata.image.ServletContextImagePicker;

import javax.servlet.ServletContext;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 *
 */
public class Sham {
    private static Sham instance = null;
    private Random random;
    // config
    private String imageBaseDir;

    // actal generators
    private PersonGenerator personGenerator;
    private Map<String,ImagePicker> imagePickers;
    private MarkovGenerator textGenerator;
    private SpewGenerator headlineGenerator;
    private Object servletContext; // object so we don't have a runtime dependency on servlet api when it's not used

    public Sham() {
        random = new Random();
    }

    public static void setInstance(Sham instance) {
        Sham.instance = instance;
    }

    public static Sham getInstance() {
        if(Sham.instance == null) {
            Sham.instance = new Sham();
        }
        return Sham.instance;
    }

    private void initPersonGenerator() {
        if(personGenerator == null) {
            personGenerator = new PersonGenerator();
            personGenerator.setRandom(random);
        }
    }
    public Person nextPerson() {
        initPersonGenerator();
        return personGenerator.nextPerson();
    }


    private ImagePicker getImagePicker(String relativeDir) {
        if(imagePickers == null) {
            imagePickers = new HashMap<String, ImagePicker>();
        }
        ImagePicker picker = imagePickers.get(relativeDir);
        if(picker == null) {
			if(servletContext != null) {
				ServletContextImagePicker scip = new ServletContextImagePicker();
				scip.setServletContext((ServletContext) servletContext);
				picker = scip;
			} else {
				picker = new FileSystemImagePicker();
			}

            picker.setBaseDir(imageBaseDir + "/" + relativeDir);
            picker.setRandom(random);

            picker.init();
        }
        return picker;
    }

    public String nextSentence() {
        initTextGenerator();
        return textGenerator.nextSentence();
    }

    public String nextParagraph() {
        initTextGenerator();
        return textGenerator.nextParagraph();
    }

    public String nextParagraph(int numSentences) {
        initTextGenerator();
        return textGenerator.nextParagraph(numSentences);
    }

    public List<String> nextParagraphs() {
        initTextGenerator();
        return textGenerator.nextParagraphs();
    }

    public List<String> nextParagraphs(int num) {
        initTextGenerator();
        return textGenerator.nextParagraphs(num);
    }


    private void initTextGenerator() {
        if(textGenerator == null) {
            textGenerator = new MarkovGenerator();
            textGenerator.setRandom(random);
            textGenerator.init();
        }
    }

    private void initHeadlineGenerator() {
        if(headlineGenerator == null) {
			headlineGenerator = new SpewGenerator();
            headlineGenerator.setRandom(random);
			headlineGenerator.setBundleName("headline");
            headlineGenerator.init();
        }
    }
	
	public String nextHeadline() {
		initHeadlineGenerator();
		return headlineGenerator.nextLine();
	}

    public URL nextImage(String relativeDir) {
        return getImagePicker(relativeDir).nextImage();
    }

    public Map<String,URL> nextImageSet(String relativeDir) {
        return getImagePicker(relativeDir).nextImageSet();
    }

    public void setImageBaseDir(String imageBaseDir) {
        this.imageBaseDir = imageBaseDir;
    }
}
