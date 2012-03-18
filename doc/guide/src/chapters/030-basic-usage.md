# Basic Usage

Usage of Sham is usually through an instance of the `Sham` class.
This class acts provides a number of easy-to-use methods to generate data, and
is the class which ties the library together.

## Initialising Sham

You can create a new `Sham` instance by calling its default
constructor, or by calling the static `Sham.getInstance()` method.
Often you may wish to create some data in a place (for example some kind of
fixture) where it is difficult to pass a `Sham` instance in or have
one dependency-injected - if this is the case, use the static helper. Use of
the static instance should be a policy decision in your project, though - use
it everywhere or nowhere.

## Examples of Data Generation

You can easily generate random data using Sham's built-in generators with no
further work:

Generates a random person and prints out their name:

    Person person = Sham.getInstance().nextPerson();
    System.out.println(person.getName());

Generates and prints out a random news headline:

    System.out.println(Sham.getInstance().nextHeadline());

Generates and prints out a random product name:

    System.out.println(Sham.getInstance().nextProductName());

Selects a random image from a given directory, (assumes that a
`test/images/faces` directory exists):

    Sham sham = Sham.getInstance();
    sham.setImageBaseDir("test/images");
    URL randomFaceImage = sham.nextImage("faces")
    System.out.println(randomFaceImage);
