package com.epam.webproject.model.connection;

import com.epam.webproject.exception.ConnectionException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayDeque;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;


public enum ConnectionPool {
    INSTANCE;
    private final static Logger logger = LogManager.getLogger();
    private final static int DEFAULT_POOL_SIZE = 8;
    private BlockingQueue<ProxyConnection> freeConnections;
    private BlockingQueue<ProxyConnection> usedConnections;


    ConnectionPool() {
        freeConnections = new LinkedBlockingQueue<>(DEFAULT_POOL_SIZE);
        usedConnections = new LinkedBlockingQueue<>(DEFAULT_POOL_SIZE);
        initializePool();

    }

    public Connection getConnection() {

        ProxyConnection proxyConnection = null;
        try {
            proxyConnection = freeConnections.take();
            usedConnections.offer(proxyConnection);
        } catch (InterruptedException e) {
            logger.error("The connection is not received " + e);
            Thread.currentThread().interrupt();
        }
        return proxyConnection;
    }


    public void releaseConnection(Connection connection) throws ConnectionException {
        if (connection instanceof ProxyConnection) {
            usedConnections.remove(connection);
            freeConnections.offer((ProxyConnection) connection);
        } else {
            logger.error("Connection is not proxy or null!");
            throw new ConnectionException("Invalid connection type passed");
        }
    }


    public void destroyPool() {
        for (int i = 0; i < DEFAULT_POOL_SIZE; i++) {
            try {
                freeConnections.take().reallyClose();
            } catch (SQLException | InterruptedException e) {
                logger.error("The pool was not destroyed " + e);
                Thread.currentThread().interrupt();
            }
        }
        deregisterDrivers();
    }

    private void deregisterDrivers() {
        DriverManager.getDrivers().asIterator().forEachRemaining(driver -> {
            try {
                DriverManager.deregisterDriver(driver);
            } catch (SQLException e) {
                logger.error("Can not deregister driver: " + e);
            }
        });
    }

    void initializePool() {
        for (int i = 0; i < DEFAULT_POOL_SIZE; i++) {
            try {
                Connection connection = ConnectionCreator.getConnection();
                ProxyConnection proxyConnection = new ProxyConnection(connection);
                freeConnections.offer(proxyConnection);
            } catch (SQLException e) {
                logger.fatal(e);
                throw new RuntimeException();
            }
        }
    }



}
