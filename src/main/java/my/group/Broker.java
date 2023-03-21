package my.group;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.pool.PooledConnectionFactory;
import org.slf4j.Logger;


import javax.jms.*;

public class Broker {
    private static final Logger LOGGER = new MyLogger().getLogger();
    RPS rps = new RPS();

    void
    sendMessage(String nameQueue, String message, Connection producerConnection) throws JMSException {

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
    }


    String
    receiveMessage(String nameQueue, Connection consumerConnection) throws JMSException { // Establish a connection for the consumer.
        rps.startWatch();
        // Create a session.
        final Session consumerSession = consumerConnection
                .createSession(false, Session.AUTO_ACKNOWLEDGE);
        System.out.println(rps.getTimeMillisecond());
        // Create a queue named "MyQueue".
        final Destination consumerDestination = consumerSession
                .createQueue(nameQueue);
        System.out.println(rps.getTimeMillisecond());

        // Create a message consumer from the session to the queue.
        final MessageConsumer consumer = consumerSession
                .createConsumer(consumerDestination);
        String message;
        System.out.println(rps.getTimeMillisecond());

        try {
            // Begin to wait for messages.
            final Message consumerMessage = consumer.receive(1000);
            System.out.println(rps.getTimeMillisecond());

            // Receive the message when it arrives.
            final TextMessage consumerTextMessage = (TextMessage) consumerMessage;
            message = consumerTextMessage.getText();
            LOGGER.info("Message received: {}", consumerTextMessage.getText());
            System.out.println(rps.getTimeMillisecond());

            return message;
            // Clean up the consumer.

        } catch (NullPointerException e){
            LOGGER.error("Queue is empty",e);
            System.exit(4);
        }

        finally {
            consumer.close();
            consumerSession.close();
            rps.stopWatch();
        }

        return null;
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
