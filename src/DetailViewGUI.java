//written with assistance from Claude.ai

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

// Detail view of a ticket, opened from the main view by clicking on a ticket's row
// shows every field of one ticket, back / closing the window returns to the main view
public class DetailViewGUI extends JFrame{

    private final TicketManager manager;
    private final MainViewGUI mainView;
    private final Ticket ticket;

    public DetailViewGUI(TicketManager manager, MainViewGUI mainView, Ticket ticket){
        this.manager = manager;
        this.mainView = mainView;
        this.ticket = ticket;

        // Window setup
        setTitle("TicketManager");
        setSize(700,560);
        setLocationRelativeTo(null);

        // Closing the window acts like back
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter(){
            @Override
            public void windowClosing(WindowEvent e){
                goBack();
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

        //Page Title
        JLabel pageTitle = new JLabel("Detailed View");
        pageTitle.setFont(pageTitle.getFont().deriveFont(Font.PLAIN, 24f));
        pageTitle.setBorder(new EmptyBorder(5, 10, 5, 0));

        // Delete button sits on the right of the page title, away from Back / Edit
        JButton deleteButton = new JButton("Delete");
        JPanel deleteHolder = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        deleteHolder.add(deleteButton);

        JPanel titleRow = new JPanel(new BorderLayout());
        titleRow.add(pageTitle, BorderLayout.WEST);
        titleRow.add(deleteHolder, BorderLayout.EAST);
        titleRow.setAlignmentX(LEFT_ALIGNMENT);
        topBar.setAlignmentX(LEFT_ALIGNMENT);

        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.add(topBar);
        header.add(titleRow);

        // Form
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(new EmptyBorder(5,20,5,20));
        addRow(form, 0, "ID", readOnlyField(Formats.formatId(ticket.getId())), false);
        addRow(form, 1, "Name", readOnlyField(ticket.getTitle()), false);
        addRow(form, 2, "Status", readOnlyField(Formats.formatStatus(ticket.getStatus())), false);
        addRow(form, 3, "Priority", readOnlyField(Formats.formatPriority(ticket.getPriority())), false);
        addRow(form, 4, "Assignee", readOnlyField(Formats.textOrDash(ticket.getAssignee())), false);
        addRow(form, 5, "Due-Date", readOnlyField(Formats.formatDate(ticket.getDuedate())), false);
        addRow(form, 6, "Description", readOnlyArea(Formats.textOrDash(ticket.getDescription()), 3), true);
        addRow(form, 7, "Dependencies", readOnlyArea(manager.dependencyText(ticket.getDependencies()), 3), true);
        addRow(form, 8, "Last Modified", readOnlyField(Formats.formatDate(ticket.getLastModified())), false);

        // Back and Edit buttons
        JButton backButton = new JButton("Back");
        JButton editButton = new JButton("Edit");
        for (JButton b : new JButton[]{backButton, editButton}) {
            b.setPreferredSize(new Dimension(110, 28));
        }

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 10));
        bottom.add(backButton);
        bottom.add(editButton);

        // Actions
        helpButton.addActionListener(e -> help());
        saveExitButton.addActionListener(e -> saveAndExit());
        backButton.addActionListener(e -> goBack());
        editButton.addActionListener(e -> openEdit());
        deleteButton.addActionListener(e -> confirmDelete());
        resetButton.addActionListener(e -> DatabaseReset.confirmAndReset(this, manager, this::goBack));

        add(header, BorderLayout.NORTH);
        add(form, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);
    }

    // Adds one "label : value" row to the form
    private void addRow(JPanel form, int row, String label, JComponent value, boolean tall){
        GridBagConstraints c = new GridBagConstraints();
        c.gridy = row;
        c.insets = new Insets(6, 4, 6, 4);
        c.anchor = tall ? GridBagConstraints.NORTHWEST : GridBagConstraints.WEST;

        c.gridx = 0;
        form.add(new JLabel(label), c);

        c.gridx = 1;
        form.add(new JLabel(":"), c);

        c.gridx = 2;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        form.add(value, c);
    }

    // A one-line box that can't be edited
    private JTextField readOnlyField(String text){
        JTextField field = new JTextField(text);
        field.setEditable(false);
        field.setCaretPosition(0);
        return field;
    }

    // A wrapping scrollable box that can't be edited (description and dependencies)
    private JScrollPane readOnlyArea(String text, int rows){
        JTextArea area = new JTextArea(text, rows, 20);
        area.setEditable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setCaretPosition(0);
        return new JScrollPane(area);
    }

    // Hides this window then opens the edit screen for this ticket
    private void openEdit(){
        setVisible(false);
        new EditTicketGUI(manager, mainView, this, ticket).setVisible(true);
    }

    // Asks before deleting, then removes the ticket, saves and returns to the main view
    private void confirmDelete(){
        // tell the user if other tickets will lose this one as a dependency
        int dependents = manager.countDependents(ticket.getId());

        String message = "Permanently delete ticket #" + Formats.formatId(ticket.getId())
                + " - " + ticket.getTitle() + "?\n";
        if (dependents > 0){
            message += dependents + " other ticket(s) depend on this one and will lose that dependency.\n";
        }
        message += "This cannot be undone.";

        if (!ConfirmDialog.ask(this, "Delete ticket", message, "Delete")){
            return;
        }

        manager.removeTicket(ticket.getId());
        if (!TicketStorage.save(manager)){
            JOptionPane.showMessageDialog(this, "The ticket was deleted, but the database could not be updated.",
                    "Save failed", JOptionPane.ERROR_MESSAGE);
        }
        goBack();
    }

    // Closes this window and returns to the main view
    private void goBack(){
        dispose();
        mainView.refresh();
        mainView.setVisible(true);
    }

    // Saves everything in the manager and quits
    // if the save fails it asks before quitting so nothing is lost by accident
    private void saveAndExit(){
        if (!TicketStorage.save(manager)) {
            boolean exitAnyway = ConfirmDialog.ask(this, "Save failed",
                    "Tickets could not be saved to the database.\nExit without saving?", "Exit anyway");
            if (!exitAnyway) return;
        }
        TicketStorage.shutdown();
        System.exit(0);
    }

    // Help screen
    private void help() {
        String text = """
                DETAIL VIEW

                Shows all the details of one ticket.

                Back         - Return to the list.
                Edit         - Change this ticket.
                Delete       - Permanently delete this ticket (asks first).
                Save & Exit  - Save all tickets and close the program.
                Reset        - Delete every ticket and start fresh (asks first).
                """;
        JOptionPane.showMessageDialog(this, text, "Help - Detail View", JOptionPane.INFORMATION_MESSAGE);
    }
}
