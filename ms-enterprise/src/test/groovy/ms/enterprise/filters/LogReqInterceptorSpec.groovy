package ms.enterprise.filters


import grails.test.mixin.TestFor
import spock.lang.Specification

/**
 * See the API for {@link grails.test.mixin.web.ControllerUnitTestMixin} for usage instructions
 */
@TestFor(LogReqInterceptor)
class LogReqInterceptorSpec extends Specification {

    def setup() {
    }

    def cleanup() {

    }

    void "Test logReq interceptor matching"() {
        when:"A request matches the interceptor"
            withRequest(controller:"logReq")

        then:"The interceptor does match"
            interceptor.doesMatch()
    }
}
