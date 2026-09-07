import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author jenic
 */
public class TicketManager {
    //hashmap to store data
    private Map<Integer, Ticket> tickets = new HashMap<>();
    
    //assigning id's to new tickets
    private int nextID = 1;
    public Ticket createTicket(String title) {
        Ticket t = new Ticket(nextID, title);
        tickets.put(nextID, t);
        nextID++;
        return t;
    }
    
    //returns all tickets as a list sorted by ID
    //MainView and DetailedView will pull from here
    public List<Ticket> getAllTickets(){
        List<Ticket> list = new ArrayList<>(tickets.values());
        list.sort(Comparator.comparing(Ticket::getId));
        return list;
    }
    
}