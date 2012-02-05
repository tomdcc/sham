import org.grails.comments.Comment
import sham.grails.plugin.driver.Article

Comment.findAll()*.delete()
Article.findAll()*.delete()
