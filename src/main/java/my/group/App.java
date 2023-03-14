package my.group;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.*;
import java.io.IOException;
import java.util.stream.Stream;

public class App {
    private static final Logger LOGGER = new MyLogger().getLogger();
    private static final String PROPERTIES_PATH = "config.properties";
    private static final MyProperties PROPERTIES = new MyProperties();
    public static final PersonFactory POJO_FACTORY = new PersonFactory();

    public static void main(String[] args){
        readPropertyFile();
        String endPoint = PROPERTIES.getProperty("sslActiveMQ");
        String userName = PROPERTIES.getProperty("username");
        String password = PROPERTIES.getProperty("password");
        String nameQueue = PROPERTIES.getProperty("nameQueue");

        long messageSendingTime = Long.parseLong(args[0]);
        int numberObjects = Integer.parseInt(PROPERTIES.getProperty("numberObjects"));
        Broker broker = new Broker(endPoint, userName, password);
//        Stream<Person> stream = POJO_FACTORY.createStreamRandomPerson();
        Thread producer = new Thread(new Producer(messageSendingTime,nameQueue,broker,numberObjects));
        producer.start();
        Thread consumer = new Thread(new Consumer(nameQueue,broker));
        consumer.start();
   }


//
//    private static ArrayList<String> sendJsonPojoOnTimerAndGetReceiveMessages(String nameQueue, Broker broker, long time,int numberObjects) throws JMSException {
//        long timeStopSendingMessage = System.currentTimeMillis()+(time*1000);//stopwath
//        ArrayList<String> receiveMessages = new ArrayList<>();
//        Supplier <Stream<Person>> supplier =()->  POJO_FACTORY.createPOJO(numberObjects);
//        while (System.currentTimeMillis()<timeStopSendingMessage){
//            Person person;
//            Optional<Person> optionalPerson =supplier.get().findAny();
//            if(optionalPerson.isPresent()){
//                person=optionalPerson.get();
//            }else {
//                break;
//            }
//            String json = JSON_CONVERTER.createJsonFromObjects(person);
//            String receiveMessage;
//            String sendMessage =System.currentTimeMillis()>=timeStopSendingMessage?POISON_PILL:json;
//            broker.sendMessage(nameQueue, sendMessage);
//            receiveMessage = broker.receiveMessage(nameQueue);
////            if(receiveMessage.equals(POISON_PILL)){
////                break;
////            }
//            receiveMessages.add(receiveMessage);
//        }
//        broker.stopPooledConnectionFactory();
//        return receiveMessages;
//        }


    private static void readPropertyFile() {
        try {
            PROPERTIES.readPropertyFile(PROPERTIES_PATH);
        } catch (IOException e) {
            LOGGER.error("Properties file not found");
        }
    }

}
