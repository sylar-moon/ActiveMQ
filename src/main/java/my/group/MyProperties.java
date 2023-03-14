package my.group;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class MyProperties  {
    private final Properties properties;

    MyProperties()  {
        properties=new Properties();
    }

    public void readPropertyFile(String path) throws IOException{
        try (FileInputStream inputStream = new FileInputStream(path)) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            properties.load(reader);
            reader.close();
        }
    }

    public String getProperty(String key)  {
        return properties.getProperty(key);
    }
}

