package ms.enterprise

import grails.compiler.GrailsCompileStatic
import ms.enterprise.enums.ValueType
import org.springframework.validation.Errors

@GrailsCompileStatic
class AttributeValue {

    Municipality municipality
    Attribute attribute
    String description
    Long revision = 1l
    Long number
    String textValue

    Date dateCreated

    ValueType valueType


    static constraints = {
        municipality nullable: false
        attribute nullable: false
        description nullable: true
        revision nullable: false
        number nullable: true, validator: {
            Long val,
            AttributeValue obj,
            Errors errors ->

                if(val == null && obj.textValue == null){
                    errors.reject(
                            'both.number.and.text.value.cant.be.null',
                            [] as Object[],
                            'Number and TextValue cant be null!')
                    return false
                }
                return true
        }
        textValue nullable: true
        valueType nullable: false
    }

    static mapping = {
        version false
        id generator: 'sequence', params:[sequence:'attribute_value_seq']
        id index: 'attribute_value_id_idx'
        attribute index: 'attribute_value_attribute_idx'
        attribute lazy: false
        municipality index: 'attribute_value_municipality_idx'

        description type: 'text'

        valueType enumType:"ordinal"

    }
}
