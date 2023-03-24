package my.group;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.pool.PooledConnectionFactory;
import org.slf4j.Logger;

import javax.jms.*;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class Producer implements Runnable {
    private static final Logger LOGGER = new MyLogger().getLogger();
    private final long time;
    private final int numberObjects;
    private final String nameQueue;
    private final String poisonPill;
    JsonConverter converter = new JsonConverter();
    Broker broker = new Broker();
    PooledConnectionFactory pooledConnectionFactory;
    RPS rps = new RPS();

    public Producer(long time, String nameQueue, DataToConnectActiveMQ data, int numberObjects, String poisonPill) {
        this.time = time;
        this.nameQueue = nameQueue;
        this.numberObjects = numberObjects;
        this.poisonPill = poisonPill;
        ActiveMQConnectionFactory activeMQConnectionFactory = broker.createActiveMQConnectionFactory(data);
        this.pooledConnectionFactory = broker.createPooledConnectionFactory(activeMQConnectionFactory);
    }

    @Override
    public void run() {
        rps.startWatch();
        Connection connection = connectToActiveMQ();
        Session producerSession = creatSession(connection);
        MessageProducer producer = createMessageProducer(producerSession);
        Supplier<Stream<Person>> supplier = () -> new PersonFactory().createStreamRandomPerson();
        while (rps.getTimeSecond() < time && rps.getCount() < numberObjects) {
            Person person = getPerson(supplier);
            String json = converter.createJsonFromObjects(person);
            broker.sendMessage(json,producerSession, producer);
            rps.incrementCount();
        }
        rps.stopWatch();
        LOGGER.info("Producer indicators: count= {} time={} rps={}", (rps.getCount()), rps.getTimeSecond(), rps.getRPS());
        broker.sendMessage(poisonPill, producerSession, producer);
        stopConnectActiveMQ(connection);
        closeSession(producerSession);
        closeProducer(producer);
    }

    private void closeProducer(MessageProducer producer) {
        try {
            producer.close();
        } catch (JMSException e) {
            LOGGER.error("Unable to close Message Producer",e);
            System.exit(0);
        }
    }

    private MessageProducer createMessageProducer(Session producerSession) {
        try {
            // Create a queue
            Queue producerQueue = producerSession
                    .createQueue(nameQueue);
            // Create a producer from the session to the queue.
            final MessageProducer producer = producerSession
                    .createProducer(producerQueue);
            producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
            return producer;
        } catch (JMSException e) {
            LOGGER.error("Unable to create Message Producer",e);
            System.exit(0);
        }
        return null;
    }

    private void closeSession(Session producerSession) {
        try {
            producerSession.close();
        } catch (JMSException e) {
            LOGGER.error("Unable to close session", e);
        }
    }

    private Session creatSession(Connection connection) {
        try {
            return connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        } catch (JMSException e) {
            LOGGER.error("Unable to create session", e);
            System.exit(0);
        }
        return null;
    }

    private void stopConnectActiveMQ(Connection connection) {
        try {
            connection.stop();
            connection.close();
            pooledConnectionFactory.stop();
        } catch (JMSException e) {
            LOGGER.error("Unable to stop connection to ActiveMQ", e);
        }
    }

    private Connection connectToActiveMQ() {
        try {
            Connection connection = pooledConnectionFactory.createConnection();
            connection.start();
            return connection;
        } catch (JMSException e) {
            LOGGER.error("Unable to connect to ActiveMQ", e);
            System.exit(0);
        }
        return null;
    }


    private Person getPerson(Supplier<Stream<Person>> supplier) {
        Optional<Person> optionalPerson = supplier.get().findAny();
        return optionalPerson.orElse(null);

    }

}