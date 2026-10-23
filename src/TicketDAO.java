// Generated with assistance from Claude
import java.util.List;

// Data Access Object (DAO) for tickets: the rest of the program saves and loads through
// this interface, so it doesn't need to know which database is used or how it works
public interface TicketDAO {

    // Creates the tables if they don't exist yet, returns false if the database couldn't start
    boolean initialize();

    // Replaces everything stored with these tickets, returns false if the save failed
    boolean saveAll(List<Ticket> tickets);

    // Reads every stored ticket (with its dependencies), returns null if the load failed
    List<Ticket> loadAll();

    // Closes the database
    void shutdown();
}
