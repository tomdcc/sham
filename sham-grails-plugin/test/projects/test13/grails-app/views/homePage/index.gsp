<!DOCTYPE html>
<html>
<head>
	<title>Foo News</title>
	<meta name="layout" content="main">
</head>
<body>
<g:if test="${topArticle}">
	
	<div class="topArticle">
		<div class="left">
			<h2 class="headline">${topArticle.headline.encodeAsHTML()}</h2>
			<div class="byline">
				%{--<img src="http://2.gravatar.com/avatar/${topArticle.authorEmail.encodeAsMD5()}5b131a252b91dc91442d8b9772dc6045?s=32&d=wavatar"/>--}%
				<img src="${topArticle.authorAvatar}"/>
				${topArticle.author.encodeAsHTML()}
			</div>
			<p class='teaser'>
				<g:if test="${topArticle.teaser}">${topArticle.teaser.encodeAsHTML()}</g:if>
				<g:else>${topArticle.firstParagraph}</g:else>
			</p>
			<g:link class='read-more' controller='article' id="${topArticle.id}">Read more...</g:link>
		</div>
		<div class="img-wrapper">
			<img class='main' src="${topArticle.imageUri}">
		</div>
	</div>

	<div class='other-articles'>
		<h2>Other News</h2>
		<g:each var='article' in="${otherArticles}">
			<div class='article'>
				<div class="img-wrapper">
					<img src="${article.imageUri}"/>
				</div>
				<g:link class='headline' controller='article' id="${article.id}">${article.headline.encodeAsHTML()}</g:link>
			</div>
		</g:each>
	</div>
</g:if>
<g:else>
	No articles
</g:else>
</body>
</html>