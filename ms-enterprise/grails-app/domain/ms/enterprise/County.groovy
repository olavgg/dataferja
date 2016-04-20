package ms.enterprise

import com.google.common.base.MoreObjects
import grails.compiler.GrailsCompileStatic

@GrailsCompileStatic
class County {

    Region region
    String name
    String description

    static constraints = {
        region: nullable: false
        name nullable: false, blank: false, unique: true
        description nullable: true
    }

    static mapping = {
        version false
        id generator: 'sequence', params: [sequence: 'county_seq']
        description type: 'text'

        //Indexes
        id index: 'county_id_idx'
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", id)
                .add("name", name)
                .toString();
    }
}
