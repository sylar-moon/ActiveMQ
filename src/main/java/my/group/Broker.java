package my.group;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.pool.PooledConnectionFactory;
import org.slf4j.Logger;


import javax.jms.*;

public class Broker {
    private static final Logger LOGGER = new MyLogger().getLogger();

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



//    String
//    receiveMessage(String nameQueue, Session consumerSession) throws JMSException {
//        // Create a session.
////        Session consumerSession = consumerConnection
////                .createSession(false, Session.AUTO_ACKNOWLEDGE);
//
//        // Create a queue
//        Destination queue = consumerSession
//                .createQueue(nameQueue);
//
//        // Create a message consumer from the session to the queue.
//        MessageConsumer consumer = consumerSession
//                .createConsumer(queue);
//
//        try {
//            // Begin to wait for messages.
//            Message consumerMessage = consumer.receive();
//
//
//            String text = ((TextMessage) consumerMessage).getText();
//            LOGGER.info("Message received: {}", text);
//            return text;
//
//        } catch (NullPointerException e) {
//            LOGGER.error("Queue is empty", e);
//            System.exit(4);
//        } finally {
//            consumer.close();
//            consumerSession.close();
//
//        }
//
//        return null;
//    }

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
