package ms.enterprise.search;

import grails.util.Holders;
import io.searchbox.client.JestClient;
import io.searchbox.client.JestClientFactory;
import io.searchbox.client.config.HttpClientConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class JestPool {

    private static final Logger log =
            LoggerFactory.getLogger(JestPool.class);

    private static JestClientFactory factory;

    private JestPool(){
        String host = Holders.getConfig().getProperty("elasticsearch.host");
        String port = Holders.getConfig().getProperty("elasticsearch.port");
        String url = "http://" + host + ":" + port;

        factory = new JestClientFactory();
        factory.setHttpClientConfig(new HttpClientConfig
                .Builder(url)
                .multiThreaded(true)
                .maxTotalConnection(100)
                .maxConnectionIdleTime(30, TimeUnit.SECONDS)
                .build());
    }

    public static void initialize(){
        if(factory == null) {
            new JestPool();
        }
    }

    public static JestClient getClient(){
        return factory.getObject();
    }
}
