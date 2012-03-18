# Built-In Generators

Sham comes with a number of built-in data generators which can fit into many
domain models in different ways.

## Person

Sham can generate a random person, including their name, gender, email address
and many other things. See below for an example. This code:

    Person person = new Sham().nextPerson();
    System.out.println("Full name: " + person.getFullName());
    System.out.println("Name: " + person.getName());
    System.out.println("First name: " + person.getFirstName());
    System.out.println("Last name: " + person.getLastName());
    System.out.println("Given names: " + person.getGivenNames());
    System.out.println();
    System.out.println("Gender: " + person.getGender());
    System.out.println("Date of birth: " + person.getDob());
    System.out.println();
    System.out.println("Email address: " + person.getEmail());
    System.out.println("Username: " + person.getUsername());
    System.out.println("Twitter username: " + person.getTwitterUsername());

generates the following output:

    Full name: Olivia Chantelle Walsh
    Name: Olivia Walsh
    First name: Olivia
    Last name: Walsh
    Given names: [Olivia, Chantelle]

    Gender: FEMALE
    Date of birth: Fri Jan 02 00:00:00 GMT 1987

    Email address: owalsh@hotmail.com.bv
    Username: oliviaw
    Twitter username: @oliviawalsh

## Text

Sham comes with a built in Markov text generator, which can generate random
sentences and paragraphs of text anywhere that you might need some -
body copy, comment content, strap lines etc.

Outputs a sentence of text:

    System.out.println(Sham.getInstance().nextSentence());

Outputs a sentence of text no longer than 30 characters long:

    System.out.println(Sham.getInstance().nextSentence(30));

Outputs a paragraph of text:

    System.out.println(Sham.getInstance().nextParagraph());

Outputs a paragraph of text with exactly 4 sentences:

    System.out.println(Sham.getInstance().nextParagraph(4));

Generates several paragraphs of text:

    List<String> paragraphs = Sham.getInstance().nextParagraphs();

Generates exactly 3 paragraphs of text:

    List<String> paragraphs = Sham.getInstance().nextParagraphs(3);


## Headline

Sham has a news headline generator based on Spew, an
[old unix utility](http://groups.google.com/group/alt.sources/browse_thread/thread/f96fb816ec77303e)
which can generate fake headlines from a file with headline styles and celebrity
names. The following code:

    Sham sham = new Sham();
    for(int i = 0; i < 10; i++) {
        System.out.println(sham.nextHeadline());
    }

gives the following output:

    Polish Scientists resurrect Joan of Arc.
     -- Exclusive Pictures Inside.

    Cher Shoots Thirty-Four Psychiatrists in drunken rampage.

    Wombat Sees Face Of Kermit the frog In an Enchilada.

    "Bob Dylan is the Father Of My Son", Says Neurotic French Student.

    "Space Beings From Jupiter Gave Me VD" Says Sean Penn.

    French Pattern Recognition Researchers Produce Spiritual Crocodile Girl.

    Real Life Ghost Busters Exorcise Demon from Prince's home in Orlando, Florida.

    "Cher Is Addicted to Angel Dust".
     -- Photographic Evidence Offers Proof.

    "Killer Dolphins From Neptune took My Son" Reveals Lou Albano.
     -- Exclusive Pictures Inside.

    "I Saw Jimi Hendrix Alive and Well in Transylvania" Reveals Japanese Dentist.

## Product Name

Sham has a product name generator based on a Sped definition file. See the
following output:

    Sham sham = new Sham();
    for(int i = 0; i < 10; i++) {
        System.out.println(sham.nextProductName());
    }

gives:

    WonderCo StealthEspresso Ultra Deluxe Edition
    SilentPad 6200 X
    General Foomatic MiniCushion 9900 Titanium Edition -- Limited time only!
    TinyScarfe X Bling Edition
    TransBlender
    FooSoft SuperPhone 0900 Ultra
    WonderCo SilentGoggles Titanium Edition
    EnergizedPod JE2400 Ultra -- Don't miss out!
    General Foomatic SuperBoots
    EnergizedCutter 8201 Platinum Edition

## Images and Files

Sham can scan a directory for images (or any file) and return a random one.
It can also return an *image set* - a map of image names to files, with each
image set corresponding to a subdirectory.

So for a directory tree looking like this:

    images/
        faces/
            face1.png
            face2.jpg
            face3.jpg
        products/
            blender/
                mainImage.png
                secondImage0.jpg
                secondImage1.jpg
            kettle/
                mainImage.png
                secondImage0.jpg

Sham should be initialised with the target image directory:

    Sham sham = new Sham();
    sham.setImageBaseDir("images");

Then calling `sham.nextImage("faces")` will return a URL to a random image in
the `images/faces` directory.

Calling `sham.nextImageSet("products")` will return a `java.util.Map` instance
representing one of the two subdirectories of `images/products`. The keys of
the map will be the filenames, minus expension, and the values are a URL
pointing to the file. So one return result would point to the
`images/products/kettle` directory and would have keys of `"mainImage"` and
`"secondImage0"` pointing to URLs to the `mainImage.png` and `secondImage0.jpg`
 files respectively.

### File System

Sham can scan the file system for images as shown above. By default, Sham
understands a single base image dir to scanfor images, but it can be told
about other directories by registering them directly:

    Sham sham = new Sham();
    sham.setImageBaseDir("images"); // set default image dir
    FileSystemImagePicker picker = new FileSystemImagePicker();
    picker.setBaseDir("/var/copy/of/production/product/images");
    sham.registerGenerator("image/products", picker);
    URL randomImage = sham.nextImage("products");

The `image/` prefix on the generator's name when registering the generator
tells Sham that it is an image picker. For more information see the next
chapter.

### Servlet context

Sham can also scan a servlet context for images. Use this if you need to pick
random images from a deployed servlet-based web application. Simply pass the
servlet context to Sham and it wil do the rest:

    Sham sham = Sham.getInstance();
    sham.setImageBaseDir("/WEB-INF/images");
    sham.setServletContext(servletContext);
    URL randomImage = sham.nextImage("products");


## Numbers and Booleans

You can also use Sham's internal random number generator to generate things
that you can get from a `Random` object.

    Sham sham = Sham.getInstance();
    boolean separateShippingAddress = sham.getRandom().nextBoolean();
    boolean price = 20 + sham.getRandom().nextInt(80);

