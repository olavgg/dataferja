package ms.enterprise.forms

import grails.compiler.GrailsCompileStatic
import grails.validation.Validateable

@GrailsCompileStatic
class SearchCmd implements Validateable {

    String query

    static constraints = {
        query nullable: false, blank: false, minSize: 2
    }
}
