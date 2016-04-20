package ms.enterprise.filters

import grails.util.Environment


class LogRequestInterceptor {

    int order = 0

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";

    LogRequestInterceptor(){
        matchAll()
    }

    boolean before() {
        if(Environment.currentEnvironment.equals(Environment.DEVELOPMENT)) {
            println "URL: " +
                    ANSI_BLUE + "${request.scheme}://${request.serverName}" +
                    ":${request.serverPort}${request.requestURI}" + ANSI_RESET

            println   "Calling controller:$ANSI_RED $controllerName " +
                    "$ANSI_RESET and action:$ANSI_RED $actionName" +
                    ANSI_RESET

            println "Method: $ANSI_YELLOW $request.method" + ANSI_RESET

            println "Parameters:" + ANSI_RESET

            request.getParameterNames().each{
                println "   Name: $ANSI_PURPLE" + it +
                        "$ANSI_RESET | Value:$ANSI_RED " +
                        "${request.getParameterValues(it)}" +
                        ANSI_RESET
            }
            /*println "Headers:"
            request.getHeaderNames().each{
                println "    Name: " + it +
                          " | Value: ${request.getHeader(it)}"
            }
            println request.getServletPath()*/
        }
        true
    }

    boolean after() { true }

    void afterView() {
        // no-op
    }
}
