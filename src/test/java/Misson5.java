package full.server;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import static org.junit.jupiter.api.Assertions.*;

public class Misson5 {
    private static final Logger logger = Logger.getLogger(Misson5.class.getName());

    @BeforeEach
    public void setUp() throws IOException {
        File logDir = new File("logs");
        if (logDir.exists()) {
            for (File file : logDir.listFiles()) {
                file.delete();
            }
        }
        System.setProperty("java.util.logging.config.file", "src/main/resources/logging.properties");
    }

    @Test
    public void testLogFileIsCreated() {
        logger.info("This is an info message.");
        File logFile = new File("logs/application-0.log");
        assertTrue(logFile.exists());
    }

    @Test
    public void testLogFileRolling() throws IOException {
        for (int i = 0; i < 1000; i++) {
            logger.info("Log message number " + i);
        }

        File logFile = new File("logs/application-1.log");
        assertTrue(logFile.exists());
    }

    @Test
    public void testErrorLoggingWithStackTrace() {
        try {
            throw new RuntimeException("Test exception");
        } catch (RuntimeException e) {
            logger.log(Level.SEVERE, "An error occurred", e);
        }

        File logFile = new File("logs/application-0.log");
        assertTrue(logFile.exists());
        
        try {
            boolean containsStackTrace = new String(java.nio.file.Files.readAllBytes(logFile.toPath()))
                    .contains("Test exception");
            assertTrue(containsStackTrace, "The log file should contain the stack trace of the exception");
        } catch (IOException e) {
            fail("Failed to read log file", e);
        }
    }

    @Test
    public void testLogLevel() {
        logger.setLevel(Level.ALL);

        logger.finest("This is a finest message.");
        logger.finer("This is a finer message.");
        logger.fine("This is a fine message.");
        logger.config("This is a config message.");
        logger.info("This is an info message.");
        logger.warning("This is a warning message.");
        logger.severe("This is a severe message.");

        File logFile = new File("logs/application-0.log");
        assertTrue(logFile.exists());

        try {
            String logContent = new String(java.nio.file.Files.readAllBytes(logFile.toPath()));
            assertTrue(logContent.contains("INFO: This is an info message."));
            assertTrue(logContent.contains("WARNING: This is a warning message."));
            assertTrue(logContent.contains("SEVERE: This is a severe message."));
        } catch (IOException e) {
            fail("Failed to read log file", e);
        }
    }
}