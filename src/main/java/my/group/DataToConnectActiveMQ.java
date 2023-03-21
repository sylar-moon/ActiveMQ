package my.group;

public class DataToConnectActiveMQ {
    private final String endPoint;
    private final String username;
    private final String password;

    public DataToConnectActiveMQ(String endPoint, String username, String password) {
        this.endPoint = endPoint;
        this.username = username;
        this.password = password;
    }

    public String getEndPoint() {
        return endPoint;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

}
