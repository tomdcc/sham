package grails.plugin.sham

class ShamController {

	def fixtureLoader

	def run = {
		if(!params.fixtures) {
			throw new IllegalArgumentException("Please provide fixture list")
		}
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
}
