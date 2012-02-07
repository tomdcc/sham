package org.shamdata;

import org.shamdata.person.Person;
import org.shamdata.person.PersonGenerator;
import org.shamdata.text.MarkovGenerator;
import org.shamdata.text.SpewGenerator;
import org.shamdata.image.FileSystemImagePicker;
import org.shamdata.image.ImagePicker;
import org.shamdata.image.ServletContextImagePicker;

import javax.servlet.ServletContext;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

/**
 *
 */
public class Sham {
	
	public static final String SHAM_SEED_SYSTEM_PROPERTY_KEY = "sham.seed";
	
    private static Sham instance = null;
    private Random random;
    // config
    private String imageBaseDir;

    // actal generators
    private PersonGenerator personGenerator;
    private Map<String,ImagePicker> imagePickers;
    private MarkovGenerator textGenerator;
    private SpewGenerator headlineGenerator;
    private SpewGenerator productNameGenerator;
    private Object servletContext; // object so we don't have a runtime dependency on servlet api when it's not used

    public Sham() {
        random = new Random();
		String seedSysProp = System.getProperty(SHAM_SEED_SYSTEM_PROPERTY_KEY);
		if(seedSysProp != null) {
			setSeed(Long.parseLong(seedSysProp));
		}
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

    public String nextSentence(int maxChars) {
        initTextGenerator();
        return textGenerator.nextSentence(maxChars);
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

    private void initProductNameGenerator() {
        if(productNameGenerator == null) {
			productNameGenerator = new SpewGenerator();
			productNameGenerator.setRandom(random);
			productNameGenerator.setBundleName("product-name");
			productNameGenerator.init();
        }
    }

	public String nextProductName() {
		initProductNameGenerator();
		return productNameGenerator.nextLine();
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

	public void setSeed(long seedVal) {
		try {
			Field f = Random.class.getDeclaredField("seed"); //NoSuchFieldException
			f.setAccessible(true);
			AtomicLong seed = (AtomicLong) f.get(random);
			seed.set(seedVal);
		} catch(NoSuchFieldException e) {
			throw new RuntimeException("Couldn't access seed field - perhaps JDK Random object not laid out as expected?", e);
		} catch(IllegalAccessException e) {
			throw new RuntimeException("Couldn't access seed field - are you running under a SecurityManager?", e);
		} catch(SecurityException e) {
			throw new RuntimeException("Couldn't access seed field - are you running under a SecurityManager?", e);
		}
	}

	public Long getSeed() {
		try {
			Field f = Random.class.getDeclaredField("seed"); //NoSuchFieldException
			f.setAccessible(true);
			AtomicLong seed = (AtomicLong) f.get(random);
			return seed == null ? null : seed.get();
		} catch(Exception e) {
			// not allowed to access
			return null;
		}
	}
}
