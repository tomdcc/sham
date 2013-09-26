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
 * Sham helper class. Keeps a reference to a single shared random number generator
 * and maintains a registry of data generators which share that RNG. New generators
 * can be registered with and fetched from the <code>Sham</code> instance. The
 * <code>Sham</code> class also has a number of built-in convenience accessors to
 * commonly used generators.
 *
 * <p>Usage: The <code>Sham</code> instance can be instantiated like any normal Java bean,
 * or an application can get a handle to a shared static instance by calling
 * {@link #getInstance()  Sham.getInstance()}.</p>
 *
 * <p>Once you have an instance, it is possible to call one of the <code>nextXXX()</code>
 * methods to get some data. Or new generators may be registered using the
 * {@link #registerGenerator registerGenerator()} method, and subsequently called by fetching them from
 * the <code>Sham</code> instance by calling {@link #getGenerator(String) getGenerator()}.</p>
 */
public class Sham {

    /**
     * System property which can be used to initialize the Sham RNG's internal seed.
     * Property name is <strong><code>sham.seed</code></strong> .
     */
	public static final String SHAM_SEED_SYSTEM_PROPERTY_KEY = "sham.seed";

    private static Sham instance = null;
    private Random random;
    // config
    private String imageBaseDir;

    // actual generators
    private Map<String,ShamGenerator> generators;

    // this is declared object so we don't have a runtime dependency on servlet api when it's not used
    private Object servletContext;

    /**
     * Initialise new Sham instance. If the System property <strong><code>sham.seed</code></strong>
     * is set, the internal random number generator will be initialized with this value. Otherwise
     * the RNG will be initialized with the default for {@link java.util.Random java.util.Random}.
     */
    public Sham() {
        random = new Random();
		String seedSysProp = System.getProperty(SHAM_SEED_SYSTEM_PROPERTY_KEY);
		if(seedSysProp != null) {
			setSeed(Long.parseLong(seedSysProp));
		}
        generators = new LinkedHashMap<String,ShamGenerator>();
    }

    /**
     * Sets the static shared instance to the one provided.
     * <p>One example usage of this may be to initialize a <code>Sham</code> instance
     * using a dependency injection container such as Spring, but then register
     * the created bean statically so that code which does not get dependency injected
     * can still call {@link #getInstance Sham.getInstance()} to get a reference to the same copy.</p>
     *
     * @param instance the instance to save for easy access
     */
    public static void setInstance(Sham instance) {
        Sham.instance = instance;
    }

    /**
     * Returns the global <code>Sham</code> instance. If one has not been explicitly set,
     * a new instance is created and saved first, then returned.
     *
     * @return a previously saved static <code>Sham</code> instance, or a new one if not requested before
     */
    public static Sham getInstance() {
        if(Sham.instance == null) {
            Sham.instance = new Sham();
        }
        return Sham.instance;
    }

    /**
     * Register a data generator. The passed in generator will have its random number generator
     * set to the same RNG as the <code>Sham</code> instance.
     *
     * @param name the name to give to the generator, so that it may be recalled when calling
     *        <code>getGenerator</code> or <code>getGenerators</code>.
     * @param generator the generator to register
     */
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

    /**
     * Generates a new random {@link Person} object. This will initialise a new
     * default {@link PersonGenerator} in the <code>Sham</code> instance if one has not
     * previously been registered.
     *
     * @return new {@link Person}
     * @see org.shamdata.person.PersonGenerator#nextPerson()
     */
    public Person nextPerson() {
        return getPersonGenerator().nextPerson();
    }


    private ImagePicker getImagePicker(String relativeDir) {
        String genKey = "image/" + relativeDir;
        ImagePicker picker = (ImagePicker) generators.get(genKey);
        if(picker == null) {
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

    /**
     * Generates a random sentence. If a text generator has not previously been registered,
     * this will initialize a new default {@link MarkovGenerator} to generate the text.
     *
     * @return a random sentence of text.
     * @see org.shamdata.text.MarkovGenerator#nextSentence()
     */
    public String nextSentence() {
        return getTextGenerator().nextSentence();
    }

    /**
     * Generates a random sentence up to the requested number of characters.
     * If a text generator has not previously been registered, this will initialize a new
     * default {@link MarkovGenerator} to generate the text.
     *
     * @param maxChars the maximum number of characters to return
     * @return a random sentence of text with length no greater than the specified number of characters
     * @see org.shamdata.text.MarkovGenerator#nextSentence(int)
     */
    public String nextSentence(int maxChars) {
        return getTextGenerator().nextSentence(maxChars);
    }

    /**
     * Generates a random paragraph with a random number of sentences.
     * If a text generator has not previously been registered, this will initialize a new
     * default {@link MarkovGenerator} to generate the text.
     *
     * @return a random paragraph of text
     * @see org.shamdata.text.MarkovGenerator#nextParagraph()
     */
    public String nextParagraph() {
        return getTextGenerator().nextParagraph();
    }

    /**
     * Generates a random paragraph with a specific number of sentences.
     * If a text generator has not previously been registered, this will initialize a new
     * default {@link MarkovGenerator} to generate the text.
     *
     * @param numSentences the number of sentences in the paragraph
     * @return a random paragraph of text with length with the specified number of sentences.
     * @see org.shamdata.text.MarkovGenerator#nextParagraph(int)
     */
    public String nextParagraph(int numSentences) {
        return getTextGenerator().nextParagraph(numSentences);
    }

    /**
     * Generates a list of random paragraphs of random size.
     * If a text generator has not previously been registered, this will initialize a new
     * default {@link MarkovGenerator} to generate the text.
     *
     * @return a list of random paragraphs of text, or random size
     * @see org.shamdata.text.MarkovGenerator#nextParagraphs()
     */
    public List<String> nextParagraphs() {
        return getTextGenerator().nextParagraphs();
    }

    /**
     * Generates a list of random paragraphs with a specific number of paragraphs.
     * If a text generator has not previously been registered, this will initialize a new
     * default {@link MarkovGenerator} to generate the text.
     *
     * @param num the number of paragraphs to return
     * @return a list of <code>num</code> random paragraphs of text.
     * @see org.shamdata.text.MarkovGenerator#nextParagraphs(int)
     */
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

    /**
     * Generates a random National Inquirer style news headline. If a headline generator has
     * not previously been registered, this will initialize a new default {@link SpewGenerator}
     * to generate the text.
     *
     * @return a random headline
     * @see org.shamdata.text.SpewGenerator#nextLine()
     */
	public String nextHeadline() {
		return getSpewGenerator("headline", "headline").nextLine();
	}

    /**
     * Generates a random product name. If a product name generator has
     * not previously been registered, this will initialize a new default {@link SpewGenerator}
     * to generate the text.
     *
     * @return a random headline
     * @see org.shamdata.text.SpewGenerator#nextLine()
     */
	public String nextProductName() {
		return getSpewGenerator("productName", "product-name").nextLine();
	}

    /**
     * Fetches a random image from a directory. If an image picker has
     * not previously been registered for the given directory, this will initialize
     * a new default {@link ImagePicker} for that dir.
     *
     * @param relativeDir the dir relative to Sham's baseImageDir
     * @return a random image from the given directory
     * @see #setImageBaseDir(String)
     * @see org.shamdata.image.ImagePicker#nextImage()
     */
    public URL nextImage(String relativeDir) {
        return getImagePicker(relativeDir).nextImage();
    }

    /**
     * Fetches a random image set from a directory. An image set should be a directory
     * with a number of images inside. The returned image set is a {@link Map}
     * with the image file names, minus file extension, as the keys, with a {@link URL} to
     * the actual image as the value.
     *
     * If an image picker has
     * not previously been registered for the given directory, this will initialize
     * a new default {@link ImagePicker} for that dir.
     *
     * @param relativeDir the dir relative to Sham's baseImageDir
     * @return a random image set from the given directory
     * @see #setImageBaseDir(String)
     * @see org.shamdata.image.ImagePicker#nextImageSet()
     */
    public Map<String,URL> nextImageSet(String relativeDir) {
        return getImagePicker(relativeDir).nextImageSet();
    }

    /**
     * Tells Sham where to look for images. This can be an absolute or relative path name,
     * or a path relative to a servlet context when executing inside a servlet container.
     * To have the path treated as relative to a servlet context root, set the servlet context
     * by calling {@link #setServletContext(Object) setServletContext()} <em>before</em>
     * trying to fetch any random images or image sets.
     *
     * @param imageBaseDir the absolute or relative file system path, or the servlet context relative path
     *        to images
     */
    public void setImageBaseDir(String imageBaseDir) {
        this.imageBaseDir = imageBaseDir;
    }

    /**
     * Sets the random seem value for Sham's internal random number generator. Since
     * {@link java.util.Random java.util.Random} does not allow us to set this directly
     * (the {@link Random#setSeed(long) Random.setSeed()} and {@link Random#Random(long) new Random(seed)} calls don't set it directly,
     * the seed gets mutated first), we use reflection to set it. This has been tested in Sun / Oracle JRE
     * 1.6 - other implementations may not work yet. Also may not work if you're running inside a
     * {@link SecurityManager}.
     *
     * @param seedVal the value to set the seed to
     */
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

    /**
     * Returns the currend seed value in Sham's internal random number generator. Since
     * {@link java.util.Random java.util.Random} does not expose this directly, we use reflection
     * to get it. This has been tested in Sun / Oracle JRE 1.6 - other implementations may not work
     * yet. Also may not work if you're running inside a {@link SecurityManager}.
     *
     * @return the current value of the RNG's internal seed
     */
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

    /**
     * Returns the internal random number generator. Mostly useful for calling e.g.
     * {@link Random#nextInt} etc.
     *
     * @return the RNG
     */
    public Random getRandom() {
        return random;
    }

    /**
     * Returns the named data generator.
     *
     * @param name the name of the generator to return
     * @return the specified generator, or null if no such generator is registered
     */
    public ShamGenerator getGenerator(String name) {
        return generators.get(name);
    }

    /**
     * Returns the current set of regstered generators as a {@link Map}.
     *
     * @return the current set of registered generators, mapped by name
     */
    public Map<String,ShamGenerator> getGenerators() {
        return Collections.unmodifiableMap(generators);
    }


    /**
     * Gives Sham a handle on the current servlet context. This is mostly to allow
     * Sham to look up random images in a path relative to the servlet context's
     * root, rather than on the file system directly. This should be set <em>before</em>
     * any calls to {@link #nextImage nextImage()} or {@link #nextImageSet nextImageSet()}.
     *
     * @param servletContext the servlet context
     */
    public void setServletContext(Object servletContext) {
        this.servletContext = servletContext;
    }
}
