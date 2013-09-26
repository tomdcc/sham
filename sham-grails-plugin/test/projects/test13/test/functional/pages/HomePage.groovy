package pages

import geb.Page
import geb.Module

class HomePage extends Page {
	static url = '/'
	static content = {
		topArticle { module ArticleModule, $('.topArticle') }
		otherArticles { $('.other-articles .article').collect { module ArticleModule, it} }
	}


}

class ArticleModule extends Module {
	static content = {
		headline { $('.headline').text() }
		teaser(required: false) { $('.teaser').text() }
		link { $('a').@href }
		image { $('.img-wrapper img').@src }
	}
}
