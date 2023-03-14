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
    private static final String POISON_PILL = "End sending messages";
    MyValidator validator = new MyValidator();
    private static final String INVALID_PERSONS_CSV = "invalidPersons.csv";
    private static final String VALID_PERSONS_CSV = "validPersons.csv";
    private final JsonConverter converter = new JsonConverter();

    Consumer(String nameQueue, Broker broker) {
        this.nameQueue = nameQueue;
        this.broker = broker;
    }

    @Override
    public void run() {
        try (
                CSVWriter invalidWriter = new CSVWriter(new FileWriter(INVALID_PERSONS_CSV));
                CSVWriter validWriter = new CSVWriter(new FileWriter(VALID_PERSONS_CSV))
        ) {

            while (true) {
                String message = broker.receiveMessage(nameQueue);
                if (message.equals(POISON_PILL)) {
                    break;
                }
                Person person = converter.createPersonFromJson(message);
                String[] errors = validator.validatePerson(person);
                if(errors.length==0){
                    validWriter.writeNext(new String[]{person.getName(),String.valueOf(person.getCount())});
                }else {
                    invalidWriter.writeNext(new String[]{person.getName(),String.valueOf(person.getCount()),"errors: ", Arrays.toString(errors)});
                }
            }

        } catch (IOException e) {
            LOGGER.error("Invalid csv file", e);
        } catch (JMSException e) {
            LOGGER.error("Unable to receive message",e);
        }
    }
}
