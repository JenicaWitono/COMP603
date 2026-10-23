// Generated with assistance from Claude
import java.util.List;

// The one place the program gets its ticket DAO from.
// The screens call these static methods, which pass the work on to the DAO,
// so swapping the database only means changing the line below
public class TicketStorage {

    private static TicketDAO dao = new DerbyTicketDAO("ticketdb");

    private TicketStorage(){
        // Static helper class
    }

    // Lets tests point the storage at a throwaway database instead of the real one
    static void useDatabase(String name) {
        dao = new DerbyTicketDAO(name);
    }

    // Creates the tables if needed, returns false if the database couldn't start
    public static boolean initialize() {
        return dao.initialize();
    }

    // Writes every ticket in the manager to the database, returns false if it failed
    public static boolean save(TicketManager manager) {
        return dao.saveAll(manager.getAllTickets());
    }

    // Puts every saved ticket into the manager, returns false if loading failed
    public static boolean load(TicketManager manager) {
        List<Ticket> tickets = dao.loadAll();
        if (tickets == null) {
            return false;
        }
        for (Ticket t : tickets) {
            manager.addLoadedTicket(t);
        }
        return true;
    }

    // Shuts down the database engine
    public static void shutdown() {
        dao.shutdown();
    }
}
