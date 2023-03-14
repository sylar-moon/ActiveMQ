package my.group;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyLogger {
    private String pathLogger = "logback.xml";

    MyLogger(String pathLogger) {
        this.pathLogger = pathLogger;
    }

    MyLogger() {
    }

    public String getPathLogger() {
        return pathLogger;
    }

    public void setPathLogger(String pathLogger) {
        this.pathLogger = pathLogger;
    }

    public Logger getLogger() {
        System.setProperty("logback.configurationFile", pathLogger);
        return LoggerFactory.getLogger(App.class);
    }
}
