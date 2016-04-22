package ms.enterprise.helpers;

import ms.enterprise.Attribute;
import ms.enterprise.Municipality;

import java.util.List;
import java.util.Map;

/**
 * User: olav
 * Date: 21.04.16
 * Time: 12:21
 */
public interface AttributeStore {

    Attribute save(String label);

    Municipality getMunicipality(String field);

    void saveAttrVal(List<Map<String, Object>> items);
}
