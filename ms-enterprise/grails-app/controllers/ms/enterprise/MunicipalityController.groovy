package ms.enterprise

import grails.compiler.GrailsCompileStatic
import grails.converters.JSON

@GrailsCompileStatic
class MunicipalityController {

    def index() { }

    def show(Long id){
        String returnFormat = request.getHeader("Accept");
        Municipality municipality = Municipality.get(id);
        if(municipality){
            if(returnFormat.contains('application/json')){
                def results = [:]
                results.id = municipality.id
                results.name = municipality.name
                results.image = municipality.logo.uriName
                render results as JSON
                return
            }
        }
        render text: 'Not found', status: 404
    }
}
