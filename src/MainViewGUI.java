import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.time.format.DateTimeFormatter;

// Homepage/mainview of the ticket manager, backed by the Derby database
// all buttons like Help and new tasks are still placeholders, however save & exit work

public class MainViewGUI extends JFrame {
    
    // Ticket manager is the single source that the GUI reads from
    private final TicketManager manager;
    private final DefaultTableModel model;
    private final JTable table;
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yy");
    
    // Window Constructor
    public MainViewGUI(TicketManager manager) {
        this.manager = manager;
        
        // Window setup
        setTitle("TicketManager");
        setSize(700, 400);
        setLocationRelativeTo(null); // Sets it to the centre of the screen
        
        // Closing the window will save to the database, then end the program
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter(){
            @Override
            public void windowClosing(WindowEvent e){
                saveAndExit();
            }
        });
        
        // Top Bar: Help and Save buttons, and the title
        JButton helpButton = new JButton("Help");
        JButton saveExitButton = new JButton("Save & Exit");
        
        JPanel buttonRow = new JPanel();
        buttonRow.add(helpButton);
        buttonRow.add(saveExitButton);
        
        JLabel appTitle = new JLabel("Ticket Manager", SwingConstants.CENTER);
        appTitle.setFont(appTitle.getFont().deriveFont(Font.BOLD,18f));
        
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.add(buttonRow, BorderLayout.WEST);
        topBar.add(appTitle, BorderLayout.CENTER);
        topBar.setBorder(BorderFactory.createMatteBorder(0,0,2,0, java.awt.Color.BLACK));
        
        // Page Title and New Task Button
        //Page Title
        JLabel pageTitle = new JLabel("Main View");
        pageTitle.setFont(pageTitle.getFont().deriveFont(Font.PLAIN, 24f));
        pageTitle.setBorder(new EmptyBorder(5, 10, 5, 0));
        
        // New Task Button
        JButton newTaskButton = new JButton("New Task");
        JPanel newTaskRow = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));
        newTaskRow.add(newTaskButton);
        
        // Stacking the three pieces vertically in the header
        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        topBar.setAlignmentX(LEFT_ALIGNMENT);
        pageTitle.setAlignmentX(LEFT_ALIGNMENT);
        newTaskRow.setAlignmentX(LEFT_ALIGNMENT);
        header.add(topBar);
        header.add(pageTitle);
        header.add(newTaskRow);
        
        // Table (read-only cells)
        model = new DefaultTableModel(new String[]{"ID", "Name", "Status", "Priority", "Due Date"}, 0){
            @Override
            public boolean isCellEditable(int row, int col){
                return false;
            }
        };
        
        table = new JTable(model);
        table.setRowHeight(30);
        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setFont(table.getTableHeader().getFont().deriveFont(Font.BOLD));
        
        // Centre the text in all columns
        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);
        for(int i = 0; i < table.getColumnCount(); i++){
            table.getColumnModel().getColumn(i).setCellRenderer(center);
        }
        // Centre the header text
        ((DefaultTableCellRenderer) table.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
        
        // Fill the table from the manager
        refresh();
        
        // Actions: Help / New Task/ Detatil view (placehodlers for now)
        helpButton.addActionListener(e -> placeholder("Help screen"));
        saveExitButton.addActionListener(e -> saveAndExit());
        newTaskButton.addActionListener(e -> openNewTask());
        
        // Clicking on a r ow to open detail view (placeholder for now)
        table.addMouseListener(new MouseAdapter(){
            @Override
            public void mouseClicked(MouseEvent e){
                int row = table.rowAtPoint(e.getPoint());
                if(row >=0){
                    OpenDetail(row);
                }  
            }
        });
        
        // Puting it all into the window
        add(header, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
    }
    
    // Rebuilds the t able rows from the manager.
    // Call after any change to the data
    public void refresh(){
        model.setRowCount(0);
        for (Ticket t : manager.getAllTickets()){
            // Skip archived tickets
            if (t.getStatus() == Status.DONE || t.getStatus() == Status.CLOSED) continue;
           // One value per column
            model.addRow(new Object[]{
                String.format("%03d", t.getId()),
                t.getTitle(),
                formatStatus(t.getStatus()),
                formatPriority(t.getPriority()),
                t.getDuedate() == null ? "-": t.getDuedate().format(DATE_FORMAT)
            });
        }
    }
    
    // Writes every ticket to the database, will shut derby down and end the program
    private void saveAndExit() {
        TicketStorage.save(manager);
        TicketStorage.shutdown();
        System.exit(0);
    }
    // Hides this window and opesn the detail view for the ticket clicked
    private void OpenDetail(int row){
        int id = Integer.parseInt((String) table.getValueAt(row, 0));
        Ticket ticket = manager.getTicket(id);
        if(ticket == null) return;
        setVisible(false);
        new DetailViewGUI(manager, this, ticket).setVisible(true);
    }
    
    // Hides this window and opens the New Task screen (it brings us back when it closes)
    private void openNewTask() {
        setVisible(false);
        new NewTaskGUI(manager, this).setVisible(true);
    }
    
    // Stand in for screens that don't exist yet
    private void placeholder(String what){
        JOptionPane.showMessageDialog(this, what + " - not built yet.");
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
    
    public static void main(String[] args){
        if(!TicketStorage.initialize()){
            System.out.println("Database failed to start");
            return;
        }
        
        TicketManager manager = new TicketManager();
        TicketStorage.load(manager);
        
        SwingUtilities.invokeLater(() -> new MainViewGUI(manager).setVisible(true));
    }
}