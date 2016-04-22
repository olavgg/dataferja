package ms.enterprise

import grails.compiler.GrailsCompileStatic
import grails.transaction.NotTransactional
import grails.transaction.Transactional
import io.searchbox.client.JestClient
import io.searchbox.client.JestResult
import io.searchbox.core.Bulk
import io.searchbox.core.Index
import ms.enterprise.helpers.DownloadHelper
import ms.enterprise.init.InitImages
import ms.enterprise.search.BulkAttrInsert
import ms.enterprise.search.JestPool
import ms.enterprise.search.SearchIndexCreator
import org.apache.tika.config.TikaConfig
import org.apache.tika.metadata.Metadata
import org.apache.tika.mime.MediaType
import org.apache.tika.mime.MimeType
import reactor.spring.context.annotation.Consumer
import reactor.spring.context.annotation.Selector

import java.nio.charset.StandardCharsets

@GrailsCompileStatic
@Consumer
@Transactional
class InitDataService {

    AttributeService attributeService
    ImageService imageService

    def init() {
        String fp = "data/region_data_norway.csv"
        File fileHandler = new File(fp)
        if(fileHandler.exists()){
            fileHandler.splitEachLine(",") { fields ->
                Region region = new Region(
                        name: fields[0],
                        i18nCode: fields[1]
                )
                if(region.validate() && region.save()){
                    initCounties(region)
                } else {
                    region.errors.allErrors.each{ log.error it }
                }
            }
        }
        initMunicipalities()
    }

    def initCounties(Region region){
        String fp = "data/county_data_"+ region.i18nCode +".csv"
        File fileHandler = new File(fp)
        if(fileHandler.exists()){
            fileHandler.splitEachLine(",") { fields ->
                County county = new County(
                        region: region,
                        name: fields[0],
                        i18nCode: fields[1]
                )
                if (county.validate() && county.save()) {
                    log.debug("saved ${county.name} with id: ${county.id}!")
                } else {
                    county.errors.allErrors.each { log.error it }
                }
            }
        }
    }

