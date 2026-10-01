import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.time.format.DateTimeFormatter;
import java.util.List;

// Prototype of the homepage/mainview of the ticket manager
// only able to read existing tickets in the system.
// PROTOTYPE WILL LOOK WAY BETTER LATER
public class MainViewGUI extends JFrame {
    
    private static final String SAVE_FILE = "tickets.txt";
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yy");  
        
    public MainViewGUI(TicketManager manager) {
        setTitle("Ticket Manger - Main View");
        setSize(650, 350);
        setDefaultCloseOperation(EXIT_ON_CLOSE); // Will end the program when user hits the X
        setLocationRelativeTo(null); // Centre of screen
        
        // overrides the defualt edditable cells to uneditable 
        DefaultTableModel model = new DefaultTableModel(new String[]{"ID", "Name", "Status", "Priority", "Due Date"}, 0){
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }

        };
        
        // Gets every ticket in ticket manager
        List<Ticket> tickets = manager.getAllTickets();
        
        // Turns each ticket that is not archived onto a rable row
        for (Ticket t: tickets) {
            // skips all archieved tickets
            if (t.getStatus() == Status.DONE || t.getStatus() == Status.CLOSED) continue;
            
            // One value per column
            model.addRow(new Object[]{
                String.format("%03d", t.getId()),
                t.getTitle(),
                formatStatus(t.getStatus()),
                formatPriority(t.getPriority()),
                t.getDuedate() == null ? "-N/A-" : t.getDuedate().format(DATE_FORMAT)
            });
        }
        
        add(new JScrollPane(new JTable(model)), BorderLayout.CENTER);
    }
    
    // Status Display
    private static String formatStatus(Status s) {
        return switch(s){
            case TODO -> "To-Do";
            case IN_PROGRESS -> "In Progress";
            case IN_REVIEW -> "In Review";
            case DONE -> "Done";
            case CLOSED -> "Closed";
        };
    }
    
    // Converts Priority into display label, by making the first letter a captial and the rest lowercase
    private static String formatPriority(Priority p) {
        String s = p.toString();
        return s.charAt(0) + s.substring(1).toLowerCase();
    }
    
    public static void main(String[] args) {
        // Load saved tickets into a manger, then open the window
        TicketManager manager = new TicketManager();
        FileStorage.load(SAVE_FILE, manager);
        
        SwingUtilities.invokeLater(() -> new MainViewGUI(manager).setVisible(true));
    }
    
}
