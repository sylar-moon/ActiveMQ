package my.group;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.pool.PooledConnectionFactory;
import org.slf4j.Logger;

import javax.jms.Connection;
import javax.jms.JMSException;
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
        Supplier<Stream<Person>> supplier = () -> new PersonFactory().createStreamRandomPerson();
        while (rps.getTimeSecond() < time && rps.getCount() < numberObjects) {
            Person person = getPerson(supplier);
            String json = converter.createJsonFromObjects(person);
            sendMessage(json, connection);
            rps.incrementCount();
        }
        rps.stopWatch();
        LOGGER.info("Producer indicators: count= {} time={} rps={}", (rps.getCount()), rps.getTimeSecond(), rps.getRPS());
        sendMessage(poisonPill, connection);
        stopConnectActiveMQ(connection);
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

    private void sendMessage(String message, Connection connection) {
        try {
            broker.sendMessage(nameQueue, message, connection);
        } catch (JMSException e) {
            LOGGER.error("Unable to send message: {}", message, e);
        }
    }

}