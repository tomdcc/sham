import pages.HomePage

class HomePageSpec extends BaseSpec {
	
	def "can hit fixtures"() {
		when: 'hit fixture url'
			go "/sham/run-fixtures?fixtures=tearDown&fixtures=frontPage"

		then: 'redirected to home page'
			at HomePage

		and: 'see topArticle'
			topArticle.headline
			topArticle.teaser
			topArticle.image =~ /.*\/images\/articles\/.*\.jpg/
			topArticle.link.contains("/article?id=")

		and: 'see 10 other articles'
			otherArticles.size() == 10
			otherArticles.each { article ->
				assert article.headline
				assert !article.teaser
				assert article.image =~ /.*\/images\/articles\/.*\.jpg/
				assert article.link.contains("/article?id=")
			}

	}
}
