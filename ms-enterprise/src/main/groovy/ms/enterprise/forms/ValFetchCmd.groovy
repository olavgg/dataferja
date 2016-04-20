package ms.enterprise.forms

import grails.compiler.GrailsCompileStatic
import grails.validation.Validateable

@GrailsCompileStatic
class ValFetchCmd implements Validateable {

    Set<Long> municipality_id
    Set<Long> attr_id

    static constraints = {
        municipality_id nullable: false
        attr_id nullable: false
    }
}
