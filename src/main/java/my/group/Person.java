package my.group;

import org.hibernate.validator.constraints.Length;

import javax.annotation.Nonnull;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;
import java.time.LocalDateTime;
import java.util.Objects;

public class Person {
    private String name;
    private int count;
    private LocalDateTime createdAt;
    public Person(){}

    public Person( String name, int count, LocalDateTime createdAt) {
        this.name = name;
        this.count = count;
        this.createdAt = createdAt;
    }

    @Nonnull
    @Length(min = 7,message = "Yor name length must be more 6")
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Person person = (Person) o;
        return count == person.count && Objects.equals(name, person.name) && Objects.equals(createdAt, person.createdAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, count, createdAt);
    }
}
