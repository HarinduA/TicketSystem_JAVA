package src.ui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import src.core.*;
import src.threads.Customer;
import src.threads.FastVendor;
import src.threads.SlowVendor;

import java.util.List;

public class JavaFXInterface extends Application {
    private TextField totalTicketsField;
    private TextField ticketReleaseRateField;
    private TextField customerRetrievalRateField;
    private TextField maxTicketCapacityField;
    private Label statusLabel;
    private ListView<String> ticketListView; // ListView for dynamic ticket visualization
    private TicketPool ticketPool;
    private Thread vendorThread;
    private Thread customerThread;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Dynamic Ticket Pool Visualization");

        // Configuration input fields
        GridPane gridPane = new GridPane();
        gridPane.setVgap(10);
        gridPane.setHgap(10);

        totalTicketsField = new TextField();
        ticketReleaseRateField = new TextField();
        customerRetrievalRateField = new TextField();
        maxTicketCapacityField = new TextField();

        gridPane.add(new Label("Total Tickets:"), 0, 0);
        gridPane.add(totalTicketsField, 1, 0);
        gridPane.add(new Label("Ticket Release Rate:"), 0, 1);
        gridPane.add(ticketReleaseRateField, 1, 1);
        gridPane.add(new Label("Customer Retrieval Rate:"), 0, 2);
        gridPane.add(customerRetrievalRateField, 1, 2);
        gridPane.add(new Label("Max Ticket Capacity:"), 0, 3);
        gridPane.add(maxTicketCapacityField, 1, 3);

        // Buttons
        Button startButton = new Button("Start");
        Button stopButton = new Button("Stop");
        gridPane.add(startButton, 0, 4);
        gridPane.add(stopButton, 1, 4);

        // Status Label
        statusLabel = new Label("System Status: Stopped");
        gridPane.add(statusLabel, 0, 5, 2, 1);

        // ListView for ticket visualization
        ticketListView = new ListView<>();
        VBox ticketBox = new VBox(new Label("Ticket Pool:"), ticketListView);
        ticketBox.setSpacing(10);

        // Layout and scene setup
        VBox root = new VBox(gridPane, ticketBox);
        root.setSpacing(20);
        Scene scene = new Scene(root, 600, 400);
        primaryStage.setScene(scene);
        primaryStage.show();

        // Start button event
        startButton.setOnAction(event -> {
            try {
                startSystem();
            } catch (Exception e) {
                updateStatus("Error: " + e.getMessage());
            }
        });

        // Stop button event
        stopButton.setOnAction(event -> stopSystem());
    }

    private void startSystem() throws Exception {
        // Validate and parse input
        int totalTickets = validateInput(totalTicketsField.getText(), "Total Tickets");
        int ticketReleaseRate = validateInput(ticketReleaseRateField.getText(), "Ticket Release Rate");
        int customerRetrievalRate = validateInput(customerRetrievalRateField.getText(), "Customer Retrieval Rate");
        int maxTicketCapacity = validateInput(maxTicketCapacityField.getText(), "Max Ticket Capacity");

        // Initialize configuration and ticket pool
        ticketPool = new TicketPool();

        // Vendors and customers
        Thread fastVendor = new Thread(new FastVendor(ticketPool, ticketReleaseRate)); // Fast vendor
        Thread slowVendor = new Thread(new SlowVendor(ticketPool, ticketReleaseRate)); // Slow vendor

        TicketRetrievalStrategy retrievalStrategy = new PriorityRetrieval();
        customerThread = new Thread(new Customer(ticketPool, retrievalStrategy));

        // Start vendor and customer threads
        fastVendor.start();
        slowVendor.start();
        customerThread.start();

        // Start background thread for updating the ListView
        Thread uiUpdater = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                updateTicketList();
                try {
                    Thread.sleep(1000); // Update the list every second
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });
        uiUpdater.setDaemon(true);
        uiUpdater.start();

        updateStatus("System Running...");
    }

    private void stopSystem() {
        if (customerThread != null) customerThread.interrupt();
        updateStatus("System Stopped.");
    }

    private void updateTicketList() {
        List<String> tickets = ticketPool.getTickets();
        Platform.runLater(() -> {
            ticketListView.getItems().setAll(tickets);
        });
    }

    private void updateStatus(String status) {
        Platform.runLater(() -> statusLabel.setText("System Status: " + status));
    }

    private int validateInput(String input, String fieldName) throws Exception {
        try {
            int value = Integer.parseInt(input);
            if (value <= 0) {
                throw new Exception(fieldName + " must be positive.");
            }
            return value;
        } catch (NumberFormatException e) {
            throw new Exception(fieldName + " must be a valid integer.");
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
