package ms.enterprise

import grails.compiler.GrailsCompileStatic
import net.openhft.hashing.LongHashFunction

import java.text.DateFormat
import java.text.SimpleDateFormat

import static org.springframework.http.HttpStatus.NOT_FOUND

@GrailsCompileStatic
class ImageController {

    static namespace = "media"

    static allowedMethods = [
            show: 'GET'
    ]

    def show(String id) {
        Long longHash = LongHashFunction.xx_r39().hashChars(id)
        Image img = Image.findByHashValue(longHash)
        if(img){
            if(img.contentType.equals("image/svg+xml")){
                renderImg(img, 2)
            }
            if(params.boolean('thumbnail') && img.width > 200){
                renderImg(img, 0)
            } else {
                if(img.width > 800){
                    renderImg(img, 1)
                } else {
                    renderImg(img, 2)
                }
            }
        } else {
            render(text: 'not found', status: NOT_FOUND)
        }
    }

    protected def renderImg(Image img, int renderType){

        // Add expires header to 1 day
        final Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, 1);
        final DateFormat httpDateFormat =
                new SimpleDateFormat(
                        "EEE, dd MMM yyyy HH:mm:ss z",
                        Locale.US
                );
        httpDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        String expiryDate = httpDateFormat.format(calendar.getTime());

        response.setHeader("Expires", expiryDate);
        response.setContentType(img.contentType);

        if(renderType == 0){
            //response.setContentLength(img.thumbnailContentLength);
            //response.outputStream << img.thumbnailBytes.bytes
        }
        else if(renderType == 1){
            //response.setContentLength(img.normalContentLength);
            //response.outputStream << img.normalBytes.bytes
        }
        else {
            response.setContentLength(img.contentLength);
            response.outputStream << img.data.bytes
        }
        response.flushBuffer()
    }
}
