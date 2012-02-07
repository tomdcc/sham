import org.shamdata.Sham
import org.apache.log4j.Logger
import org.apache.log4j.FileAppender
import org.apache.log4j.SimpleLayout
import org.apache.log4j.Level

class ShamGrailsPlugin {
    // the plugin version
    def version = "0.2"
    // the version or versions of Grails the plugin is designed for
    def grailsVersion = "1.3.7 > *"
    // the other plugins this plugin depends on
    def dependsOn = [:]
    // resources that are excluded from plugin packaging
    def pluginExcludes = [
            "grails-app/views/error.gsp"
    ]

    // TODO Fill in these fields
    def author = "Tom Dunstan"
    def authorEmail = "tom@energizedwork.com"
    def title = "Plugin summary/headline"
    def description = '''\\
Brief description of the plugin.
'''

    // URL to the plugin's documentation
    def documentation = "http://grails.org/plugin/sham"

    def doWithWebDescriptor = { xml ->
        // TODO Implement additions to web.xml (optional), this event occurs before 
    }

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

    def doWithDynamicMethods = { ctx ->
        // TODO Implement registering dynamic methods to classes (optional)
    }

    def doWithApplicationContext = { applicationContext ->
		applicationContext.sham.servletContext = applicationContext.servletContext

		def logger = applicationContext.shamLog
		logger.additivity = false
		logger.addAppender(new FileAppender(new SimpleLayout(), "sham.log"))
		logger.level = Level.DEBUG
    }

    def onChange = { event ->
        // TODO Implement code that is executed when any artefact that this plugin is
        // watching is modified and reloaded. The event contains: event.source,
        // event.application, event.manager, event.ctx, and event.plugin.
    }

    def onConfigChange = { event ->
        // TODO Implement code that is executed when the project configuration changes.
        // The event is the same as for 'onChange'.
    }
}
