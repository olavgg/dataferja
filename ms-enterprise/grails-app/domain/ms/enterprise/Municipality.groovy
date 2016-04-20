package ms.enterprise

import com.google.common.base.MoreObjects
import grails.compiler.GrailsCompileStatic

@GrailsCompileStatic
class Municipality {

    County county
    String number
    String name
    String description

    Image logo

    static constraints = {
        county nullable: false
        number nullable: false
        name nullable: false, blank: false
        description nullable: true
        logo nullable: true
    }

    static mapping = {
        version false
        id generator: 'sequence', params: [sequence: 'municipality_seq']
        description type: 'text'

        //Indexes
        id index: 'municipality_id_idx'
        number index: 'municipality_number_idx'
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", id)
                .add("number", number)
                .add("name", name)
                .toString();
    }
}
