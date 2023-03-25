package my.group;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.pool.PooledConnectionFactory;
import org.slf4j.Logger;


import javax.jms.*;

public class Broker {
    private static final Logger LOGGER = new MyLogger().getLogger();

    void
    sendMessage(String nameQueue, String message, Session producerSession) throws JMSException {

        // Create a queue named "MyQueue".
        final Destination producerDestination = producerSession
                .createQueue(nameQueue);

        // Create a producer from the session to the queue.
        final MessageProducer producer = producerSession
                .createProducer(producerDestination);
        producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

        // Create a message.
        final TextMessage producerMessage = producerSession
                .createTextMessage(message);

        // Send the message.
        producer.send(producerMessage);
        LOGGER.info("Message send: {}", message);
        // Clean up the producer.
        producer.close();
    }


    PooledConnectionFactory
    createPooledConnectionFactory(ActiveMQConnectionFactory connectionFactory) {
        // Create a pooled connection factory.
        final PooledConnectionFactory factory =
                new PooledConnectionFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMaxConnections(10);
        return factory;
    }

    ActiveMQConnectionFactory createActiveMQConnectionFactory(DataToConnectActiveMQ connectActiveMQ) {
        // Create a connection factory.
        final ActiveMQConnectionFactory connectionFactory =
                new ActiveMQConnectionFactory(connectActiveMQ.getEndPoint());

        // Pass the sign-in credentials.
        connectionFactory.setUserName(connectActiveMQ.getUsername());
        connectionFactory.setPassword(connectActiveMQ.getPassword());
        return connectionFactory;
    }

}
