package ms.enterprise

import grails.converters.JSON
import grails.transaction.Transactional
import groovy.json.JsonSlurper
import io.searchbox.client.JestClient
import io.searchbox.core.Search
import io.searchbox.core.SearchResult
import ms.enterprise.forms.SearchCmd
import ms.enterprise.search.ElasticQueries
import ms.enterprise.search.JestPool
import ms.enterprise.search.SearchIndexCreator

class SearchController {

    @Transactional(readOnly = true)
    def municipality(SearchCmd form) {

        if(form.hasErrors()){
            render text: 'invalid query...', status: 400
            return
        }
        // Make sure only unicode characters is allowed, remove anything else
        String properQuery = form.query
                .replaceAll("[^\\p{L}\\p{N}]+", " ")
                .toLowerCase();

        String query = ElasticQueries.getMunicipalityQuery(properQuery)
        Search search = new Search.Builder(query)
                .addIndex(SearchIndexCreator.MUNICIPALITY_IDX_NAME)
                .build();

        JestClient client = JestPool.getClient()
        SearchResult result = client.execute(search);
        if(result.succeeded){
            log.debug("search succeeded...")

            JsonSlurper jsonSlurper = new JsonSlurper()
            Map obj = jsonSlurper.parseText(result.jsonString)

            log.debug("Total hits: ${obj.hits.total}")

            String returnFormat = request.getHeader("Accept");
            if(returnFormat.contains('application/json')){

                Map renderResult = [:]
                renderResult.total = obj.hits.total

                if(obj.hits.total > 0) {
                    List<Long> idCollection =
                            obj.hits.hits.collect {
                                Long.valueOf(it._id)
                            }
                    log.debug(idCollection)
                    List<Map> municipalities = Municipality.executeQuery(
                            "SELECT NEW MAP(" +
                                    "m.id as id, " +
                                    "m.name as name, " +
                                    "concat(m.number,' - ',m.name) as text, " +
                                    "m.county.name as parent_text," +
                                    "m.county.id as parent_id" +
                            ") " +
                            "FROM Municipality m " +
                            "WHERE m.id in :ids " +
                            "ORDER BY parent_text ASC, name ASC",
                            [ids: idCollection]
                    )

                    renderResult.hits = municipalities
                } else {
                    renderResult.hits = []
                }
                log.debug(renderResult)
                render renderResult as JSON
                return

            }
        }
        render text: '', status: 204
    }

    def attributes(SearchCmd form) {

        if (form.hasErrors()) {
            render text: 'invalid query...', status: 400
            return
        }
        String returnFormat = request.getHeader("Accept");

        // Make sure only unicode characters is allowed, remove anything else
        String properQuery = form.query
                .replaceAll("[^\\p{L}\\p{N}]+", " ")
                .toLowerCase();

        String query = ElasticQueries.getAttributesQuery(properQuery)
        Search search = new Search.Builder(query)
                .addIndex(SearchIndexCreator.ATTR_IDX_NAME)
                .build();

        JestClient client = JestPool.getClient()
        SearchResult result = client.execute(search);

        if(result.succeeded) {
            log.debug("search succeeded...")

            JsonSlurper jsonSlurper = new JsonSlurper()
            Map obj = jsonSlurper.parseText(result.jsonString)

            log.debug("Total hits: ${obj.hits.total}")

            if(returnFormat.contains('application/json')){

                def renderResult = [:]
                renderResult.hits = []
                renderResult.total = Long.valueOf(obj.hits.total)

                if(renderResult.total > 0) {
                    for(Map item : obj.hits.hits){
                        renderResult.hits.add([
                                id: item._id,
                                text: item.fields.label[0]
                        ])
                    }
                }
                render renderResult as JSON
                return

            }
        }
        render text: '', status: 204
    }
}
