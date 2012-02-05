package sham.grails.plugin.driver

class HomePageController {

    def index = {
		def articles = Article.findAllByDatePublishedIsNotNull(sort: 'datePublished', order: 'desc')
		def topArticle = articles.size() ? articles.remove(0) : null
		[topArticle: topArticle, otherArticles: articles]
	}
}
