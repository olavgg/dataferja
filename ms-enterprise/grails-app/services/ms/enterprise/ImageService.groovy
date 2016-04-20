package ms.enterprise

import grails.compiler.GrailsCompileStatic
import grails.transaction.Transactional
import ms.enterprise.helpers.SimpleImageInfo


@GrailsCompileStatic
@Transactional
class ImageService {

    def long createImage(String filename, String contentType, byte[] bytes) {
        Image image = new Image()
        image.filename = filename

        image.data = new FileBlob(bytes: bytes)
        if(!(image.data.validate() && image.data.save())){
            image.data.errors.allErrors.each{
                log.error it
            }
        }
        image.contentType = contentType

        if(!contentType.equals("image/svg+xml")){
            SimpleImageInfo sii = new SimpleImageInfo(bytes)
            image.width = sii.width
            image.height = sii.height
        }

        if(image.validate() && image.save()){
            log.debug("Image: ${image.filename} saved.")
            return (long)image.id
        } else {
            image.errors.allErrors.each{
                log.error it
            }
        }
        return 0l
    }

}
