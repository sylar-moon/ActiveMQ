package my.group;

import org.slf4j.Logger;

import java.io.IOException;

/**
 * This program reads the ActiveMQ login data from the config.properties file and sends it to the queue
 * a given number of randomly created Person objects, after which it reads them from the queue and validates.
 * If the name of the Person object does not contain the letter a, or the length of the name is shorter than 7 characters, or count < 10
 * then the data of the Person object is written to PATH_INVALID_PERSONS_CSV, and if the object matches these
 * characteristics, it is written to the PATH_VALID_PERSONS_CSV file.
 * The time to send messages is set by the launch argument in seconds, after which the message will be sent
 * to stop reading messages
 */

public class App {
    private static final Logger LOGGER = new MyLogger().getLogger();
    private static final MyProperties PROPERTIES = new MyProperties();
    //Message to stop reading messages
    private static final String POISON_PILL = "End sending messages";
    private static final String PATH_INVALID_PERSONS_CSV = "invalidPersons.csv";
    private static final String PATH_VALID_PERSONS_CSV = "validPersons.csv";
    //Set in seconds
    public static final Long DEFAULT_TIME = 10L;

    public static void main(String[] args) {
        readPropertyFile();
        String endPoint = PROPERTIES.getProperty("sslActiveMQ");
        String userName = PROPERTIES.getProperty("username");
        String password = PROPERTIES.getProperty("password");
        String nameQueue = PROPERTIES.getProperty("nameQueue");
        //If arguments are not set then use DEFAULT_TIME
        long messageSendingTime = args.length==0?DEFAULT_TIME:Long.parseLong(args[0]);
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
