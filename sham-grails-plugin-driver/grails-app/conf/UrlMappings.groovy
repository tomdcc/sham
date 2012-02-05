class UrlMappings {

	static mappings = {
		"/"(controller: 'homePage', action: 'index')
		"/article/$id"(controller: 'article', action: 'view')
		"/$controller/$action?/$id?"{
			constraints {
				// apply constraints here
			}
		}

		"500"(view:'/error')
	}
}
