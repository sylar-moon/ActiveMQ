package my.group;

import org.slf4j.Logger;

import java.io.IOException;

public class App {
    private static final Logger LOGGER = new MyLogger().getLogger();
    private static final MyProperties PROPERTIES = new MyProperties();
    private static final String POISON_PILL = "End sending messages";
    private static final String PATH_INVALID_PERSONS_CSV = "invalidPersons.csv";
    private static final String PATH_VALID_PERSONS_CSV = "validPersons.csv";

    public static void main(String[] args) {
        readPropertyFile();
        String endPoint = PROPERTIES.getProperty("sslActiveMQ");
        String userName = PROPERTIES.getProperty("username");
        String password = PROPERTIES.getProperty("password");
        String nameQueue = PROPERTIES.getProperty("nameQueue");

        long messageSendingTime = Long.parseLong(args[0]);
        int numberObjects = Integer.parseInt(PROPERTIES.getProperty("numberObjects"));
        Broker broker = new Broker(endPoint, userName, password);
        Thread producer = new Thread(new Producer(messageSendingTime, nameQueue, broker, numberObjects,POISON_PILL));
        producer.start();
        Thread consumer = new Thread(new Consumer(nameQueue, broker,POISON_PILL,PATH_VALID_PERSONS_CSV,PATH_INVALID_PERSONS_CSV));
        consumer.start();
    }

    private static void readPropertyFile() {
        try {
            PROPERTIES.readPropertyFile();
        } catch (IOException e) {
            LOGGER.error("Properties file not found");
        }
    }

}
