package src.core;

import src.logging.Logger;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class TicketPool implements TicketOperation {
    private final List<String> tickets = Collections.synchronizedList(new LinkedList<>());
    private int totalTicketsAdded = 0;   // Tracks total tickets added
    private int totalTicketsRemoved = 0; // Tracks total tickets removed

    @Override
    public synchronized void addTickets(String ticket) {
        tickets.add(ticket);
        totalTicketsAdded++;  // Increment total tickets added
        Logger.logTicketOperation("ADD", ticket);
    }

    @Override
    public synchronized String removeTicket() {
        if (tickets.isEmpty()) return null;
        String ticket = tickets.remove(0);
        totalTicketsRemoved++; // Increment total tickets removed
        Logger.logTicketOperation("REMOVE", ticket);
        return ticket;
    }

    public synchronized List<String> getTickets() {
        return new LinkedList<>(tickets); // Return a copy to avoid modification
    }

    public synchronized int getTicketCount() {
        return tickets.size();
    }

    public synchronized int getTotalTicketsAdded() {
        return totalTicketsAdded;
    }

    public synchronized int getTotalTicketsRemoved() {
        return totalTicketsRemoved;
    }
}
