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
import java.util.*;
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

    // actual generators
    Map<String,ShamGenerator> generators;


    private Object servletContext; // object so we don't have a runtime dependency on servlet api when it's not used

    public Sham() {
        random = new Random();
		String seedSysProp = System.getProperty(SHAM_SEED_SYSTEM_PROPERTY_KEY);
		if(seedSysProp != null) {
			setSeed(Long.parseLong(seedSysProp));
		}
        generators = new LinkedHashMap<String,ShamGenerator>();
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

    public void registerGenerator(String name, ShamGenerator generator) {
        generator.setRandom(random);
        generators.put(name, generator);
    }

    private PersonGenerator getPersonGenerator() {
        PersonGenerator personGenerator = (PersonGenerator) generators.get("person");
        if(personGenerator == null) {
            personGenerator = new PersonGenerator();
            registerGenerator("person", personGenerator);
        }
        return personGenerator;
    }

    public Person nextPerson() {
        return getPersonGenerator().nextPerson();
    }


    private ImagePicker getImagePicker(String relativeDir) {
        String genKey = "image/" + relativeDir;
        ImagePicker picker = (ImagePicker) generators.get(genKey);
        if(picker == null) {
            // TODO - does this actually protect us from the runtime dep on ServletContext? I doubt it...
			if(servletContext != null) {
				ServletContextImagePicker scip = new ServletContextImagePicker();
				scip.setServletContext((ServletContext) servletContext);
				picker = scip;
			} else {
				picker = new FileSystemImagePicker();
			}
            registerGenerator(genKey, picker);
            picker.setBaseDir(imageBaseDir + "/" + relativeDir);
            picker.init();
        }
        return picker;
    }

    public String nextSentence() {
        return getTextGenerator().nextSentence();
    }

    public String nextSentence(int maxChars) {
        return getTextGenerator().nextSentence(maxChars);
    }

    public String nextParagraph() {
        return getTextGenerator().nextParagraph();
    }

    public String nextParagraph(int numSentences) {
        return getTextGenerator().nextParagraph(numSentences);
    }

    public List<String> nextParagraphs() {
        return getTextGenerator().nextParagraphs();
    }

    public List<String> nextParagraphs(int num) {
        return getTextGenerator().nextParagraphs(num);
    }


    private MarkovGenerator getTextGenerator() {
        MarkovGenerator textGenerator = (MarkovGenerator) generators.get("text");
        if(textGenerator == null) {
            textGenerator = new MarkovGenerator();
            registerGenerator("text", textGenerator);
            textGenerator.init();
        }
        return textGenerator;
    }

    private SpewGenerator getSpewGenerator(String name, String bundleName) {
        SpewGenerator generator = (SpewGenerator) generators.get(name);
        if(generator == null) {
			generator = new SpewGenerator();
			generator.setBundleName(bundleName);
            registerGenerator(name, generator);
            generator.init();
        }
        return generator;
    }

	public String nextHeadline() {
		return getSpewGenerator("headline", "headline").nextLine();
	}

	public String nextProductName() {
		return getSpewGenerator("productName", "product-name").nextLine();
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

    public Random getRandom() {
        return random;
    }

    public ShamGenerator getGenerator(String name) {
        return generators.get(name);
    }

    public Map<String,ShamGenerator> getGenerators() {
        return Collections.unmodifiableMap(generators);
    }

}
