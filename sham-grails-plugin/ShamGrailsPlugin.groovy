import org.shamdata.Sham
import org.apache.log4j.Logger
import org.apache.log4j.FileAppender
import org.apache.log4j.SimpleLayout
import org.apache.log4j.Level

class ShamGrailsPlugin {
    def version = "0.2"
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

    def doWithSpring = {
		sham(Sham) { bean ->
			bean.factoryMethod = "getInstance"
			imageBaseDir = '/images'
		}
		shamLog(Logger) { bean ->
			bean.factoryMethod = "getInstance"
			bean.constructorArgs = ["sham"]
		}
    }

    def doWithDynamicMethods = { ctx -> }

    def doWithApplicationContext = { applicationContext ->
		applicationContext.sham.servletContext = applicationContext.servletContext

		def logger = applicationContext.shamLog
		logger.additivity = false
		logger.addAppender(new FileAppender(new SimpleLayout(), "sham.log"))
		logger.level = Level.DEBUG
    }

    def onChange = { event -> }
    def onConfigChange = { event -> }
}
