// Generated with assistance from Claude
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;
import java.time.LocalDate;

public class TicketStorage {

    private static final String DB_URL = "jdbc:derby:ticketdb;create=true";
    
    //runs once, forces Java to run Derby driver
    static {
        try {
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
        } catch (ClassNotFoundException e) {
            System.out.println("Derby embedded driver not found: " + e.getMessage());
        }
    }
    
    //opening a connection to the database
    private static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }
    
    //create tables
    public static boolean initialize() {
        try (Connection conn = getConnection()) {//closes connection once block finish
            //create tables if there is none yet
            createTableIfMissing(conn,
                "CREATE TABLE tickets (" +
                "id INT PRIMARY KEY, " +
                "title VARCHAR(500) NOT NULL, " +
                "description VARCHAR(2000), " +
                "assignee VARCHAR(100), " +
                "status VARCHAR(20) NOT NULL, " +
                "priority VARCHAR(10) NOT NULL, " +
                "due_date DATE, " +
                "last_modified DATE NOT NULL)");

            createTableIfMissing(conn,
                "CREATE TABLE ticket_dependencies (" +
                "ticket_id INT NOT NULL, " +
                "depends_on_id INT NOT NULL, " +
                "PRIMARY KEY (ticket_id, depends_on_id), " +
                "FOREIGN KEY (ticket_id) REFERENCES tickets(id), " +
                "FOREIGN KEY (depends_on_id) REFERENCES tickets(id))");
            //return true if success
            return true;
        } catch (SQLException e) {
            System.out.println("Could not start database: " + e.getMessage());
            //return false if there is an error in starting up database
            return false;
        }
    }
    
    //helper class to catch the error if the table already exist
    private static void createTableIfMissing(Connection conn, String sql) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            //catch the error for the table already existing
            if (!"X0Y32".equals(e.getSQLState())) {
                throw e;
            }
        }
    }
    
    //wipes everything that was stored and write down the data in memory
    public static void save(TicketManager manager) {
        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);
            
            //wipes everything from both tables
            try (PreparedStatement clearDeps = conn.prepareStatement("DELETE FROM ticket_dependencies");
                 PreparedStatement clearTickets = conn.prepareStatement("DELETE FROM tickets")) {
                clearDeps.executeUpdate();
                clearTickets.executeUpdate();
            }
            
            //Tickets table
            //contains 8 fields
            String insertTicket = "INSERT INTO tickets " +
                "(id, title, description, assignee, status, priority, due_date, last_modified) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(insertTicket)) {
                //loop through all the tickets
                for (Ticket t : manager.getAllTickets()) {
                    //sets the value. Matches the order in insertTicket
                    stmt.setInt(1, t.getId());
                    stmt.setString(2, t.getTitle());
                    stmt.setString(3, t.getDescription());
                    stmt.setString(4, t.getAssignee());
                    stmt.setString(5, t.getStatus().name());
                    stmt.setString(6, t.getPriority().name());
                    stmt.setDate(7, t.getDuedate() == null ? null : Date.valueOf(t.getDuedate()));
                    stmt.setDate(8, Date.valueOf(t.getLastModified()));
                    stmt.executeUpdate();
                }
            }
            //Dependencies Table
            //only 2 fields
            String insertDep = "INSERT INTO ticket_dependencies (ticket_id, depends_on_id) VALUES (?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(insertDep)) {
                //loops through all tickets
                for (Ticket t : manager.getAllTickets()) {
                    //loops through all the dependencies if the ticket has it
                    for (int depId : t.getDependencies()) {
                        stmt.setInt(1, t.getId());
                        stmt.setInt(2, depId);
                        stmt.executeUpdate();
                    }
                }
            }
            //commits the changes to the database
            conn.commit();
        } catch (SQLException e) {
            System.out.println("Failed to save tickets: " + e.getMessage());
        }
    }
    
    //reading the tickets back
    public static void load(TicketManager manager) {
        try (Connection conn = getConnection();
            //select everything from tickets
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM tickets");
            //asks for data back from select stamement 
            ResultSet rs = stmt.executeQuery()) {
            
            //moves through the rows
            while (rs.next()) {
                //pulls the values by the row name
                int id = rs.getInt("id");
                String title = rs.getString("title");
                String description = rs.getString("description");
                String assignee = rs.getString("assignee");
                Status status = Status.valueOf(rs.getString("status"));
                Priority priority = Priority.valueOf(rs.getString("priority"));
                Date dueSql = rs.getDate("due_date");
                LocalDate duedate = (dueSql == null) ? null : dueSql.toLocalDate();
                LocalDate lastModified = rs.getDate("last_modified").toLocalDate();
                
                //converts the values into the respective java objects
                Ticket t = new Ticket(id, title, description, assignee, status, duedate, lastModified, priority);
                manager.addLoadedTicket(t);
            }
        } catch (SQLException e) {
            System.out.println("Failed to load tickets: " + e.getMessage());
            return;
        }

        loadDependencies(manager);
    }
    
    //match rows in ticket_dependencies to the matching ticket
    private static void loadDependencies(TicketManager manager) {
        try (Connection conn = getConnection();
            //select everything from the dependency table
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM ticket_dependencies");
            ResultSet rs = stmt.executeQuery()) {
            
            //loops through the rows
            while (rs.next()) {
                //sets ticket id
                Ticket t = manager.getTicket(rs.getInt("ticket_id"));
                //add dependency ID onto the list if it exists
                if (t != null) {
                    t.getDependencies().add(rs.getInt("depends_on_id"));
                }
            }
        } catch (SQLException e) {
            System.out.println("Failed to load dependencies: " + e.getMessage());
        }
    }
    
    //shuts down the embedded Derby engine
    public static void shutdown() {
        try {
            DriverManager.getConnection("jdbc:derby:;shutdown=true");
        } catch (SQLException e) {
            //expected
        }
    }
}