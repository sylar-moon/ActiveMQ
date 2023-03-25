package my.group;

import org.slf4j.Logger;

import javax.jms.JMSException;
import java.io.IOException;
import java.util.concurrent.*;

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
    private static final RPS[] RPS = new RPS[2];

    public static void main(String[] args) {
        readPropertyFile();
        String endPoint = PROPERTIES.getProperty("sslActiveMQ");
        String userName = PROPERTIES.getProperty("username");
        String password = PROPERTIES.getProperty("password");
        DataToConnectActiveMQ dataToConnect = new DataToConnectActiveMQ(endPoint, userName, password);
        String nameQueue = PROPERTIES.getProperty("nameQueue");
        //If arguments are not set then use DEFAULT_TIME
        long messageSendingTime = args.length == 0 ? DEFAULT_TIME : Long.parseLong(args[0]);
        int numberObjects = Integer.parseInt(PROPERTIES.getProperty("numberObjects"));
        try {
            RPS[0] = getRpsFromCallable(new Producer(messageSendingTime, nameQueue, dataToConnect, numberObjects, POISON_PILL));
        } catch (JMSException e) {
            LOGGER.error("Unable to get result from rps producer", e);
        }

        try {
            RPS[1] = getRpsFromCallable(new Consumer(nameQueue, dataToConnect, POISON_PILL, PATH_VALID_PERSONS_CSV, PATH_INVALID_PERSONS_CSV));
        } catch (JMSException e) {
            LOGGER.error("Unable to get result from rps consumer", e);
        }

        printRPS();

    }

    private static RPS getRpsFromCallable(Callable<RPS> callable) {
        ExecutorService service = Executors.newSingleThreadExecutor();
        try {
            Future<RPS> future = service.submit(callable);
            service.shutdown();
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            LOGGER.error("Unable to get result from rps producer", e);
            System.exit(0);
        } finally {
            service.shutdown();
        }
        return null;
    }

    private static void printRPS() {
        LOGGER.info("Producer indicators: count= {} time={} rps={}", (RPS[0].getCount()), RPS[0].getTimeSecond(), RPS[0].getRPS());
        LOGGER.info("Consumer indicators: count= {} time={} rps={}", (RPS[1].getCount()), RPS[1].getTimeSecond(), RPS[1].getRPS());

    }

    private static void readPropertyFile() {
        try {
            PROPERTIES.readPropertyFile();
        } catch (IOException e) {
            LOGGER.error("Properties file not found");
        }
    }

}
