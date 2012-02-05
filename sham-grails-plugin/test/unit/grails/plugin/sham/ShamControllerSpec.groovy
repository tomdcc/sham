package grails.plugin.sham

import grails.plugin.spock.ControllerSpec
import grails.plugin.fixtures.FixtureLoader

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
            controller.redirectArgs.uri == '/'
    }

    def "controller can apply fixtures and redirect to a uri"() {
        given:
            String targetUri = '/someTarget/foo'

        when: 'call controller with fixtures and redirect uri'
            controller.params.fixtures = fixtureNames
            controller.params.redirectUri = targetUri
            controller.run()

        then: 'fixture loader was called'
            1 * fixtureLoader.load(fixtureNames)

        and: 'browser was redirected to target uri'
            controller.redirectArgs.uri == targetUri
    }

    def "controller can apply fixtures and redirect to a url"() {
        given:
            String targetUrl = 'http:/www.foo.com/someTarget/foo'

        when: 'call controller with fixtures and redirect url'
            String[] fixtureNames = ["fixture1", "fixture2", "fixture3" ] as String[]
            controller.params.fixtures = fixtureNames
            controller.params.redirectUrl = targetUrl
            controller.run()

        then: 'fixture loader was called'
            1 * fixtureLoader.load(fixtureNames)

        and: 'browser was redirected to target uri'
            controller.redirectArgs.url == targetUrl
    }

}
