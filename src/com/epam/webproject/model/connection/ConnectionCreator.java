package com.epam.webproject.model.connection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.ResourceBundle;

public final class ConnectionCreator {

    private final static Logger logger = LogManager.getLogger();

    private final static String RELATIVE_PATH_TO_PROPERTIES = "\\base";
    //private final static String DATABASE_PROPERTIES = "database.properties";

    private final static String URL = "url";

    private final static String USERNAME = "username";

    private final static String PASSWORD = "password";

    private final static String DRIVER = "driver";

    private final static String DATABASE_URL;

    private final static String DATABASE_USERNAME;

    private final static String DATABASE_PASSWORD;

    private static ResourceBundle properties;


    static {
        ResourceBundle resourceBundle = ResourceBundle.getBundle(RELATIVE_PATH_TO_PROPERTIES);
        try {
            DATABASE_URL = properties.getString(URL);
            String driver = properties.getString(DRIVER);
            DATABASE_USERNAME = properties.getString(USERNAME);
            DATABASE_PASSWORD = properties.getString(PASSWORD);
            Class.forName(driver);
        } catch (ClassNotFoundException | MissingResourceException e) {
            logger.fatal("fatal error: config file " + e);
            throw new RuntimeException(e);
        }
    }

    private ConnectionCreator() {
    }

    /**
     * Gets connection.
     *
     * @return the connection
     * @throws SQLException the sql exception
     */
    protected static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DATABASE_URL, DATABASE_USERNAME, DATABASE_PASSWORD);
    }
}