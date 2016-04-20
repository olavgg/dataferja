package ms.enterprise

import grails.compiler.GrailsCompileStatic
import grails.transaction.Transactional
import ms.enterprise.enums.ValueType
import ms.enterprise.search.BulkAttrInsert

@GrailsCompileStatic
@Transactional
class AttributeService {

    def saveAttribute(
            Municipality municipality,
            Attribute attr,
            long number
    ){

        AttributeValue attributeValue = new AttributeValue(
                municipality: municipality,
                attribute: attr,
                number: number,
                valueType: ValueType.NUMBER
        )
        doSave(attributeValue)
    }

    def saveAttribute(
            Municipality municipality,
            Attribute attr,
            String text
    ){

        AttributeValue attributeValue = new AttributeValue(
                municipality: municipality,
                attribute: attr,
                textValue: text,
                valueType: ValueType.TEXT
        )
        doSave(attributeValue)

    }

    private def doSave(AttributeValue attributeValue){
        if(attributeValue.validate() && attributeValue.save()){
            log.debug("saved attribute ${attributeValue.attribute.label} " +
                    "with value: " +
                    "${attributeValue.number?:attributeValue.textValue}")
            BulkAttrInsert.index(attributeValue)
        }
    }
}
