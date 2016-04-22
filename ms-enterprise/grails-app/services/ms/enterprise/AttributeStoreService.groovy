package ms.enterprise

import grails.transaction.Transactional
import groovy.sql.Sql
import ms.enterprise.helpers.AttributeStore
import ms.enterprise.search.BulkAttrInsert

import javax.sql.DataSource
import java.sql.SQLException
import java.sql.Timestamp

@Transactional
class AttributeStoreService implements AttributeStore{

    AttributeService attributeService
    DataSource dataSource

    Attribute save(String label) {
        Attribute attribute = new Attribute(label: label)
        attribute.save()
        BulkAttrInsert.index(attribute)
        return attribute
    }

    Municipality getMunicipality(String name){
        log.debug(name)
        if(name.contains('kommune')){
            name = name.replace(' kommune', '')
        }
        Municipality municipality
        int hasCountyInName = name.indexOf("(")
        if( hasCountyInName > -1){
            int endPos = name.indexOf(")")
            String mName = name.substring(0, hasCountyInName-1)
            String cName = name.substring(hasCountyInName+1, endPos)
            if(cName.equals('M og R')){
                cName = 'Møre'
            }
            County county = County.findByNameIlike(cName + '%')
            municipality = Municipality.findByCountyAndNameIlike(county, mName)
        } else {
            municipality = Municipality.findByNameIlike(name)
        }
        return municipality
    }

    void saveAttrVal(List<Map<String, Object>> items){

        Sql db = new Sql(dataSource)
        try {
            db.withTransaction {
                int counter = 0
                for(Map<Object, Object> item : items){

                    List<List<Object>> results = db.executeInsert([
                            attrId: item.attribute.id,
                            date: new Timestamp(new Date().getTime()),
                            muniId: item.municipality.id,
                            number: item.value
                    ], """
                        INSERT INTO attribute_value (
                          id,
                          attribute_id,
                          date_created,
                          municipality_id,
                          number,
                          revision,
                          value_type
                        ) VALUES(
                          nextval('attribute_value_seq'),
                          :attrId,
                          :date,
                          :muniId,
                          :number,
                          1,
                          0
                        )
                        """
                    )
                    item.id = (long)results.get(0).get(0)
                    BulkAttrInsert.index(item)
                    counter++
                    if( (counter % 1000) == 0){
                        log.debug("inserted $counter rows...")
                    }
                }


            }
        } catch (RuntimeException | SQLException e){
            log.error e.message, e
        }
    }
}
