//written with assistance from Claude.ai

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

// Homepage/main view of the ticket manager, backed by the Derby database
// lists the active (or archived) tickets, clicking a row opens its detail view

public class MainViewGUI extends JFrame {

    // Ticket manager is the single source that the GUI reads from
    private final TicketManager manager;
    private final DefaultTableModel model;
    private final JTable table;
    private boolean showArchived = false;

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
        JButton resetButton = new JButton("Reset");

        JPanel buttonRow = new JPanel();
        buttonRow.add(helpButton);
        buttonRow.add(saveExitButton);
        buttonRow.add(resetButton);

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

        // Archive View button
        JButton archiveButton = new JButton("Archive");
        newTaskRow.add(archiveButton);

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

        // Actions: Help / New Task / Detail view / Archive view toggle
        helpButton.addActionListener(e -> help());
        saveExitButton.addActionListener(e -> saveAndExit());
        newTaskButton.addActionListener(e -> openNewTask());
        archiveButton.addActionListener(e -> {
            showArchived = !showArchived;
            archiveButton.setText(showArchived ? "Active Tickets" : "Archive");
            pageTitle.setText(showArchived ? "Archived Tickets" : "Main View");
            newTaskButton.setEnabled(!showArchived); // new tickets start active, so hide the option while archived
            refresh();
        });
        resetButton.addActionListener(e -> DatabaseReset.confirmAndReset(this, manager));
        // Clicking on a row opens its detail view
        table.addMouseListener(new MouseAdapter(){
            @Override
            public void mouseClicked(MouseEvent e){
                int row = table.rowAtPoint(e.getPoint());
                if(row >=0){
                    openDetail(row);
                }
            }
        });

        // Putting it all into the window
        add(header, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    // Rebuilds the table rows from the manager.
    // Call after any change to the data
    public void refresh(){
        model.setRowCount(0);
        for (Ticket t : manager.getAllTickets()){

            boolean archived = t.getStatus() == Status.DONE || t.getStatus() == Status.CLOSED;
            if (archived != showArchived) continue; // wrong list for the current mode
           // One value per column
            model.addRow(new Object[]{
                Formats.formatId(t.getId()),
                t.getTitle(),
                Formats.formatStatus(t.getStatus()),
                Formats.formatPriority(t.getPriority()),
                t.getDuedate() == null ? "-": t.getDuedate().format(Formats.SHORT_DATE_FORMAT)
            });
        }
    }

    // Writes every ticket to the database, will shut derby down and end the program
    // if the save fails it asks before quitting so nothing is lost by accident
    private void saveAndExit() {
        if (!TicketStorage.save(manager)) {
            boolean exitAnyway = ConfirmDialog.ask(this, "Save failed",
                    "Tickets could not be saved to the database.\nExit without saving?", "Exit anyway");
            if (!exitAnyway) return;
        }
        TicketStorage.shutdown();
        System.exit(0);
    }
    // Hides this window and opens the detail view for the ticket clicked
    private void openDetail(int row){
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

    // help screen
    private void help(){
        String text = """
                      MAIN VIEW

                      This page lists your active tickets (anything that is not Done or Closed).
                      Click a row to open that ticket's detail view.

                      Buttons:
                        New Task - Create a new ticket.
                        Archive/Active Tickets - Toggles between active and archived tickets.
                        Save & Exit - Saves all tickets to the database and closes the program.
                        Reset - Deletes every ticket and starts fresh (asks first).
                      """;
        JOptionPane.showMessageDialog(this, text, "Help - Main View", JOptionPane.INFORMATION_MESSAGE);
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
