package grails.plugin.sham

import grails.converters.JSON

class ShamController {

	def fixtureLoader
	def sham
	def shamLog

	def run = {
		if(!params.fixtures) {
			throw new IllegalArgumentException("Please provide fixture list")
		}
		shamLog.info("${params.prefix ? params.prefix + ', ' : ''}loading fixtures, sham seed: $sham.seed")
		fixtureLoader.load(params.fixtures)
		def redirectArgs
		def nocache = '_nocache=' + System.currentTimeMillis()
		if(params.redirectUri) {
			redirectArgs = [uri: params.redirectUri + joiner(params.redirectUri) + nocache]
		} else if(params.redirectUrl) {
			redirectArgs = [url: params.redirectUrl + joiner(params.redirectUrl) + nocache]
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
