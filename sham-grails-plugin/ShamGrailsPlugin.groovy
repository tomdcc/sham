import org.shamdata.Sham
import org.apache.log4j.Logger
import org.apache.log4j.FileAppender
import org.apache.log4j.SimpleLayout
import org.apache.log4j.Level
import org.codehaus.groovy.grails.commons.ConfigurationHolder

class ShamGrailsPlugin {
    def version = "0.3.1"
    def grailsVersion = "1.3.7 > *"
    def dependsOn = [:]
    def pluginExcludes = [
            "grails-app/views/error.gsp"
    ]

	def license = "MIT"
	def issueManagement = [system: 'github', url: 'https://github.com/tomdcc/sham/issues' ]
	def scm = [ url: "http://svn.grails-plugins.codehaus.org/browse/grails-plugins/" ]

    def author = "Tom Dunstan"
    def authorEmail = "grails@tomd.cc"
    def title = "Sham Plugin"
    def description = '''A plugin using the sham data generation library to generate test / demo data when testing your app.'''
    def documentation = "http://grails.org/plugin/sham"

    def doWithWebDescriptor = { xml -> }

    static config
    def doWithSpring = {
        loadConfig()

        String imageDirVal
        if(config?.sham?.image?.dir instanceof File) {
            imageDirVal = config.sham.image.dir.absolutePath
        } else if(config?.sham?.image?.dir) {
            imageDirVal = config.sham.image.dir
        } else {
            imageDirVal = '/images'
        }

		sham(Sham) { bean ->
			bean.factoryMethod = "getInstance"
			imageBaseDir = imageDirVal
		}
		shamLog(Logger) { bean ->
			bean.factoryMethod = "getInstance"
			bean.constructorArgs = ["sham"]
		}
    }

    def doWithDynamicMethods = { ctx -> }

    def doWithApplicationContext = { applicationContext ->
        if(!(config?.sham?.image?.dir instanceof File)) {
            // if configured with File object, images should use FileSystemImagePicker, otherwise relative to web app
            // so we need the servlet context set
            applicationContext.sham.servletContext = applicationContext.servletContext
        }

        // put sham logging into separate file unless ordered not to
        boolean separateShamLogfile = config?.sham?.separate?.log?.file != null ? config.sham.separate.log.file : true
        if(separateShamLogfile) {
            def logger = applicationContext.shamLog
            logger.additivity = false
            logger.addAppender(new FileAppender(new SimpleLayout(), config?.sham?.log?.file ?: "sham.log"))
            logger.level = Level.DEBUG
        }
    }

    def onChange = { event -> }
    def onConfigChange = { event -> }


    void loadConfig() {
        GroovyClassLoader classLoader = new GroovyClassLoader(getClass().getClassLoader())
        def confClass
        try {
            confClass = classLoader.loadClass('ShamConfig')
        } catch (Exception e) {}
        config = confClass ? new ConfigSlurper().parse(confClass).merge(ConfigurationHolder.config) : ConfigurationHolder.config
    }
}
