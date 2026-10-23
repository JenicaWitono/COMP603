//written with assistance from Claude.ai

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

// Homepage/main view of the ticket manager, backed by the Derby database
// lists the active (or archived) tickets, clicking a row opens its detail view

public class MainViewGUI extends TicketWindow {

    private final DefaultTableModel model;
    private final JTable table;
    private boolean showArchived = false;

    // Window Constructor
    public MainViewGUI(TicketManager manager) {
        super(manager, "Main View", 700, 400);

        // New Task Button
        JButton newTaskButton = new JButton("New Task");
        JPanel newTaskRow = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));
        newTaskRow.add(newTaskButton);

        // Archive View button
        JButton archiveButton = new JButton("Archive");
        newTaskRow.add(archiveButton);

        // New Task and Archive buttons go in the extra row of the shared header
        header.addExtra(newTaskRow);

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

        // Actions
        newTaskButton.addActionListener(e -> openNewTask());
        archiveButton.addActionListener(e -> {
            showArchived = !showArchived;
            archiveButton.setText(showArchived ? "Active Tickets" : "Archive");
            header.setPageTitle(showArchived ? "Archived Tickets" : "Main View");
            newTaskButton.setEnabled(!showArchived); // new tickets start active, so hide the option while archived
            refresh();
        });
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

        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    // Rebuilds the table rows from the manager.
    // Call after any change to the data
    public void refresh(){
        model.setRowCount(0);
        for (Ticket t : manager.getAllTickets()){

            boolean archived = t.getStatus().isFinished();
            if (archived != showArchived) continue; // wrong list for the current mode
           // One value per column
            model.addRow(new Object[]{
                Formats.formatId(t.getId()),
                t.getTitle(),
                t.getStatus().getLabel(),
                t.getPriority().getLabel(),
                t.getDuedate() == null ? "-": t.getDuedate().format(Formats.SHORT_DATE_FORMAT)
            });
        }
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

    // reset
    @Override
    protected void afterReset() {
        refresh();
    }
    // help screen
    @Override
    protected String helpText(){
        return """
                      MAIN VIEW

                      This page lists your active tickets (anything that is not Done or Closed).
                      Click a row to open that ticket's detail view.

                      Buttons:
                        New Task - Create a new ticket.
                        Archive/Active Tickets - Toggles between active and archived tickets.
                        Save & Exit - Saves all tickets to the database and closes the program.
                        Reset - Deletes every ticket and starts fresh (asks first).
                      """;
    }

    public static void main(String[] args){
        // The program's one Derby DAO, everything else goes through the TicketManager
        TicketDAO dao = new DerbyTicketDAO("ticketdb");
        if(!dao.initialize()){
            // usually means the database is already open in another copy of the program
            JOptionPane.showMessageDialog(null,
                    "The ticket database could not be started.\n"
                    + "Make sure Ticket Manager isn't already open, then try again.",
                    "Database error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        TicketManager manager = new TicketManager(dao);
        manager.load();

        SwingUtilities.invokeLater(() -> new MainViewGUI(manager).setVisible(true));
    }
}
