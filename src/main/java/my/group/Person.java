package my.group;

import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Min;
import java.time.LocalDateTime;

public class Person {
    private String name;
    private int count;
    private LocalDateTime createdAt;
    public Person(){}

    public Person(String name, int count, LocalDateTime createdAt) {
        this.name = name;
        this.count = count;
        this.createdAt = createdAt;
    }

    @Length(min = 7,message = "Yor name length must be more 6 ")
    @CheckA(message = "Your name is not valid, name must contain at least one letter a")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Min(message = "Count cannot be not less than 9",value = 10)
    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

}
