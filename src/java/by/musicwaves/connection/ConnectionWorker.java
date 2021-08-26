package by.musicwaves.connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.ResourceBundle;

public class ConnectionWorker {

    private final static ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle("resources.connection");
    
    String url = RESOURCE_BUNDLE.getString("url");
    Properties properties;

    public void init() throws SQLException {
        properties = new Properties();
        properties.put("user", RESOURCE_BUNDLE.getString("user"));
        properties.put("password", RESOURCE_BUNDLE.getString("password"));
        properties.put("usingpassword", RESOURCE_BUNDLE.getString("usingpassword"));
        properties.put("autoReconnect", RESOURCE_BUNDLE.getString("autoReconnect"));
        properties.put("characterEncoding", RESOURCE_BUNDLE.getString("characterEncoding"));
        properties.put("useUnicode", RESOURCE_BUNDLE.getString("useUnicode"));
        properties.put("allowMultiQueries", RESOURCE_BUNDLE.getString("allowMultiQueries"));

        DriverManager.registerDriver(new com.mysql.jdbc.Driver());

    }

    public Connection openConnection() throws SQLException {
        Connection connection = DriverManager.getConnection(url, properties);
        return connection;
    }

}
