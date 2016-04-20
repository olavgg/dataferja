package ms.enterprise

import grails.gsp.PageRenderer

class QueryTagLib {
    static defaultEncodeAs = [taglib:'none']
    //static encodeAsForTags = [tagName: [taglib:'html'], otherTagName: [taglib:'none']]

    static namespace = 'ferja'

    PageRenderer groovyPageRenderer

    Closure querySelector = { attrs ->
        out << groovyPageRenderer.render(
                template: "/taglib/query/query_selector",
                model: attrs
        )
    }
}
