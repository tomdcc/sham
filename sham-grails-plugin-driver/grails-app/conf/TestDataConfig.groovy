import org.shamdata.Sham
import org.codehaus.groovy.grails.web.context.ServletContextHolder

def resourceUrlToUri(URL url) {
	def baseUri = ServletContextHolder.servletContext.getResource('/').toExternalForm()
	"/${url.toExternalForm() - baseUri}"

}

testDataConfig {
	sampleData {
		"sham.grails.plugin.driver.Article" {
			headline = { Sham.instance.nextHeadline() }
			teaser = { Sham.instance.nextParagraph(3) }
			text = { Sham.instance.nextParagraphs().collect{"<p>${it.encodeAsHTML()}</p>"}.join('\n') }
			author = { Sham.instance.nextPerson().name }
			authorEmail = { Sham.instance.nextPerson().email }
			authorAvatar = { Sham.instance.nextPerson().email }
			datePublished = { new Date() }
			imageUri = { resourceUrlToUri(Sham.instance.nextImage("articles")) }
		}
	}
}
