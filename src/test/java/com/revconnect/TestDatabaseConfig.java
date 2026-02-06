// test/java/com/revconnect/TestDatabaseConfig.java
package com.revconnect;

import java.io.InputStream;
import java.util.Properties;

public class TestDatabaseConfig {
    private static Properties properties = new Properties();

    static {
        try {
            InputStream input = TestDatabaseConfig.class.getClassLoader()
                    .getResourceAsStream("database.properties");
            if (input != null) {
                properties.load(input);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getTestDatabaseUrl() {
        return properties.getProperty("test.db.url", "jdbc:h2:mem:testdb");
    }

    public static String getTestDatabaseUser() {
        return properties.getProperty("test.db.username", "sa");
    }

    public static String getTestDatabasePassword() {
        return properties.getProperty("test.db.password", "");
    }

    public static String getDriver() {
        return properties.getProperty("test.db.driver", "org.h2.Driver");
    }
}