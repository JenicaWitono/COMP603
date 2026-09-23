import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author jenic + THE INCREDIBLE JDOGTACULAR
 */
public class TicketManager {
    //hashmap to store data
    private Map<Integer, Ticket> tickets = new HashMap<>();
    //for corruptedIDs
    private Set<Integer> corruptedIds = new HashSet<>();

    //assigning id's to new tickets
    private int nextID = 1;
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
                continue; //
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
    
    // Removes a ticket by ID
    public boolean removeTicket(int id){
        return tickets.remove(id) != null;
    }
    
    // Inserts a ticket during load, preserving its saved ID, and keeps nextID ahead of it
    public void addLoadedTicket(Ticket t){
        tickets.put(t.getId(), t);
        if (t.getId() >= nextID) {
            nextID = t.getId() + 1;
        }
    }
    
    // Resets the save file tickets are stored in
    public void reset(){
        tickets.clear();
        nextID = 1;
    }
    
    // Ticket lookup 
    public Ticket getTicket(int id){
        return tickets.get(id);
    }

    // Marks an ID as unusable due to a load failure, and keeps nextID ahead of it
    public void markCorrupted(int id){
        corruptedIds.add(id);
        if (id >= nextID) {
            nextID = id + 1;
        }
    }
    
    //checks if the ticket is corrupted.
    public boolean isCorrupted(int id){
        return corruptedIds.contains(id);
    }
}