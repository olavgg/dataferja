package ms.enterprise.search

import groovy.transform.CompileStatic
import io.searchbox.core.Index
import ms.enterprise.Attribute
import ms.enterprise.AttributeValue
import ms.enterprise.Municipality
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 *
 * User: olav
 * Date: 19.04.16
 * Time: 16:40
 *
 */
@CompileStatic
class BulkAttrInsert {

    private static final Logger log =
            LoggerFactory.getLogger(BulkAttrInsert.class);

    public static ArrayList<Index> attrValList = new ArrayList<>()
    public static ArrayList<Index> attrList = new ArrayList<>()
    public static ArrayList<Index> municipalities = new ArrayList<>()

    static void index(AttributeValue attributeValue){

        Map<Object, Object> attrMap = new HashMap<>()
        attrMap.put("id", attributeValue.id)
        attrMap.put("municipality_id", attributeValue.municipality.id)
        attrMap.put("attribute_id", attributeValue.attribute.id)
        attrMap.put("number", attributeValue.number)
        attrMap.put("textValue", attributeValue.textValue)

        Index index = new Index.Builder(attrMap)
                .index(SearchIndexCreator.ATTR_VAL_IDX_NAME)
                .type(SearchIndexCreator.ATTR_VAL_TYPE)
                .id(String.valueOf(attributeValue.id))
                .build();

        attrValList.add(index)
    }

    static void index(Attribute attribute){
        Map<Object, Object> attrMap = new HashMap<>()
        attrMap.put("label", attribute.label)

        Index index = new Index.Builder(attrMap)
                .index(SearchIndexCreator.ATTR_IDX_NAME)
                .type(SearchIndexCreator.ATTR_TYPE)
                .id(String.valueOf(attribute.id))
                .build();

        attrList.add(index)
    }

    static void index(Municipality municipality, String indexName){

        Map<Object, Object> attrMap = new HashMap<>()
        attrMap.put("id", municipality.id)
        attrMap.put("number", municipality.number)
        attrMap.put("search_label", municipality.number +
                " " + municipality.name.toLowerCase())
        attrMap.put("name", municipality.name.toLowerCase())
        attrMap.put("county_id", municipality.county.id)

        Index index = new Index.Builder(attrMap)
                .index(indexName)
                .type(SearchIndexCreator.MUNICAPILITY_TYPE)
                .id(String.valueOf(municipality.id))
                .build();

        municipalities.add(index)
    }

}
