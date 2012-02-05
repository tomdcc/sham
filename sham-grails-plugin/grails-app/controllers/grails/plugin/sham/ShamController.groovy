package grails.plugin.sham

class ShamController {

    def fixtureLoader

    def run = {
        if(!params.fixtures) {
            throw new IllegalArgumentException("Please provide fixture list")
        }
        fixtureLoader.load(params.fixtures)
        def redirectArgs
        if(params.redirectUri) {
            redirectArgs = [uri: params.redirectUri]
        } else if(params.redirectUrl) {
            redirectArgs = [url: params.redirectUrl]
        } else {
            redirectArgs = [uri: '/']
        }
        redirect redirectArgs
    }
}
