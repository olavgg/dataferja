package ms.enterprise

import com.github.slugify.Slugify
import com.google.common.base.MoreObjects
import grails.compiler.GrailsCompileStatic
import net.openhft.hashing.LongHashFunction

@GrailsCompileStatic
class Image {

    Long hashValue
    String title
    String author
    String license
    String filename
    String contentType
    String description
    FileBlob data
    Date dateCreated
    Date lastUpdated
    String uriName
    Integer width
    Integer height

    static constraints = {
        title nullable:true, blank:false
        filename nullable:false, blank:false
        contentType nullable:false, blank:false
        data nullable:false

        hashValue nullable:true, blank:false, unique: true
        description nullable:true, blank:true
        author nullable:true, blank:true
        license nullable:true, blank:true
        uriName nullable:true
        width nullable: true
        height nullable: true
    }

    static mapping = {
        version false
        id generator:'sequence', params:[sequence:'image_seq']
        //Indexes
        id index:'image_id_idx'
        title index:'image_title_idx'
        hashValue index:'image_hash_value_idx'

        data lazy: true
    }

    def beforeInsert() {
        updateUriName()
        updateHashValue()
    }

    def beforeUpdate() {
        updateUriName()
        updateHashValue()
    }

    public void updateHashValue(){
        this.hashValue = LongHashFunction.xx_r39().hashChars(this.uriName)
    }

    public void updateUriName() {
        if(this.title == null){
            if(this.filename.indexOf('.') == -1){
                this.title = this.filename
            } else{
                List<String> names = this.filename.tokenize('.')
                this.title = names.get(0)
                Slugify slg = new Slugify();
                this.uriName = slg.slugify(this.title)
            }
        }
    }

    public int getContentLength(){
        return this.data.bytes.length
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", id)
                .add("title", title)
                .add("author", author)
                .add("license", license)
                .add("filename", filename)
                .add("contentType", contentType)
                .add("contentLength", contentLength)
                .add("description", description)
                .add("dateCreated", dateCreated)
                .add("lastUpdated", lastUpdated)
                .add("width", width)
                .add("height", height)
                .add("uriName", uriName)
                .add("hashValue", this.hashValue)
                .toString();
    }
}
