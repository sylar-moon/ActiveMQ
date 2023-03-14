package my.group;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.stream.Stream;

public class PersonFactory {
    private final Random random = new Random();
    public Stream<Person> createStreamRandomPerson(){
        return Stream.generate(()->new Person(getRandomString(),random.nextInt(100),getRandomDate()));
    }
    public Person createRandomPerson(){
        return new Person(getRandomString(),random.nextInt(100),getRandomDate());
    }

    public String getRandomString(){
        int length = random.nextInt(19)+1;
        String charset = "abcdefghijklmnoprstuvwxyz";
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(charset.length());
            builder.append(charset.charAt(index));
        }
        return builder.toString();
    }

    public LocalDateTime getRandomDate(){
        int year = random.nextInt(2023-1900+1)+1900;
        int month = random.nextInt(12)+1;
        int day = random.nextInt(28)+1;
        int hour = random.nextInt(23);
        int minute = random.nextInt(60);
        int second = random.nextInt(60);
        return  LocalDateTime.of(year,month,day,hour,minute,second);
    }
}
