package my.group;

import org.slf4j.Logger;

import java.io.IOException;

public class App {
    private static final Logger LOGGER = new MyLogger().getLogger();
    private static final String PROPERTIES_PATH = "config.properties";
    private static final MyProperties PROPERTIES = new MyProperties();
    public static final PersonFactory POJO_FACTORY = new PersonFactory();

    public static void main(String[] args) {
        readPropertyFile();
        String endPoint = PROPERTIES.getProperty("sslActiveMQ");
        String userName = PROPERTIES.getProperty("username");
        String password = PROPERTIES.getProperty("password");
        String nameQueue = PROPERTIES.getProperty("nameQueue");

        long messageSendingTime = Long.parseLong(args[0]);
        int numberObjects = Integer.parseInt(PROPERTIES.getProperty("numberObjects"));
        Broker broker = new Broker(endPoint, userName, password);
        Thread producer = new Thread(new Producer(messageSendingTime, nameQueue, broker, numberObjects));
        producer.start();
        Thread consumer = new Thread(new Consumer(nameQueue, broker));
        consumer.start();
    }

    private static void readPropertyFile() {
        try {
            PROPERTIES.readPropertyFile(PROPERTIES_PATH);
        } catch (IOException e) {
            LOGGER.error("Properties file not found");
        }
    }

}