    def initMunicipalities(){

        BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                        new FileInputStream("data/municipalities_norway.csv"
                        ), StandardCharsets.UTF_8)
        );
        String line = "";
        String cvsSplitBy = ",";
        long linesRead = 0;

        SearchIndexCreator attrValIdx = SearchIndexCreator.instance
        attrValIdx
                .setIndexName(SearchIndexCreator.MUNICIPALITY_IDX_NAME)
                .setMunicipalityMapping()
                .deleteIndex()
                .createIndexIfNotExists()


        Map<String, County> counties = new HashMap<>()
        counties.put('Østfold', County.findByNameLike('Østfold'))
        counties.put('Akershus', County.findByNameLike('Akershus'))
        counties.put('Oslo', County.findByNameLike('Oslo'))
        counties.put('Hedmark', County.findByNameLike('Hedmark'))
        counties.put('Oppland', County.findByNameLike('Oppland'))
        counties.put('Buskerud', County.findByNameLike('Buskerud'))
        counties.put('Vestfold', County.findByNameLike('Vestfold'))
        counties.put('Telemark', County.findByNameLike('Telemark'))
        counties.put('Aust-Agder', County.findByNameLike('Aust-Agder'))
        counties.put('Vest-Agder', County.findByNameLike('Vest-Agder'))
        counties.put('Rogaland', County.findByNameLike('Rogaland'))
        counties.put('Hordaland', County.findByNameLike('Hordaland'))
        counties.put('Sogn og Fjordane',
                County.findByNameLike('Sogn og Fjordane'))
        counties.put('Møre og Romsdal',
                County.findByNameLike('Møre og Romsdal'))
        counties.put('Sør-Trøndelag',
                County.findByNameLike('Sør-Trøndelag'))
        counties.put('Nord-Trøndelag',
                County.findByNameLike('Nord-Trøndelag'))
        counties.put('Nordland', County.findByNameLike('Nordland'))
        counties.put('Troms', County.findByNameLike('Troms'))
        counties.put('Finnmark', County.findByNameLike('Finnmark'))

        log.debug(counties.get("Oslo"))


        Attribute admSenter = new Attribute(label: "Adm. senter")
        admSenter.save()
        BulkAttrInsert.index(admSenter)

        Attribute folkeTall = new Attribute(label: "Folketall")
        folkeTall.save()
        BulkAttrInsert.index(folkeTall)

        Attribute areal = new Attribute(label: "Areal")
        areal.save()
        BulkAttrInsert.index(areal)

        Attribute maalform = new Attribute(label: "Målform")
        maalform.save()
        BulkAttrInsert.index(maalform)

        Attribute ordforer = new Attribute(label: "Ordfører")
        ordforer.save()
        BulkAttrInsert.index(ordforer)

        Attribute parti = new Attribute(label: "Parti")
        parti.save()
        BulkAttrInsert.index(parti)

        while ((line = reader.readLine()) != null){
            String[] fields = line.split(cvsSplitBy);
            if(fields[0].length() == 3){
                fields[0] = "0" + fields[0]
            }
            Municipality municipality = new Municipality(
                    number: fields[0],
                    county: counties.get(fields[3]),
                    name: fields[1],
            )
            if (municipality.validate() && municipality.save()) {
                log.debug("saved ${municipality.name} " +
                        "with id: ${municipality.id}")

                BulkAttrInsert.index(
                        municipality,
                        SearchIndexCreator.MUNICIPALITY_IDX_NAME
                )

                attributeService.saveAttribute(
                        municipality,
                        admSenter,
                        fields[2]
                )

                attributeService.saveAttribute(
                        municipality,
                        folkeTall,
                        Long.valueOf(fields[4])
                )

                attributeService.saveAttribute(
                        municipality,
                        areal,
                        Long.valueOf(fields[5])
                )

                attributeService.saveAttribute(
                        municipality,
                        maalform,
                        fields[8]
                )
                attributeService.saveAttribute(
                        municipality,
                        ordforer,
                        fields[9]
                )

                attributeService.saveAttribute(
                        municipality,
                        parti,
                        fields[10]
                )

                InitImages.LOGOLIST.add(
                        new InitImages(municipality.id, new URL(fields[7]))
                )
            } else {
                municipality.errors.allErrors.each { log.error it }
            }
        }
        log.info("Done inserting Municipalities")
        log.info("Flushing....")
    }

    def insertImages(){
        for(InitImages ii : InitImages.LOGOLIST){
            Municipality municipality = Municipality.get(ii.id)
            byte[] bytes = DownloadHelper.downloadFile(ii.url as URL)

            String filename = municipality.number + "_" + municipality.name
            long imageId = createImage(filename, bytes)
            municipality.logo = Image.get(imageId)
            municipality.save()
        }
        log.debug("Done inserting images...")
    }

    def long createImage(String filename, byte[] bytes){
        TikaConfig config = TikaConfig.defaultConfig;
        InputStream stream = new ByteArrayInputStream(bytes);

        MediaType mediaType = config
                .getMimeRepository()
                .detect(stream, new Metadata())
        MimeType mimeType = config
                .getMimeRepository()
                .forName(mediaType.toString())

        String extension = mimeType.getExtension()
        String contentType = mimeType.name

        long imageId = imageService.createImage(
                filename + extension,
                contentType,
                bytes
        )
        return imageId
    }

    @Selector('attr.doattrsinsert.event')
    @NotTransactional
    void doAttrsInsert(Object data){
        log.debug("Inserting ${BulkAttrInsert.attrList.size()} " +
                "attributes to elasticsearch.")
        Bulk bulk = new Bulk.Builder()
                .addAction(BulkAttrInsert.attrList)
                .build();

        JestClient client = JestPool.client
        JestResult result = client.execute(bulk);
        if (!result.succeeded) {
            log.error(result.jsonString)
            throw new RuntimeException(result.errorMessage)
        }
        BulkAttrInsert.attrList.clear()
        log.debug("Done inserting attributes to elasticsearch!")
    }

    @Selector('attr.doattrvalsinsert.event')
    @NotTransactional
    void doAttrValsInsert(Object data){
        log.debug("Inserting ${BulkAttrInsert.attrValList.size()} " +
                "attributes to elasticsearch.")

        List<Index> sublist = new ArrayList<>()
        for(int i = 0; i < BulkAttrInsert.attrValList.size(); i++){
            sublist.add(BulkAttrInsert.attrValList.get(i))
            if( (i % 5000) == 0){
                doBulkInsert(sublist);
                sublist.clear()
            }
        }
        doBulkInsert(sublist);

        BulkAttrInsert.attrValList.clear()
        log.debug("Done inserting attribute values to elasticsearch!")
    }

    private doBulkInsert(List<Index> items){
        Bulk bulk = new Bulk.Builder()
                .addAction(items)
                .build();

        JestClient client = JestPool.client
        JestResult result = client.execute(bulk);
        if (!result.succeeded) {
            log.error(result.jsonString)
            throw new RuntimeException(result.errorMessage)
        }
    }

    public <T> List<List<T>> split(List<T> list, int size)
            throws NullPointerException, IllegalArgumentException {
        if (list == null) {
            throw new NullPointerException("The list parameter is null.");
        }

        if (size <= 0) {
            throw new IllegalArgumentException(
                    "The size parameter must be more than 0.");
        }

        List<List<T>> result = new ArrayList<List<T>>(size);

        for (int i = 0; i < size; i++) {
            result.add(new ArrayList<T>());
        }

        int index = 0;

        for (T t : list) {
            result.get(index).add(t);
            index = (index + 1) % size;
        }

        return result;
    }

    @Selector('attr.domunicipalityinsert.event')
    @NotTransactional
    void doMunicipalityInsert(Object data){
        log.debug("Inserting ${BulkAttrInsert.municipalities.size()} " +
                "municipalities to elasticsearch.")
        Bulk bulk = new Bulk.Builder()
                .addAction(BulkAttrInsert.municipalities)
                .build();

        JestClient client = JestPool.client
        JestResult result = client.execute(bulk);
        if (!result.succeeded) {
            log.error(result.jsonString)
            throw new RuntimeException(result.errorMessage)
        }
        BulkAttrInsert.municipalities.clear()
        log.debug("Done inserting municipalities to elasticsearch!")
    }

    def insertFolkOgEndringer(){


    }
}
