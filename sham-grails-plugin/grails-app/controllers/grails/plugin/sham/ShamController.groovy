package grails.plugin.sham

import grails.converters.JSON

class ShamController {

	def fixtureLoader
	def sham
	def shamLog

	def run = {
        def fixtures = params.remove('fixtures')
        def prefix = params.remove('prefix')
        def redirectUri = params.remove('redirectUri')
        def redirectUrl = params.remove('redirectUrl')
        if(!fixtures) {
            throw new IllegalArgumentException("Please provide fixture list")
		}
		shamLog.info("${prefix ? prefix + ', ' : ''}loading fixtures $fixtures, sham seed: $sham.seed")

        try {
            fixtureLoader.load(fixtures, params)
        } catch(MissingMethodException e) {
            // older version of fixtures plugin, parameter passing not supported
            fixtureLoader.load(fixtures)
        }

		def redirectArgs
		def nocache = '_nocache=' + System.currentTimeMillis()
		if(redirectUri) {
			redirectArgs = [uri: redirectUri + joiner(redirectUri) + nocache]
		} else if(redirectUrl) {
			redirectArgs = [url: redirectUrl + joiner(redirectUrl) + nocache]
		} else {
			redirectArgs = [uri: '/?' + nocache]
		}
		redirect redirectArgs
	}

	private String joiner(String uri) {
		uri.contains('?') ? '&' : '?'
	}
	
	def logSeed = {
		shamLog.info("${params.prefix ? params.prefix + ', ' : ''}sham seed: $sham.seed")
		getSeed()
	}

	def setSeed = {
		sham.seed = params.seed as long
		shamLog.info("${params.prefix ? params.prefix + ', ' : ''}set sham seed to: $sham.seed")
		getSeed()
	}

	def getSeed = {
		def seed = sham.seed
		if (request.xhr) {
			 render seed as JSON
		} else {
			render text: seed
		}
	}
}
