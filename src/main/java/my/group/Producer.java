package my.group;

import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;

import javax.jms.JMSException;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class Producer implements Runnable {
    private static final Logger LOGGER = new MyLogger().getLogger();
    private final long time;
    private final int numberObjects;
    private final String nameQueue;
    private final Broker broker;
    private static final String POISON_PILL = "End sending messages";
    JsonConverter converter = new JsonConverter();

    public Producer(long time, String nameQueue, Broker broker, int numberObjects) {
        this.time = time;
        this.nameQueue = nameQueue;
        this.broker = broker;
        this.numberObjects = numberObjects;
    }

    public static void main(String[] args) {
        int time = 10;
    StopWatch watch = StopWatch.createStarted();
    while(watch.getTime(TimeUnit.SECONDS)<time){
        LOGGER.info(String.valueOf(watch.getTime(TimeUnit.SECONDS)));
    }
    }

    @Override
    public void run() {
        int counter = 0;
        StopWatch watch = StopWatch.createStarted();

        long timeStopSendingMessage = System.currentTimeMillis() + (time * 1000);//stopwath
        Supplier<Stream<Person>> supplier = () -> new PersonFactory().createStreamRandomPerson();

        while ( watch.getTime(TimeUnit.SECONDS)<time&&counter < numberObjects) {
            Person person = getPerson(supplier);
            String json = converter.createJsonFromObjects(person);
            String sendMessage = System.currentTimeMillis() >= timeStopSendingMessage ? POISON_PILL : json;

            sendMessage(sendMessage);
            counter++;
        }
        sendMessage(POISON_PILL);
        broker.stopPooledConnectionFactory();
    }

    private Person getPerson(Supplier<Stream<Person>> supplier) {
        Optional<Person> optionalPerson = supplier.get().findAny();
        return optionalPerson.orElse(null);

    }

    private void sendMessage(String message) {
        try {
            broker.sendMessage(nameQueue, message);
        } catch (JMSException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }


}
