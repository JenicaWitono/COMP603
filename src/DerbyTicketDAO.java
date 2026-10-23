// Generated with assistance from Claude
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// The Apache Derby (embedded) version of the ticket DAO, all the JDBC code lives here
public class DerbyTicketDAO implements TicketDAO {

    private final String dbUrl;

    //runs once, forces Java to run Derby driver
    static {
        try {
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
        } catch (ClassNotFoundException e) {
            System.out.println("Derby embedded driver not found: " + e.getMessage());
        }
    }

    // dbName is the database folder, Derby creates it the first time if it's missing
    public DerbyTicketDAO(String dbName) {
        this.dbUrl = "jdbc:derby:" + dbName + ";create=true";
    }

    //opening a connection to the database
    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(dbUrl);
    }

    //create tables
    @Override
    public boolean initialize() {
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

    //helper method to catch the error if the table already exists
    private void createTableIfMissing(Connection conn, String sql) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            //catch the error for the table already existing
            if (!"X0Y32".equals(e.getSQLState())) {
                throw e;
            }
        }
    }

    //wipes everything that was stored and writes down the tickets given
    //returns true if it saved, false if something went wrong (the database is left as it was)
    @Override
    public boolean saveAll(List<Ticket> tickets) {
        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);
            try {
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
                    for (Ticket t : tickets) {
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
                    for (Ticket t : tickets) {
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
                return true;
            } catch (SQLException e) {
                //undo the half-finished save so the old data stays
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            System.out.println("Failed to save tickets: " + e.getMessage());
            return false;
        }
    }

    //reading the tickets back, with their dependencies
    @Override
    public List<Ticket> loadAll() {
        //tickets by ID, so the dependency rows can find their ticket
        Map<Integer, Ticket> loaded = new HashMap<>();

        try (Connection conn = getConnection()) {
            //select everything from tickets
            try (PreparedStatement stmt = conn.prepareStatement("SELECT * FROM tickets");
                 //asks for data back from select statement
                 ResultSet rs = stmt.executeQuery()) {

                //moves through the rows
                while (rs.next()) {
                    //pulls the values by the column name
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
                    loaded.put(id, new Ticket(id, title, description, assignee, status, duedate, lastModified, priority));
                }
            }

            //match rows in ticket_dependencies to the matching ticket
            try (PreparedStatement stmt = conn.prepareStatement("SELECT * FROM ticket_dependencies");
                 ResultSet rs = stmt.executeQuery()) {

                //loops through the rows
                while (rs.next()) {
                    Ticket t = loaded.get(rs.getInt("ticket_id"));
                    //add dependency ID onto the list if the ticket exists
                    //(copy, add, set, so loading doesn't change the ticket's last modified date)
                    if (t != null) {
                        List<Integer> deps = new ArrayList<>(t.getDependencies());
                        deps.add(rs.getInt("depends_on_id"));
                        t.setDependencies(deps);
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Failed to load tickets: " + e.getMessage());
            return null;
        }
        return new ArrayList<>(loaded.values());
    }

    //shuts down the embedded Derby engine
    @Override
    public void shutdown() {
        try {
            DriverManager.getConnection("jdbc:derby:;shutdown=true");
        } catch (SQLException e) {
            //expected, Derby always reports shutdown as an exception
        }
    }
}
