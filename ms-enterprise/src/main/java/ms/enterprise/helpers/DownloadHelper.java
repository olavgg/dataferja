package ms.enterprise.helpers;

import org.apache.tika.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;


public class DownloadHelper {

    private static final Logger log =
            LoggerFactory.getLogger(DownloadHelper.class);

    public static byte[] downloadFile(URL url){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            URLConnection conn = url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            conn.connect();

            IOUtils.copy(conn.getInputStream(), baos);
        }
        catch (IOException e){
            log.error(e.getMessage(), e);
        }
        return baos.toByteArray();
    }
}
