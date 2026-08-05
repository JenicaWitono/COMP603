
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author jenic
 */
public class TicketManager {
    //hashmao to store data
    private Map<Integer, Ticket> tickets = new HashMap<>();
    
    //assigning id's to new tickets
    private int nextID = 1;
    public Ticket createTicket(String title) {
        Ticket t = new Ticket(nextID, title);
        tickets.put(nextID, t);
        nextID++;
        return t;
    }
    
}
