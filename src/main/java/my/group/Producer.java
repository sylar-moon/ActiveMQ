package my.group;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.pool.PooledConnectionFactory;
import org.slf4j.Logger;

import javax.jms.*;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class Producer implements Callable<RPS> {
    private static final Logger LOGGER = new MyLogger().getLogger();
    private final long time;
    private final int numberObjects;
    private final String nameQueue;
    private final String poisonPill;
    JsonConverter converter = new JsonConverter();
    Broker broker = new Broker();
    PooledConnectionFactory pooledConnectionFactory;
    RPS rps = new RPS();
    private final Session producerSession;
    private final Connection connection;
    private final MessageProducer messageProducer;

    public Producer(long time, String nameQueue, DataToConnectActiveMQ data, int numberObjects, String poisonPill) throws JMSException {
        this.time = time;
        this.nameQueue = nameQueue;
        this.numberObjects = numberObjects;
        this.poisonPill = poisonPill;
        ActiveMQConnectionFactory activeMQConnectionFactory = broker.createActiveMQConnectionFactory(data);
        this.pooledConnectionFactory = broker.createPooledConnectionFactory(activeMQConnectionFactory);
        this.connection = pooledConnectionFactory.createConnection();
        connection.start();
        this.producerSession = connection
                .createSession(false, Session.AUTO_ACKNOWLEDGE);
        Destination producerDestination = producerSession
                .createQueue(nameQueue);
        this.messageProducer = producerSession
                .createProducer(producerDestination);
        messageProducer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
    }

    @Override
    public RPS call() {
        rps.startWatch();
        Supplier<Stream<Person>> supplier = () -> new PersonFactory().createStreamRandomPerson();
        while (rps.getTimeSecond() < time && rps.getCount() < numberObjects) {
            Person person = getPerson(supplier);
            String json = converter.createJsonFromObjects(person);
            sendMessage(json);
            rps.incrementCount();
        }
        rps.stopWatch();
        sendMessage(poisonPill);
        stopConnectActiveMQ(connection);
        return rps;
    }

    private void stopConnectActiveMQ(Connection connection) {
        try {
            messageProducer.close();
            producerSession.close();
            connection.close();
            pooledConnectionFactory.stop();
        } catch (JMSException e) {
            LOGGER.error("Unable to stop connection to ActiveMQ", e);
        }
    }

    private Person getPerson(Supplier<Stream<Person>> supplier) {
        Optional<Person> optionalPerson = supplier.get().findAny();
        return optionalPerson.orElse(null);
    }

    private void sendMessage(String message) {
        try {
            final TextMessage producerMessage = producerSession
                    .createTextMessage(message);

            // Send the message.
            messageProducer.send(producerMessage);
            LOGGER.info("Message send: {}", message);
        } catch (JMSException e) {
            LOGGER.error("Unable to send message: {}", message, e);
        }
    }

}