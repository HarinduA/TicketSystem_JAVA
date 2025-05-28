package src.tests;

import src.core.*;
import src.threads.*;

public class UnifiedTestRunner {

    public static void main(String[] args) {
        System.out.println("Select the test to run:");
        System.out.println("1. Test Customer Retrieval (Question 2)");
        System.out.println("2. Test Vendor Behavior (Question 4)");
        System.out.println("3. Test Statistics Reporting (Question 5)");

        try (java.util.Scanner scanner = new java.util.Scanner(System.in)) {
            int choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    testCustomerRetrieval();
                    break;
                case 2:
                    testVendorBehavior();
                    break;
                case 3:
                    testStatisticsReporting();
                    break;
                default:
                    System.out.println("Invalid choice. Please select 1, 2, or 3.");
            }
        }
    }

    // Test for Question 2: Customer Retrieval
    private static void testCustomerRetrieval() {
        System.out.println("Running Test: Customer Retrieval (Question 2)");
        TicketPool ticketPool = new TicketPool();

        // Add test tickets
        ticketPool.addTickets("Ticket-1");
        ticketPool.addTickets("VIP-Ticket");
        ticketPool.addTickets("Ticket-2");

        // Test PriorityRetrieval (FIFO)
        TicketRetrievalStrategy priorityStrategy = new PriorityRetrieval();
        Thread priorityCustomer = new Thread(new Customer(ticketPool, priorityStrategy));

        // Test IDRetrieval for a specific ticket
        TicketRetrievalStrategy idStrategy = new IDRetrieval("VIP-Ticket");
        Thread idCustomer = new Thread(new Customer(ticketPool, idStrategy));

        // Start customers
        priorityCustomer.start();
        idCustomer.start();

        try {
            priorityCustomer.join();
            idCustomer.join();
        } catch (InterruptedException e) {
            System.out.println("Test interrupted.");
        }

        // Verify ticket pool state
        System.out.println("Remaining tickets: " + ticketPool.getTickets());
    }

    // Test for Question 4: Vendor Behavior
    private static void testVendorBehavior() {
        System.out.println("Running Test: Vendor Behavior (Question 4)");
        TicketPool ticketPool = new TicketPool();

        // Create FastVendor and SlowVendor
        Thread fastVendor = new Thread(new FastVendor(ticketPool, 5)); // Adds tickets at double rate
        Thread slowVendor = new Thread(new SlowVendor(ticketPool, 5)); // Adds tickets at half rate

        // Start vendors
        fastVendor.start();
        slowVendor.start();

        try {
            fastVendor.join();
            slowVendor.join();
        } catch (InterruptedException e) {
            System.out.println("Test interrupted.");
        }

        // Verify ticket pool state
        System.out.println("Total tickets in pool: " + ticketPool.getTickets());
        System.out.println("Detailed tickets: " + ticketPool.getTickets());
    }

    // Test for Question 5: Periodic Statistics Reporting
    private static void testStatisticsReporting() {
        System.out.println("Running Test: Statistics Reporting (Question 5)");
        TicketPool ticketPool = new TicketPool();

        // Add test tickets
        ticketPool.addTickets("Ticket-1");
        ticketPool.addTickets("Ticket-2");

        // Start the StatisticsReporter
        StatisticsReporter reporter = new StatisticsReporter(ticketPool);
        Thread reporterThread = new Thread(reporter);
        reporterThread.start();

        // Simulate ticket operations
        new Thread(() -> {
            try {
                Thread.sleep(2000); // Wait 2 seconds
                ticketPool.addTickets("Ticket-3");
                Thread.sleep(2000); // Wait 2 seconds
                ticketPool.removeTicket();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();

        try {
            Thread.sleep(10000); // Run the reporter for 10 seconds
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            reporter.stopReporter();
            try {
                reporterThread.join();
            } catch (InterruptedException e) {
                System.out.println("Reporter thread interrupted.");
            }
        }

        // Final verification
        System.out.println("Final ticket pool state: " + ticketPool.getTickets());
    }
}
