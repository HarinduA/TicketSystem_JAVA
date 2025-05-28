package src.logging;

import java.nio.file.Files;
import java.nio.file.Path;

public class LoggerTest {
    public static void main(String[] args) {
        try {
            // Define the log file path
            Path logFilePath = Path.of("resources/logs.txt");

            // Clean up the log file before the test
            if (Files.exists(logFilePath)) {
                Files.delete(logFilePath);
            }

            // Test log() method
            System.out.println("Testing log() method...");
            Logger.log("This is a test log message");

            // Test logTicketOperation() method
            System.out.println("Testing logTicketOperation() method...");
            Logger.logTicketOperation("ADD", "TICKET123");

            // Indicate tests are complete
            System.out.println("Logging tests executed. Check 'resources/logs.txt' for results.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
