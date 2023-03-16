package my.group;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.pool.PooledConnectionFactory;
import org.slf4j.Logger;


import javax.jms.*;

public class Broker {
    private final PooledConnectionFactory pooledConnectionFactory;
    private final ActiveMQConnectionFactory mqConnectionFactory;
    private static final Logger LOGGER = new MyLogger().getLogger();

    public Broker(String endpoint, String username, String password) {
        this.mqConnectionFactory = createActiveMQConnectionFactory(endpoint, username, password);
        this.pooledConnectionFactory = createPooledConnectionFactory(mqConnectionFactory);
    }

    void
    sendMessage(String nameQueue, String message) throws JMSException {
        // Establish a connection for the producer.
        final Connection producerConnection = pooledConnectionFactory
                .createConnection();
        producerConnection.start();

        // Create a session.
        final Session producerSession = producerConnection
                .createSession(false, Session.AUTO_ACKNOWLEDGE);

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
        producerSession.close();
        producerConnection.close();
    }


    String
    receiveMessage(String nameQueue) throws JMSException { // Establish a connection for the consumer.
        // Note: Consumers should not use PooledConnectionFactory.
        final Connection consumerConnection = mqConnectionFactory.createConnection();
        consumerConnection.start();

        // Create a session.
        final Session consumerSession = consumerConnection
                .createSession(false, Session.AUTO_ACKNOWLEDGE);

        // Create a queue named "MyQueue".
        final Destination consumerDestination = consumerSession
                .createQueue(nameQueue);

        // Create a message consumer from the session to the queue.
        final MessageConsumer consumer = consumerSession
                .createConsumer(consumerDestination);
        String message;

        try {
            // Begin to wait for messages.
            final Message consumerMessage = consumer.receive(1000);

            // Receive the message when it arrives.
            final TextMessage consumerTextMessage = (TextMessage) consumerMessage;
            message = consumerTextMessage.getText();
            LOGGER.info("Message received: {}", consumerTextMessage.getText());
            return message;
            // Clean up the consumer.

        } catch (NullPointerException e){
            LOGGER.error("Queue is empty",e);
        }

        finally {
            consumer.close();
            consumerSession.close();
            consumerConnection.close();
        }

        return "";
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

    ActiveMQConnectionFactory createActiveMQConnectionFactory(String endpoint, String username, String password) {
        // Create a connection factory.
        final ActiveMQConnectionFactory connectionFactory =
                new ActiveMQConnectionFactory(endpoint);

        // Pass the sign-in credentials.
        connectionFactory.setUserName(username);
        connectionFactory.setPassword(password);
        return connectionFactory;
    }

    public void stopPooledConnectionFactory() {
        pooledConnectionFactory.stop();
    }

}
