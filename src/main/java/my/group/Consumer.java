package my.group;

import com.opencsv.CSVWriter;
import org.apache.activemq.ActiveMQConnection;
import org.slf4j.Logger;

import javax.jms.*;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

public class Consumer implements Runnable {
    private static final Logger LOGGER = new MyLogger().getLogger();
    private final String nameQueue;
    private final String poisonPill;
    private final MyValidator validator = new MyValidator();
    private final String invalidPersonsCsv;
    private final String validPersonsCsv;
    private final JsonConverter converter = new JsonConverter();
    private final Broker broker = new Broker();
    private final RPS rps = new RPS();
    private final Connection connection;
    private final Session session;
    private final MessageConsumer messageConsumer;

    public Consumer(String nameQueue, DataToConnectActiveMQ data, String poisonPill, String pathValidPersonsCsv, String pathInvalidPersonsCsv) throws JMSException {
        this.nameQueue = nameQueue;
        this.poisonPill = poisonPill;
        this.validPersonsCsv = pathValidPersonsCsv;
        this.invalidPersonsCsv = pathInvalidPersonsCsv;
        this.connection = broker.createActiveMQConnectionFactory(data).createConnection();
        connection.start();
        this.session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        Queue queue = session
                .createQueue(nameQueue);
        this.messageConsumer = session
                .createConsumer(queue);

    }


    @Override
    public void run() {
        rps.startWatch();
        validator.validateCsvFormat(invalidPersonsCsv);
        validator.validateCsvFormat(validPersonsCsv);
        try (CSVWriter invalidWriter = new CSVWriter(new FileWriter(invalidPersonsCsv));
             CSVWriter validWriter = new CSVWriter(new FileWriter(validPersonsCsv))
        ) {

            while (true) {
                String message = receiveMessage();
                if(message==null){continue;}
                if (message.equals(poisonPill)) {
                    break;
                }
                rps.incrementCount();
                Person person = converter.createPersonFromJson(message);
                String[] errors = validator.validatePerson(person);
                if (errors.length == 0) {
                    validWriter.writeNext(new String[]{person.getName(), String.valueOf(person.getCount())});
                } else {
                    String error = converter.createJsonFromObjects(new Error(Arrays.toString(errors)));
                    invalidWriter.writeNext(new String[]{person.getName(), String.valueOf(person.getCount()), error});
                }
            }

        } catch (IOException e) {
            LOGGER.error("Invalid csv file", e);
        } catch (JMSException e) {
            e.printStackTrace();
        } finally {
            closeActiveMQ();
            rps.stopWatch();
        }

        LOGGER.info("Consumer indicators: count= {} time={} rps={}", (rps.getCount()), rps.getTimeSecond(), rps.getRPS());

    }

    private void closeActiveMQ() {
        try {
            messageConsumer.close();
            session.close();
            connection.stop();
            connection.close();
        } catch (JMSException e) {
            LOGGER.error("Unable close activeMQ consumer connection", e);
        }

    }



    String
    receiveMessage() throws JMSException {

        try {
            // Begin to wait for messages.
            Message consumerMessage = messageConsumer.receive();
            LOGGER.info("Message received: {}", consumerMessage);
            String text = ((TextMessage) consumerMessage).getText();
            LOGGER.info("Message received: {}", text);
            return text;

        } catch (NullPointerException e) {
            LOGGER.error("Queue is empty", e);
            System.exit(4);
        }
        return null;
    }


}
