//written with assistance from Claude.ai

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

// Detail view of a ticket, opened from the main view by clicking on a ticket's row
// shows every field of one ticket, back / closing the window returns to the main view
public class DetailViewGUI extends TicketWindow{

    private final MainViewGUI mainView;
    private final Ticket ticket;

    public DetailViewGUI(TicketManager manager, MainViewGUI mainView, Ticket ticket){
        super(manager, "Detail View", 700, 560);
        this.mainView = mainView;
        this.ticket = ticket;

        // Delete button sits on the right of the page title, away from Back / Edit
        JButton deleteButton = new JButton("Delete");
        header.addTitleButton(deleteButton);

        // Form
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(new EmptyBorder(5,20,5,20));
        addRow(form, 0, "ID", readOnlyField(Formats.formatId(ticket.getId())), false);
        addRow(form, 1, "Name", readOnlyField(ticket.getTitle()), false);
        addRow(form, 2, "Status", readOnlyField(ticket.getStatus().getLabel()), false);
        addRow(form, 3, "Priority", readOnlyField(ticket.getPriority().getLabel()), false);
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
        backButton.addActionListener(e -> goBack());
        editButton.addActionListener(e -> openEdit());
        deleteButton.addActionListener(e -> confirmDelete());

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
        if (!manager.save()){
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

    // X button acts like Back
    @Override
    protected void onWindowClosing(){
        goBack();
    }

    // After a reset the ticket no longer exists so go back to main view
    @Override
    protected void afterReset(){
        goBack();
    }

    // Help screen
    @Override
    protected String helpText() {
        return """
                DETAIL VIEW

                Shows all the details of one ticket.

                Back         - Return to the list.
                Edit         - Change this ticket.
                Delete       - Permanently delete this ticket (asks first).
                Save & Exit  - Save all tickets and close the program.
                Reset        - Delete every ticket and start fresh (asks first).
                """;
    }
}
