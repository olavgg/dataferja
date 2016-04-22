package ms.enterprise

import grails.async.PromiseList
import grails.compiler.GrailsCompileStatic
import grails.util.Environment
import ms.enterprise.helpers.BufferedCsvReader
import ms.enterprise.search.JestPool
import ms.enterprise.search.SearchIndexCreator
import org.springframework.stereotype.Component
import reactor.spring.context.annotation.Consumer
import reactor.spring.context.annotation.Selector

@GrailsCompileStatic
@Component
@Consumer
class MainInitService {

    InitDataService initDataService

    @Selector('gorm:datastoreInitialized')
    def init() {
        JestPool.initialize()

        log.debug(Environment.currentEnvironment)
        if(Environment.currentEnvironment.equals(Environment.DEVELOPMENT)) {
            log.info('LASTER OPP FERJA MED TRAILERE SOM HAR HARDDISKER!')

            PromiseList list = new PromiseList()
            list << {

                SearchIndexCreator attrIdx = SearchIndexCreator.instance
                attrIdx
                        .setIndexName(SearchIndexCreator.ATTR_IDX_NAME)
                        .setAttributeMapping()
                        .deleteIndex()
                        .createIndexIfNotExists()

                SearchIndexCreator attrValIdx = SearchIndexCreator.instance
                attrValIdx
                        .setIndexName(SearchIndexCreator.ATTR_VAL_IDX_NAME)
                        .setAttributeValueMapping()
                        .deleteIndex()
                        .createIndexIfNotExists()


                initDataService.init()
                notify("attr.doattrsinsert.event", "ok")
                notify("attr.doattrvalsinsert.event", "ok")
                notify("attr.domunicipalityinsert.event", "ok")
                initDataService.insertImages()

                BufferedCsvReader bcr = new BufferedCsvReader()
                bcr.readForbrukerKraft()
                notify("attr.doattrsinsert.event", "ok")
                notify("attr.doattrvalsinsert.event", "ok")

                bcr.readFolkeMengdeOgBVekst()
                notify("attr.doattrsinsert.event", "ok")
                notify("attr.doattrvalsinsert.event", "ok")
            }
            list.onComplete {
                log.info("NÅ KAN FERJÅ LEGGA FRÅ KAI!!!")
            }
        }
    }
}
