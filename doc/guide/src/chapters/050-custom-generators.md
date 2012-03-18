# Custom Generators

## Registering generators with Sham

Any custom generators, whether using built-in Sham generator classes or your
own classes, should be registered with the `registerGenerator()` method. This
ensures that:

1. The generator uses the same random number geerator that the rest of sham
   is using, which is important for reproducability of the randomly generated
   data. See the next chapter for more details.
2. There is a single place that references to all of your generators are kept.
   This is particularly useful if you use `Sham.getInstance()` to access Sham
   from a fixture - there's no need to pass your custom generator through or
   keep it stored somewhere, it's already available from the `Sham` instance.


## Custom instances of built-in generators

You can register your own instances of built-in Sham data generators with Sham.
An example might be to create a new image picker in a different base directory:

    Sham sham = Sham.getInstance();
    URL image = sham.getImage("faces"); // picks image from faces subdirectory of default image base dir

    // now to create new image picker
    FileSystemImagePicker picker = new FileSystemImagePicker();
    picker.setBaseDir("/tmp/production-images/products");
    sham.registerGenerator("productImages", picker);

    // can now call directly
    URL productImage = picker.nextImage();

    // or can fetch from sham instance at a later date
    ImagePicker productImagePicker = (ImagePicker) Sham.getInstance().getGenerator("productImages");
    URL productImage2 = productImagePicker.nextImage();

You can see which generators Sham currently has registered by calling
`getGenerators()`:

    Sham sham = new Sham();
    sham.setImageBaseDir("images");
    URL femaleFace = sham.nextImage("faces/female");
    Person person = sham.nextPerson();
    String tagline = sham.nextSentence();
    System.out.println(sham.getGenerators());

gives:

    [image/faces/female:org.shamdata.image.FileSystemImagePicker@20985fa2, text:org.shamdata.text.MarkovGenerator@73ae9565, person:org.shamdata.person.PersonGenerator@4ad25538]


## Custom generator classes

You can plug your own data generators into Sham. Custom classes should
implement the `ShamGenerator` interface, which simply requires that
a `setRandom` method is called. Your class should use the provided
random number generator, so that it uses the same random number generator
that the rest of Sham's data generators are using. This is set by
registering your generator with Sham:

    public class AToZGenerator implements ShamGenerator {
        private Random random;
        public void setRandom(Random random) {
            this.random = random;
        }
        public char nextLetter() {
            return (char) ('A' + random.nextInt(26));
        }
    }

and:

     Sham.getInstance().registerGenerator("letter", new AToZGenerator());

The generator can now be used anywhere that you are using Sham:

    AToZGenerator letterGen = (AToZGenerator) Sham.getInstance().getGenerator("letter");
    System.out.println(letterGen.nextLetter());
