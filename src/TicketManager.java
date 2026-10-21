//written with assistance from Claude.ai
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Holds and manages all tickets in memory
public class TicketManager {
    //hashmap to store data
    private Map<Integer, Ticket> tickets = new HashMap<>();
    
    //assigning id's to new tickets
    private int nextID = 1;
    // creates and stores a new ticket. Auto assigns id based on nextID
    public Ticket createTicket(String title) {
        Ticket t = new Ticket(nextID, title);
        tickets.put(nextID, t);
        nextID++;
        return t;
    }
    
    // Returns true only if every dependency ticket is Done or Closed
    public boolean areDependenciesComplete(Ticket ticket){
        for (int depId : ticket.getDependencies()){
            Ticket dep = tickets.get(depId);
            if (dep == null){
                continue; // missing dependency ticket, skips
            }
            if (dep.getStatus() != Status.DONE && dep.getStatus() != Status.CLOSED){
                return false;
            }
        }
        return true;
    }
    
    //returns all tickets as a list sorted by ID
    //MainView and DetailedView will pull from here
    public List<Ticket> getAllTickets(){
        List<Ticket> list = new ArrayList<>(tickets.values());
        list.sort(Comparator.comparing(Ticket::getId));
        return list;
    }
    
    // Removes a ticket by ID and will return whether or not it existed
    public boolean removeTicket(int id){
        boolean removed = tickets.remove(id) != null;
        if (removed) {
            // clean up any other ticket that depended on this one
            for (Ticket t : tickets.values()) {
                t.getDependencies().remove(Integer.valueOf(id));
            }
        }
        return removed;
    }
    
    // Inserts a ticket during load, preserving its saved ID, and keeps nextID ahead of it
    public void addLoadedTicket(Ticket t){
        tickets.put(t.getId(), t);
        if (t.getId() >= nextID) {
            nextID = t.getId() + 1;
        }
    }
    
   // Clears all tickets and resets ID counter
    public void reset(){
        tickets.clear();
        nextID = 1;
    }
    
    // Ticket lookup, looks up by ID 
    public Ticket getTicket(int id){
        return tickets.get(id);
    }

}