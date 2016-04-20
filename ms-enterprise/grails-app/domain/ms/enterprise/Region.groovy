package ms.enterprise

import com.google.common.base.MoreObjects
import grails.compiler.GrailsCompileStatic

@GrailsCompileStatic
class Region {

    String name
    String i18nCode
    String description

    static constraints = {
        name nullable: false, blank: false, unique: true
        i18nCode nullable: false, blank: false, unique: true
        description nullable: true
    }

    static mapping = {
        version false
        id generator: 'sequence', params: [sequence: 'region_seq']
        description type: 'text'

        //Indexes
        id index: 'region_id_idx'
        i18nCode index: 'region_i18n_code_idx'
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", id)
                .add("name", name)
                .add("i18nCode", i18nCode)
                .toString();
    }
}
