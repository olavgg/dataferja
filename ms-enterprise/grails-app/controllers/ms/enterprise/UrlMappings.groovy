package ms.enterprise

class UrlMappings {

    static mappings = {
        "/$controller/$action?/$id?(.$format)?"{
            constraints {
                // apply constraints here
            }
        }

        "/p/$id?(.$format)?"(
                controller: 'image', action: 'show', namespace: 'media'
        )
        "/"(controller: 'default', action: 'index')

        "500"(view:'/error')
        "404"(view:'/notFound')
    }
}
