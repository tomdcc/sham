package grails.plugin.sham

import grails.plugin.spock.ControllerSpec
import grails.plugin.fixtures.FixtureLoader
import spock.lang.Unroll

class ShamControllerSpec extends ControllerSpec {

    def fixtureLoader
    static final String[] fixtureNames = ["fixture1", "fixture2", "fixture3" ] as String[]

    def setup() {
        fixtureLoader = Mock(FixtureLoader)
        controller.fixtureLoader = fixtureLoader
    }

    def "controller can apply fixtures and redirect to home page"() {
        when: 'call controller with fixtures only'
            controller.params.fixtures = fixtureNames
            controller.run()

        then: 'fixture loader was called'
            1 * fixtureLoader.load(fixtureNames)

        and: 'browser was redirected to home page'
            controller.redirectArgs.uri.startsWith("/?_nocache=")
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
            controller.redirectArgs.uri.startsWith("$targetUri${join}_nocache=")

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
            controller.redirectArgs.url.startsWith("$targetUrl${join}_nocache=")
		where:
			targetUrl                          | join
			'http:/www.foo.com/someTarget/foo' | '?'
			'http:/www.foo.com/someTarget?foo' | '&'
    }

}
