import pages.HomePage
import groovyx.net.http.HTTPBuilder
import static groovyx.net.http.ContentType.TEXT

class HomePageSpec extends BaseSpec {

	static final FIXTURES_URL = "/sham/run-fixtures?fixtures=tearDown&fixtures=frontPage"
	
	def "can hit fixtures"() {
		when: 'hit fixture url'
			go FIXTURES_URL

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

	private long fetchSeed() {
		def http = new HTTPBuilder('http://localhost:8080')
		http.get(path: '/sham/seed', contentType: TEXT).readLine() as long
	}

	private void setSeed(long seed) {
		def http = new HTTPBuilder('http://localhost:8080')
		assert http.get(path: '/sham/set-seed', params: [seed: seed], contentType: TEXT).readLine() as long == seed // set-seed returns set value
	}

	def "can get repeatable fixture output for same seed"() {
		when: 'fetch seed'
			long seed = fetchSeed()

		and: 'hit fixture url'
			go FIXTURES_URL

		then: 'redirected to home page'
			at HomePage

		and: 'current seed is different'
			seed != fetchSeed()

		when: 'hit again'
			def origHeadline = topArticle.headline
			go FIXTURES_URL

		then: 'redirected to home page with different headline'
			at HomePage
			origHeadline != topArticle.headline

		when: 'set seed'
			setSeed(seed)

		and: 'run fixtures again'
			go FIXTURES_URL

		then: 'see original headline'
			at HomePage
			origHeadline == topArticle.headline
	}

}
