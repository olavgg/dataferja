package ms.enterprise.search;

import io.searchbox.client.JestClient;
import io.searchbox.client.JestResult;
import io.searchbox.indices.CreateIndex;
import io.searchbox.indices.DeleteIndex;
import io.searchbox.indices.IndicesExists;
import io.searchbox.indices.mapping.PutMapping;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * User: olav
 * Date: 19.04.16
 * Time: 15:09
 */
public class SearchIndexCreator {

    public static final String ATTR_IDX_NAME = "ms_attributes";
    public static final String ATTR_VAL_IDX_NAME = "ms_attribute_values";
    public static final String MUNICIPALITY_IDX_NAME = "ms_municipality";

    public static final String ATTR_TYPE = "ms_attribute";
    public static final String ATTR_VAL_TYPE = "ms_attribute_value";
    public static final String MUNICAPILITY_TYPE = "ms_municipality";

    private static final Logger log =
            LoggerFactory.getLogger(SearchIndexCreator.class);

    private JestClient client;

    private String indexName;
    private boolean indexCreated = false;
    private PutMapping mapping;

    private SearchIndexCreator(){
        this.client = JestPool.getClient();
    }

    public static SearchIndexCreator getInstance(){
        return new SearchIndexCreator();
    }

    public SearchIndexCreator createIndexIfNotExists(){
        if(this.indexName == null){
            log.error("Indexname has not been set, aborting");
            return this;
        }
        if(this.mapping == null){
            log.error("Mapping has not been set, aborting");
            return this;
        }
        try {
            IndicesExists indicesExists =
                    new IndicesExists.Builder(this.indexName).build();
            JestResult result = this.client.execute(indicesExists);
            if (!result.isSucceeded()) {
                if(!this.createIndex() || !this.createMapping()){
                    log.warn("Creation of index and mapping failed, " +
                            "deleting the index");
                    deleteIndex();
                } else {
                    indexCreated = true;
                }
            } else {
                log.info("Attribute index already exists");
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            log.error(
                    "Something went wrong while checking if index exists.");
            return this;
        }
        return this;
    }

    private boolean createIndex(){

        Settings.Builder settingsBuilder = Settings.settingsBuilder();
        settingsBuilder.put("number_of_shards", 5);
        settingsBuilder.put("number_of_replicas", 0);
        settingsBuilder.put("refresh_interval", "1s");
        settingsBuilder.put("store.type", "niofs");
        settingsBuilder.put("store.compress.stored", true);
        settingsBuilder.put("store.compress.tv", true);
        settingsBuilder.put("gateway.type", "local");
        settingsBuilder.put(
                "analysis.analyzer.norwegianAnalyzer.type",
                "custom");
        settingsBuilder.put(
                "analysis.analyzer.norwegianAnalyzer.tokenizer",
                "keyword");
        settingsBuilder.put(
                "analysis.analyzer.norwegianAnalyzer.filter",
                "norwegian");
        settingsBuilder.put(
                "analysis.filter.norwegian.type",
                "icu_collation");
        settingsBuilder.put(
                "analysis.filter.norwegian.language",
                "nb");
        try {
            this.client.execute(
                    new CreateIndex.Builder(this.indexName)
                            .settings(
                                    settingsBuilder.build().getAsMap()
                            )
                            .build());
            return true;
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        return false;
    }

    public String getIndexName() {
        return indexName;
    }

    public SearchIndexCreator setIndexName(String indexName) {
        this.indexName = indexName;
        return this;
    }

    public boolean getHasIndexBeenCreated(){
        return this.indexCreated;
    }

    public SearchIndexCreator deleteIndex(){
        DeleteIndex deleteIndex =
                new DeleteIndex.Builder(this.indexName).build();
        try {
            this.client.execute(deleteIndex);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        return this;
    }

    private boolean createMapping(){
        try{
            JestResult result = this.client.execute(this.mapping);
            if(!result.isSucceeded()){
                log.error(result.getErrorMessage());
            } else {
                return true;
            }
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        return false;
    }

    public SearchIndexCreator setAttributeMapping(){
        XContentBuilder builder;

        try {
            builder = XContentFactory.jsonBuilder()
            .startObject()
                .startObject(ATTR_TYPE)
                    .startObject("_all")
                        .field("enabled", "true")
                    .endObject()
                    .startObject("properties")

                        .startObject("label")
                            .field("type", "string")
                            .field("store", "yes")
                            .field("index", "analyzed")
                            .field("null_value", "")
                        .endObject()

                    .endObject()
                .endObject()
            .endObject();

            this.mapping = new PutMapping.Builder(
                    this.indexName,
                    ATTR_TYPE,
                    builder.prettyPrint().string()
            ).build();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        return this;
    }

    public SearchIndexCreator setAttributeValueMapping(){
        XContentBuilder builder;

        try {
            builder = XContentFactory.jsonBuilder()
            .startObject()
                .startObject(ATTR_VAL_TYPE)
                    .startObject("_all")
                        .field("enabled", "true")
                    .endObject()
                    .startObject("properties")

                        .startObject("id")
                            .field("type", "long")
                            .field("store", "yes")
                            .field("index", "not_analyzed")
                        .endObject()

                        .startObject("municipality_id")
                            .field("type", "long")
                            .field("store", "yes")
                            .field("index", "not_analyzed")
                        .endObject()

                        .startObject("label")
                            .field("type", "string")
                            .field("store", "yes")
                            .field("index", "analyzed")
                            .field("null_value", "")
                        .endObject()

                        .startObject("number")
                            .field("type", "long")
                            .field("store", "no")
                            .field("index", "not_analyzed")
                        .endObject()

                        .startObject("textValue")
                            .field("type", "string")
                            .field("store", "no")
                            .field("index", "analyzed")
                        .endObject()

                    .endObject()
                .endObject()
            .endObject();

            this.mapping = new PutMapping.Builder(
                    this.indexName,
                    ATTR_VAL_TYPE,
                    builder.prettyPrint().string()
            ).build();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        return this;
    }

    public SearchIndexCreator setMunicipalityMapping(){
        XContentBuilder builder;

        try {
            builder = XContentFactory.jsonBuilder()
            .startObject()
                .startObject(MUNICAPILITY_TYPE)
                    .startObject("_all")
                        .field("enabled", "true")
                    .endObject()
                    .startObject("properties")

                        .startObject("id")
                            .field("type", "long")
                            .field("store", "no")
                            .field("index", "not_analyzed")
                        .endObject()

                        .startObject("search_label")
                            .field("type", "string")
                            .field("store", "no")
                            .field("index", "analyzed")
                            .field("null_value", "")
                        .endObject()

                        .startObject("number")
                            .field("type", "string")
                            .field("store", "no")
                            .field("index", "not_analyzed")
                            .field("null_value", "")
                        .endObject()

                        .startObject("name")
                            .field("type", "string")
                            .field("store", "no")
                            .field("index", "not_analyzed")
                            .field("null_value", "")
                        .endObject()

                        .startObject("county_id")
                            .field("type", "long")
                            .field("store", "no")
                            .field("index", "not_analyzed")
                        .endObject()

                    .endObject()
                .endObject()
            .endObject();

            this.mapping = new PutMapping.Builder(
                    this.indexName,
                    MUNICAPILITY_TYPE,
                    builder.prettyPrint().string()
            ).build();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        return this;
    }
}
