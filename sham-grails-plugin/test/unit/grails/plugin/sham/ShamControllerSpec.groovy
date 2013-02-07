package grails.plugin.sham

import grails.test.mixin.TestFor
import spock.lang.Specification
import spock.lang.Unroll
import org.shamdata.Sham
import org.apache.log4j.Logger

@TestFor(ShamController)
class ShamControllerSpec extends Specification {

    def fixtureLoader
	def shamLog
	def sham
    static final String[] fixtureNames = ["fixture1", "fixture2", "fixture3" ] as String[]

	
	
    def setup() {
        fixtureLoader = Mock(FixtureLoader)
        controller.fixtureLoader = fixtureLoader
		shamLog = Mock(Logger)
		controller.shamLog = shamLog
		sham = new Sham()
		controller.sham = sham
    }

    def "controller can apply fixtures and redirect to home page"() {
        when: 'call controller with fixtures only'
            controller.params.fixtures = fixtureNames
            controller.run()

        then: 'fixture loader was called'
            1 * fixtureLoader.load(fixtureNames)

        and: 'browser was redirected to home page'
            response.redirectedUrl.startsWith("/?_nocache=")
    }

	@Unroll
    def "controller can apply fixtures and redirect to a uri"() {
        when: 'call controller with fixtures and redirect uri'
            controller.params.fixtures = fixtureNames
            controller.params.redirectUri = targetUri
            controller.run()

        then: 'fixture loader was called'
            1 * fixtureLoader.load(fixtureNames)

        and: 'browser was redirected to target uri'
            response.redirectedUrl.startsWith("$targetUri${join}_nocache=")

		where:
			targetUri         | join
			'/someTarget/foo' | '?'
			'/someTarget?foo' | '&'
    }

	@Unroll
    def "controller can apply fixtures and redirect to a url"() {
        when: 'call controller with fixtures and redirect url'
            String[] fixtureNames = ["fixture1", "fixture2", "fixture3" ] as String[]
            controller.params.fixtures = fixtureNames
            controller.params.redirectUrl = targetUrl
            controller.run()

        then: 'fixture loader was called'
            1 * fixtureLoader.load(fixtureNames)

        and: 'browser was redirected to target uri'
            response.redirectedUrl.startsWith("$targetUrl${join}_nocache=")
		where:
			targetUrl                          | join
			'http:/www.foo.com/someTarget/foo' | '?'
			'http:/www.foo.com/someTarget?foo' | '&'
    }

	@Unroll
	def "controller logs seed to log"() {
		given: 'sham instance with known seed'
			sham.seed = seed

		when: 'call logSeed'
			controller.params.prefix = prefix
			controller.logSeed()
		
		then: 'logger called with expected message'
			1 * shamLog.info(expectedValue)
		
		and: 'returns seed'
			response.text == seed as String
		
		where:
			seed | prefix  | expectedValue
			1001 | null    | "sham seed: 1001"
			1234 | 'o hai' | "o hai, sham seed: 1234"
	}

	@Unroll
	def "controller can set sham seed"() {
		when:
			controller.params.seed = seed as String
			controller.params.prefix = prefix
			controller.setSeed()

		then: 'seed set'
			sham.seed == seed

		and: 'expected log message logged'
			1 * shamLog.info(expectedLog)

		and: 'returns seed'
			response.text == seed as String

		where:
			seed | prefix  | expectedLog
			1001 | null    | "set sham seed to: 1001"
			1234 | 'o hai' | "o hai, set sham seed to: 1234"

	}

	def "controller can return current sham seed"() {
		when:
			controller.getSeed()

		then: 'returns seed'
			response.text == sham.seed as String
	}

}


// we don't want to depend on real fixture loader, since grails 1.3.7
// doesn't do the exclusions properly when running test-app -war
interface FixtureLoader {
    def load(String[] fixtures);
}