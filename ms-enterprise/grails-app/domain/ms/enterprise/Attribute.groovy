package ms.enterprise

import grails.compiler.GrailsCompileStatic

@GrailsCompileStatic
class Attribute {

    String label
    String description

    static constraints = {
        label nullable: false
        description nullable: true
    }

    static mapping = {
        version false
        id generator: 'sequence', params:[sequence:'attribute_seq']
        id index: 'attribute_id_idx'
        label index: 'attribute_label_idx'

        description type: 'text'
    }
}
