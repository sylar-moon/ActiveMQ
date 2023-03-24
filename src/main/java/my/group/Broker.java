package my.group;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.pool.PooledConnectionFactory;
import org.slf4j.Logger;


import javax.jms.*;

public class Broker {
    private static final Logger LOGGER = new MyLogger().getLogger();
    RPS rps = new RPS();

    void
    sendMessage(String message,Session session, MessageProducer producer){
        // Create a message.
        try {
            final TextMessage producerMessage = session.createTextMessage();

            // Send the message.
            producer.send(producerMessage);
            LOGGER.info("Message send: {}", message);}
        catch (JMSException e){
            LOGGER.error("Unable to send message: {}", message, e);
        }
    }



    String
    receiveMessage(String nameQueue, Connection consumerConnection) throws JMSException {
        rps.startWatch();
        // Create a session.
        Session consumerSession = consumerConnection
                .createSession(false, Session.AUTO_ACKNOWLEDGE);

        // Create a queue
        Queue queue = consumerSession
                .createQueue(nameQueue);

        // Create a message consumer from the session to the queue.
        MessageConsumer consumer = consumerSession
                .createConsumer(queue);

        try {
            // Begin to wait for messages.
            Message consumerMessage = consumer.receive();

            String text = ((TextMessage) consumerMessage).getText();
            LOGGER.info("Message received: {}", text);
            return text;

        } catch (NullPointerException e) {
            LOGGER.error("Queue is empty", e);
            System.exit(4);
        } finally {
            consumer.close();
            consumerSession.close();
            rps.stopWatch();
            rps.resetWatch();
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
