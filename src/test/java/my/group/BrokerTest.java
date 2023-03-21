package my.group;

import org.junit.jupiter.api.Test;

import javax.jms.JMSException;

import static org.junit.jupiter.api.Assertions.*;

class BrokerTest {
    String endPoint = "ssl://b-b44c4b7c-b1c8-47b4-88de-84dd70ff0678-1.mq.us-east-2.amazonaws.com:61617";
//Broker broker = new Broker(endPoint,"sylar_moon","myPass911##qW");

@Test
    void testSendReceiveMessage() throws JMSException {
//    broker.sendMessage("NewQueue","myMessage");
//    String receiveMessage1 = broker.receiveMessage("NewQueue");
//    assertEquals("myMessage",receiveMessage1);
//
//    String receiveMessage2 = broker.receiveMessage("MyQueue");
//    assertEquals("",receiveMessage2);

}
}