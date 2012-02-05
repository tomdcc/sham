import geb.spock.GebReportingSpec
import geb.buildadapter.SystemPropertiesBuildAdapter
import org.codehaus.groovy.grails.commons.ConfigurationHolder

abstract class BaseSpec extends GebReportingSpec {

	static String getServerUrl() {
		ConfigurationHolder.config?.grails?.serverURL ?: 'http://localhost:8080/'
	}

	static {
		// if it looks like we're in the IDE, set these, since the geb plugin isn't doing it for us
		if(!System.getProperty(SystemPropertiesBuildAdapter.BASE_URL_PROPERTY_NAME)) {
			System.setProperty(SystemPropertiesBuildAdapter.BASE_URL_PROPERTY_NAME, serverUrl)
			System.setProperty(SystemPropertiesBuildAdapter.REPORTS_DIR_PROPERTY_NAME, 'target/test-reports/geb')
		}
	}

	String getBaseUrl() {
		serverUrl
	}

}
