import sham.grails.plugin.driver.Article
import org.shamdata.Sham
import org.codehaus.groovy.grails.web.context.ServletContextHolder

def sham = Sham.instance

build {
	mainNews(Article) {
		def authorObject = sham.nextPerson()
		author = authorObject.name
		authorEmail = authorObject.email
		def baseUri = ServletContextHolder.servletContext.getResource('/').toExternalForm()
		authorAvatar = '/' + sham.nextImage("authors/${authorObject.gender.name().toLowerCase()}").toExternalForm() - baseUri
	}
	
	10.times { i ->
		"otherArticle$i"(Article) {
			datePublished = new Date(System.currentTimeMillis() - (i * 3600000))
		}
	}
}