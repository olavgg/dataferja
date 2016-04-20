import grails.compiler.GrailsCompileStatic
import ms.enterprise.MainInitService

@GrailsCompileStatic
class BootStrap {

    MainInitService mainInitService

    def init = { servletContext ->
        mainInitService.init()
    }
    def destroy = {
    }
}
