/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author Jamie
 */
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.time.format.DateTimeFormatter;
import java.util.List;

// Detail view of a ticket, opened from the main view by clicking on a ticket's row
// shows every field of one ticket, black / closing the window returns to the main view
public class DetailViewGUI extends TicketWindow{
    
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
   
    private final MainViewGUI mainView;
    private final Ticket ticket;
    
    public DetailViewGUI(TicketManager manager, MainViewGUI mainView, Ticket ticket){
        super(manager, "Detail View", 700, 500);
        this.mainView = mainView;
        this.ticket = ticket;
        
        // Form
        String due = ticket.getDuedate() == null ? "-" : ticket.getDuedate().format(DATE_FORMAT);
        String desc = (ticket.getDescription() == null || ticket.getDescription().isBlank()) ? "-" : ticket.getDescription();
        
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(new EmptyBorder(5,20,5,20));
        addRow(form, 0, "ID", readOnlyField(String.format("%03d", ticket.getId())), false);
        addRow(form, 1, "Name", readOnlyField(ticket.getTitle()), false);
        addRow(form, 2, "Status", readOnlyField((ticket.getStatus().getLabel())), false);
        addRow(form, 3, "Priority", readOnlyField((ticket.getPriority().getLabel())), false);
        addRow(form, 4, "Due-Date", readOnlyField(due), false);
        addRow(form, 5, "Description", readOnlyArea(desc, 3), true);
        addRow(form, 6, "Dependencies", readOnlyArea(dependencyText(), 3), true);
        addRow(form, 7, "Last Modified", readOnlyField(ticket.getLastModified().format(DATE_FORMAT)), false);
        
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
        
        add(form, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);
    }
    
    // Adds one lable: value row to the form
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
    
    // A wrapping scrollable box thatt can't be edited (description and dependencies)
    private JScrollPane readOnlyArea(String text, int rows){
        JTextArea area = new JTextArea(text, rows, 20);
        area.setEditable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setCaretPosition(0);
        return new JScrollPane(area);
    }
    
    // One Line per dependency
   private String dependencyText(){
        List<Integer> deps = ticket.getDependencies();
        if (deps.isEmpty()) return "-";
        
        StringBuilder sb = new StringBuilder();
        for (int depId : deps){
            sb.append("#").append(String.format("%03d", depId));
            Ticket dep = manager.getTicket(depId);
            if (dep != null){
                sb.append(" - ").append(dep.getTitle()).append(" (").append(dep.getStatus().getLabel()).append(")");
            } else {
                sb.append(" (missing)");
            }
            sb.append("\n");
        }
        return sb.toString().trim();
    }
    
   // Hides this window then opens the edit screen for this ticket
   private void openEdit(){
       setVisible(false);
       new EditTicketGUI(manager, mainView, this, ticket).setVisible(true);
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
                Save & Exit  - Save all tickets and close the program.
                """;
    }
}
