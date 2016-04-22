package ms.enterprise

import ms.enterprise.helpers.BufferedCsvReader

class DefaultController {

    def index() { }
    def test() {
        BufferedCsvReader bcr = new BufferedCsvReader()
        bcr.readFolkeMengdeOgBVekst()
    }
}
