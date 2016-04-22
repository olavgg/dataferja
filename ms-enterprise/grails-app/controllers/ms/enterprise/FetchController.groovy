package ms.enterprise

import grails.converters.JSON
import grails.transaction.Transactional
import ms.enterprise.forms.ValFetchCmd

class FetchController {

    @Transactional(readOnly = true)
    def values(ValFetchCmd form) {

        def data = [:]
        data.headers = []
        List<Attribute> attributes = Attribute.getAll(form.attr_id)
        int i = 0;
        for(char alphabet = 'A'; alphabet <= 'Z';alphabet++) {
            if(i < attributes.size()){
                data.headers.add([
                    id: attributes.get(i).id,
                    text: "(${alphabet}) ${attributes.get(i).label}"
                ])
                i++
            } else {
                break;
            }
        }

        List<Map> values = AttributeValue.executeQuery(
                "SELECT NEW MAP(" +
                        "av.attribute.id as attrId," +
                        "av.municipality.id as muniId," +
                        "av.number as number," +
                        "av.textValue as text," +
                        "av.valueType as type" +
                ")" +
                "FROM AttributeValue av " +
                "WHERE av.attribute.id IN (:attrsIds) " +
                "AND av.municipality.id IN (:muniIds)",
                [
                        attrsIds: form.attr_id as List,
                        muniIds: form.municipality_id as List
                ]
        )

        List<Municipality> municipalities =
                Municipality.getAll(form.municipality_id)

        data.rows = []

        for(int y = 0; y < municipalities.size(); y++) {
            Municipality m = municipalities.get(y)
            List row = []
            row.add([
                    id: m.id,
                    text: m.number + " - " + m.name
            ])
            for(Map header : data.headers){
                for(Map attrVal : values){
                    if(header.id == attrVal.attrId && m.id == attrVal.muniId){
                        row.add([
                                id: attrVal.attrId,
                                text: attrVal.number?:attrVal.text
                        ])
                        break;
                    }
                }
            }

            data.rows.add(row)

        }

        render data as JSON
    }
}
