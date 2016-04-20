package ms.enterprise

import grails.compiler.GrailsCompileStatic

@GrailsCompileStatic
class FileBlob {

    byte[] bytes

    static constraints = {
    }

    static mapping = {
        version false
        id generator:'sequence', params:[sequence: 'file_blob_id_seq']

        id index:'file_blob_id_idx'
    }
}
