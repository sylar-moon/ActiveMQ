package my.group;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.JMSException;

public class Consumer implements Runnable{
    private static final Logger LOGGER = new MyLogger().getLogger();
    private final String nameQueue;
    private final Broker broker;
    private static final String POISON_PILL = "End sending messages";
    MyValidator validator = new MyValidator();
    Consumer (String nameQueue,Broker broker){
        this.nameQueue=nameQueue;
        this.broker=broker;
    }
    @Override
    public void run() {
        String message="";
        while (!message.equals(POISON_PILL)) {
            try {
               message= broker.receiveMessage(nameQueue);
            } catch (JMSException e) {
                e.printStackTrace();
            }
            validator.validatePerson(message);
        }
        }
}
