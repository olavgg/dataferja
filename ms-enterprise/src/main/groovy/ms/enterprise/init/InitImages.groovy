package ms.enterprise.init

import groovy.transform.CompileStatic


@CompileStatic
class InitImages {

    public static List<InitImages> LOGOLIST = new ArrayList<>()

    long id
    URL url

    public InitImages(long id, URL url){
        this.id = id
        this.url = url
    }

}
