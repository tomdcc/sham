class BootStrap {

	def fixtureLoader

    def init = { servletContext ->
		fixtureLoader.load('frontPage')
    }
    def destroy = {
    }
}
