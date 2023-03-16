package my.group;

import com.opencsv.CSVWriter;
import org.slf4j.Logger;

import javax.jms.JMSException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

public class Consumer implements Runnable {
    private static final Logger LOGGER = new MyLogger().getLogger();
    private final String nameQueue;
    private final Broker broker;
    private final String poisonPill;
    private final MyValidator validator = new MyValidator();
    private final String invalidPersonsCsv;
    private final String validPersonsCsv;
    private final JsonConverter converter = new JsonConverter();

    public Consumer(String nameQueue, Broker broker, String poisonPill, String pathValidPersonsCsv, String pathInvalidPersonsCsv) {
        this.nameQueue = nameQueue;
        this.broker = broker;
        this.poisonPill = poisonPill;
        this.validPersonsCsv = pathValidPersonsCsv;
        this.invalidPersonsCsv = pathInvalidPersonsCsv;
    }

    @Override
    public void run() {
        validator.validateCsvFormat(invalidPersonsCsv);
        validator.validateCsvFormat(validPersonsCsv);
        try (CSVWriter invalidWriter = new CSVWriter(new FileWriter(invalidPersonsCsv));
             CSVWriter validWriter = new CSVWriter(new FileWriter(validPersonsCsv))) {

            while (true) {
                String message = broker.receiveMessage(nameQueue);
                if (message.equals(poisonPill)) {
                    break;
                }
                Person person = converter.createPersonFromJson(message);
                String[] errors = validator.validatePerson(person);
                if (errors.length == 0) {
                    validWriter.writeNext(new String[]{person.getName(), String.valueOf(person.getCount())});
                } else {
                    String error =  converter.createJsonFromObjects(new Errors(Arrays.toString(errors)));
                    invalidWriter.writeNext(new String[]{person.getName(), String.valueOf(person.getCount()), error });
                }
            }

        } catch (IOException e) {
            LOGGER.error("Invalid csv file", e);
        } catch (JMSException e) {
            LOGGER.error("Unable to receive message", e);
        }
    }
}
