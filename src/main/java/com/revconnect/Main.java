package com.revconnect;

import com.revconnect.presentation.ConsoleUI;
import com.revconnect.config.DatabaseConfig;
import com.revconnect.config.Log4jConfig;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class Main {
    private static final Logger logger = LogManager.getLogger(Main.class);

    public static void main(String[] args) {
        try {
            // Initialize configurations
            Log4jConfig.init();
            DatabaseConfig.initializeDatabase();

            logger.info("Starting RevConnect Application...");
            System.out.println("╔══════════════════════════════════════╗");
            System.out.println("║      Welcome to RevConnect!          ║");
            System.out.println("║   Professional Networking Platform   ║");
            System.out.println("╚══════════════════════════════════════╝");

            // Start the application
            ConsoleUI consoleUI = new ConsoleUI();
            consoleUI.start();

        } catch (Exception e) {
            logger.error("Failed to start RevConnect: " + e.getMessage(), e);
            System.err.println("Application failed to start. Please check logs for details.");
        }
    }
}