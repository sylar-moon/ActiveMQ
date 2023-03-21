package my.group;

import com.opencsv.CSVWriter;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.slf4j.Logger;

import javax.jms.Connection;
import javax.jms.JMSException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

public class Consumer implements Runnable {
    private static final Logger LOGGER = new MyLogger().getLogger();
    private final String nameQueue;
    private final DataToConnectActiveMQ data;
    private final String poisonPill;
    private final MyValidator validator = new MyValidator();
    private final String invalidPersonsCsv;
    private final String validPersonsCsv;
    private final JsonConverter converter = new JsonConverter();
    private final Broker broker = new Broker();
    private final RPS rps = new RPS();


    public Consumer(String nameQueue, DataToConnectActiveMQ data, String poisonPill, String pathValidPersonsCsv, String pathInvalidPersonsCsv) {
        this.nameQueue = nameQueue;
        this.data = data;
        this.poisonPill = poisonPill;
        this.validPersonsCsv = pathValidPersonsCsv;
        this.invalidPersonsCsv = pathInvalidPersonsCsv;
    }

    @Override
    public void run() {
        Connection connection = connectToActiveMQ(data);
        rps.startWatch();
        validator.validateCsvFormat(invalidPersonsCsv);
        validator.validateCsvFormat(validPersonsCsv);
        try (CSVWriter invalidWriter = new CSVWriter(new FileWriter(invalidPersonsCsv));
             CSVWriter validWriter = new CSVWriter(new FileWriter(validPersonsCsv))) {

            while (true) {
                String message = broker.receiveMessage(nameQueue, connection);
                if (message.equals(poisonPill)) {
                    break;
                }
                rps.incrementCount();
//                Person person = converter.createPersonFromJson(message);
//                String[] errors = validator.validatePerson(person);
//                if (errors.length == 0) {
//                    validWriter.writeNext(new String[]{person.getName(), String.valueOf(person.getCount())});
//                } else {
//                    String error = converter.createJsonFromObjects(new Error(Arrays.toString(errors)));
//                    invalidWriter.writeNext(new String[]{person.getName(), String.valueOf(person.getCount()), error});
//                }
            }

        } catch (IOException e) {
            LOGGER.error("Invalid csv file", e);
        } catch (JMSException e) {
            LOGGER.error("Unable to receive message", e);
        }
        rps.stopWatch();
        stopConnectActiveMQ(connection);
        LOGGER.info("Consumer indicators: count= {} time={} rps={}", (rps.getCount()), rps.getTimeSecond(), rps.getRPS());

    }


    private Connection connectToActiveMQ(DataToConnectActiveMQ data) {
        try {
            Connection connection = broker.createActiveMQConnectionFactory(data).createConnection();
            connection.start();
            return connection;
        } catch (JMSException e) {
            LOGGER.error("Unable to connect to ActiveMQ", e);
            System.exit(0);
        }
        return null;
    }


    private void stopConnectActiveMQ(Connection connection) {
        try {
            connection.stop();
            connection.close();
        } catch (JMSException e) {
            LOGGER.error("Unable to stop connection to ActiveMQ", e);
        }
    }
}
