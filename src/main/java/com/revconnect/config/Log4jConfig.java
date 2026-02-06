package com.revconnect.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;
import java.io.File;

public class Log4jConfig {
    private static final Logger logger = LogManager.getLogger(Log4jConfig.class);

    public static void init() {
        try {
            // Create logs directory if it doesn't exist
            File logsDir = new File("logs");
            if (!logsDir.exists()) {
                logsDir.mkdir();
            }

            // Configure Log4j2 programmatically
            System.setProperty("log4j.configurationFile", "log4j2.xml");

            logger.info("Log4j2 configuration initialized successfully");
        } catch (Exception e) {
            System.err.println("Failed to initialize Log4j2: " + e.getMessage());
        }
    }
}