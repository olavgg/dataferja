package ms.enterprise

import grails.compiler.GrailsCompileStatic
import ms.enterprise.search.JestPool
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

        /*if(Environment.currentEnvironment.equals(Environment.DEVELOPMENT)) {
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
                initDataService.doAttrValsInsert([:])
                notify("attr.domunicipalityinsert.event", "ok")
                initDataService.insertImages()

                BufferedCsvReader bcr = new BufferedCsvReader()
                bcr.readInnERMenn()
                log.debug("Inserted Menn")
                notify("attr.doattrsinsert.event", "ok")
                initDataService.doAttrValsInsert([:])

                BufferedCsvReader bcr2 = new BufferedCsvReader()
                bcr2.readInnERKvinner()
                log.debug("Inserted Kvinner")
                notify("attr.doattrsinsert.event", "ok")
                initDataService.doAttrValsInsert([:])

                BufferedCsvReader bcr3 = new BufferedCsvReader()
                bcr3.readFolkeMengdeOgBVekst()
                log.debug("Inserted Befolkning")
                notify("attr.doattrsinsert.event", "ok")
                initDataService.doAttrValsInsert([:])

                BufferedCsvReader bcr4 = new BufferedCsvReader()
                bcr4.readForbrukerKraft()
                log.debug("Inserted Kraft")
                notify("attr.doattrsinsert.event", "ok")
                notify("attr.doattrvalsinsert.event", "ok")

            }
            list.onComplete {
                log.info("NÅ KAN FERJÅ LEGGA FRÅ KAI!!!")
            }
        }*/
    }
}
