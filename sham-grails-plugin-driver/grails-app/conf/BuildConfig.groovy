grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"

grails.project.dependency.resolution = {
    // inherit Grails' default dependencies
    inherits("global") {
        // uncomment to disable ehcache
        // excludes 'ehcache'
    }
    log "warn" // log level of Ivy resolver, either 'error', 'warn', 'info', 'debug' or 'verbose'
    repositories {
		mavenLocal()
		mavenCentral()
        grailsPlugins()
        grailsHome()
        grailsCentral()
    }
    dependencies {
		def webdriverVersion = '2.33.0'
		test "org.seleniumhq.selenium:selenium-firefox-driver:$webdriverVersion"
		test "org.codehaus.geb:geb-spock:0.6.0"

		test('org.codehaus.groovy.modules.http-builder:http-builder:0.5.1') {
			excludes "xml-apis", 'groovy'
		}
    }
	
	plugins {
		compile ':hibernate:1.3.9'
		compile ':tomcat:1.3.9'

		compile ':build-test-data:1.1.2'
		compile ':sham:0.3.1'

		runtime ':fixtures:1.2'

		test ":spock:0.5-groovy-1.7"
		test ":geb:0.6.0"
	}
}
