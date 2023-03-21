package my.group;

/**
 * Class for creating errors in json format
 **/
public class Error {
    private final String errors;
    Error(String errors){
        this.errors=errors;
    }

    public String getErrors() {
        return errors;
    }
}
