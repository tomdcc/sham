package grails.plugin.sham

import grails.plugin.spock.IntegrationSpec
import org.shamdata.Sham
import org.apache.log4j.Logger

class PluginSpec extends IntegrationSpec {

	def grailsApplication

	def "plugin adds sham instance to application context"() {
		when: 'app asks for sham instance'
			def sham = grailsApplication.mainContext.sham
		
		then: "it's there"
			sham
		
		and: "it's a sham instance"
			sham instanceof Sham
		
		and: "it's the same instance as Sham.instance returns"
			sham.is(Sham.instance)

		when: 'app asks for shamLog'
			def shamLog = grailsApplication.mainContext.shamLog

		then: "it's there"
			shamLog

		and: "it's a Logger instance"
			shamLog instanceof Logger
	}
}
