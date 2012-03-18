# Grails Plugin

There is a [Grails](http://www.grails.org/) plugin for Sham, which follows
Sham releases. The plugin has the following features:

* Registers a `Sham` instance in the Spring application context
* Configures the instance using Grails configuration
* Allows testing of web page layouts using integration with the
  [Fixtures Plugin](http://www.grails.org/plugin/fixtures)
* Provides a convenient method to log Sham state in automated tests

## Installation

Installation is as for other Grails plugins, either through the
`install-plugin` command:

    grails install-plugin sham

Or by adding the appropriate dependency to your `BuildConfig.groovy`:

    compile ':sham:0.3'

You now have a `sham` bean, being an instance of the `Sham` class available in
your Spring application context. This will be injected into any Spring-managed
component in your application.

The bean registered is the same instance that will be returned by
`Sham.getInstance()`, so you can use Sham equally easily in code which does not
have dependency injection available.

## Integration with build-test-data and fixtures plugins

One of the main goals of the plugin is to make it easy to get the
[build-test-data](http://www.grails.org/plugin/build-test-data) and
[fixtures](http://www.grails.org/plugin/fixtures) plugins to generate
domain classes with randomised properties.

The `build-test-data` plugin can be configured to use Sham by plugging Sham
into your `TestDataConfig.groovy`. For example, for a hypothetical product:

    // TestDataConfig.groovy
    testDataConfig {
	    sampleData {
		    "com.example.Product" {
			    name = { Sham.instance.nextProductName() }
			    description = { Sham.instance.nextParagraph() }
			    specs = { Sham.instance.nextParagraphs() }
			    price = new BigDecimal(20 + Sham.instance.random.nextInt(81)) // price between 20 and 100
			}
        }
    }

You can also plug Sham into named fixtures with the fixtures plugin. For
example, for a fixture which builds a new user in the system:

    // fixtures/user.groovy
    def sham = Sham.instance

    build {
    	user(User) {
    	    def person = sham.nextPerson()

    	    username = person.username
    	    password = sham.random.nextInt(10000000) as String
    	    name = person.name
    	    emailAddress = person.email
    	    twitterUsername = person.twitterUsername
    	    facebookUserId = 100000000000 + sham.random.nextInt(100000000000)
    	}
    }

## Testing Layouts

Often design for web pages is done with a very specific set of mock data. This
can be in the form of the included text in a graphic design from e.g.
Phostoshop, or in the form of more static mock data that is the sem every time
you generate it when developing your application. This often leads to either a
lot of mucking about on the part of the web developer when testing a layout, or
sometimes page layouts that simply aren't tested with data of varying lengths
and e.g. image aspect ratios.

The Sham plugin provides an innovative way to test your web page layouts. It
has a controller method which takes a list of named Fixture Plugin fixtures
and a target URL, and will then run the fixtures in order, and issue a redirect
to a given URI in the web app.

An example is required. Suppose that your news site application has several
fixtures:

 * `tearDown.groovy`: a fixture to delete all data from the database
 * `article`: a fixture to load a simple article, visible on an articles page

You could then create a bookmark in your web browser pointing to
`http://localhost:8080/sham/run-fixtures?fixtures=tearDown&fixtures=article&redirectUri=/articles`
and click it - sham will run the fixtures and redirect your browser to
`/articles` so that you can see the result. Clicking the bookmark again will
do the same thing, so that you can see the page with a different set of data,
perhaps with a different number of body paragraphs, or a headline of a
different length, or an image with a different size or aspect ratio.

The idea is that you can iteratively view your page with different data
simply by clicking the bookmark button over and over again, and looking for
anything that looks wrong. Compare this to the common practice of loading
up a page and editing parts using Firebug or the browser's built-in developer
tools. That may even work for text, although pasting multiple paragraphs in
can be a pain, but then testing properly with differently sized images, or
a different number of child objects from the database does not work at all.
This plugin can assist testing all of these things if your fixtures are set
up to allow it.

## Logging

The Sham plugin creates a Log4J logger with the name `sham`, and also registers
it as a Spring bean with the name `shamLog`. This logger gets called for all
logging operations performed by the Sham controller - for example, calling the
controller above to test a layout would have resulted in a log line something
like this:

    loading fixtures [tearDown, article], sham seed: 8129371298371

Passing a `prefix` parameter to that method adds a prefix to the line logged.
This allows logging for example a test name at the start of the test. For
example, hitting the URL above but with `&prefix=ArticleSpec.testArticlePage`
added on the end would result in a log line of:

    ArticleSpec.testArticlePage, loading fixtures [tearDown, article], sham seed: 8129371298371


By default, the Sham log is logged to its own file, defaults to `sham.log`. To
log Sham log entries along with your other Grails logging, set the
`sham.separate.log.file` config option to `false`. To override the location of
the file, set `sham.log.file` config option to the path where you would like
the log file to go. This should be a `String`, not a `File` object.

## Working with the Random Seed

The Sham plugin provides several methods for working with Sham's internal RNG
seed.

### /sham/log-seed

Hitting `/sham/log-seed` will log the curret value of the seed to the Sham log.
A `prefix` parameter can be provided to give context to eht line in the log, as
above.

If the request looks like an AJAX request, the seed value will be rendered out
as a JSON object, otherwise it is rendered out as plain text.

### /sham/get-seed

If the request looks like an AJAX request, the seed value will be rendered out
as a JSON object, otherwise it is rendered out as plain text.

### /sham/set-seed

This takes a `seed` parameter, and will attempt to set Sham's RNG seed to that
value. It will also log the new value to the Sham log, with an optional
`prefix` if passed in, as above.

If the request looks like an AJAX request, the new seed value will be rendered
out as a JSON object, otherwise it is rendered out as plain text.

## Image Directory Configuration

The plugin also provides a configuration option to set the base image directory
for the default image picker provided by Sham. The configation option is
`sham.image.dir`, and this may be set to either a `String` object, in which
case it will be assumed to be relative to the web application root, e.g.
`/WEB-INF/images`, or to a `File` object pointing to a directory, in which case
Sham will simply that directory.