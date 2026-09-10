import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author jenic + THE INCREDIBLE JDOGTACULAR
 */
public class TicketManager {
    //hashmap to store data
    private Map<Integer, Ticket> tickets = new HashMap<>();
    
    //assigning id's to new tickets
    private int nextID = 1;
    public Ticket createTicket(String title) {
        Ticket t;
        t = new Ticket(nextID, title);
        tickets.put(nextID, t);
        nextID++;
        return t;
    }
    
    // Inserts a ticket during load, preserving its saved ID, and keeps nextID ahead of it
    public void addLoadedTicket(Ticket t){
        tickets.put(t.getId(), t);
        if (t.getId() >= nextID) {
            nextID = t.getId() + 1;
        }
    }
    
    //returns all tickets as a list sorted by ID
    //MainView and DetailedView will pull from here
    public List<Ticket> getAllTickets(){
        List<Ticket> list = new ArrayList<>(tickets.values());
        list.sort(Comparator.comparing(Ticket::getId));
        return list;
    }
    
    // Removes a ticket by ID
    public boolean removeTicket(int id){
        return tickets.remove(id) != null;
    }
    
    public void reset(){
        tickets.clear();
        nextID = 1;
    }
    
    // Ticket lookup 
    public Ticket getTicket(int id){
        return tickets.get(id);
    }
}