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
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

// New Task screen. will be opened from the main view, with cancel or confrim closing the window
// all return to main menu that will refesh and show the new created ticket
public class NewTaskGUI extends JFrame {
    
    private final TicketManager manager;
    private final MainViewGUI mainView;
    
    // Text inputs
    private final JTextField nameField = new JTextField();
    private final JTextField dueField = new JTextField();
    private final JTextField descField = new JTextField();
    private final JTextField depsField = new JTextField();
    
    // Status buttons (defaults to To-Do)
    private final JToggleButton todoBtn = new JToggleButton("To-Do", true);
    private final JToggleButton progressBtn = new JToggleButton("In Progress");
    private final JToggleButton doneBtn = new JToggleButton ("Done");
    
    // Priority Buttons (defaults to Medium)
    private final JToggleButton lowBtn = new JToggleButton("Low");
    private final JToggleButton mediumBtn = new JToggleButton("Medium", true);
    private final JToggleButton highBtn = new JToggleButton("High");
    
    public NewTaskGUI(TicketManager manager, MainViewGUI mainView) {
        this.manager = manager;
        this.mainView = mainView;
        
        // Window setup
        setTitle("TicketManager");
        setSize(700,450);
        setLocationRelativeTo(null);
        
        // Closing the window acts like cancel (goes back to main view)
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
        
        JPanel buttonRow = new JPanel();
        buttonRow.add(helpButton);
        buttonRow.add(saveExitButton);
        
        JLabel appTitle = new JLabel("Ticket Manager", SwingConstants.CENTER);
        appTitle.setFont(appTitle.getFont().deriveFont(Font.BOLD,18f));
        
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.add(buttonRow, BorderLayout.WEST);
        topBar.add(appTitle, BorderLayout.CENTER);
        topBar.setBorder(BorderFactory.createMatteBorder(0,0,2,0, java.awt.Color.BLACK));
        
        // Page Title
        JLabel pageTitle = new JLabel("New Task");
        pageTitle.setFont(pageTitle.getFont().deriveFont(Font.PLAIN, 24f));
        pageTitle.setAlignmentX(LEFT_ALIGNMENT);
        
        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.add(topBar);
        header.add(pageTitle);
        
        // Only one status / priority can be picked at a time
        ButtonGroup statusGroup = new ButtonGroup();
        statusGroup.add(todoBtn);
        statusGroup.add(progressBtn);
        statusGroup.add(doneBtn);
        
        ButtonGroup priorityGroup = new ButtonGroup();
        priorityGroup.add(lowBtn);
        priorityGroup.add(mediumBtn);
        priorityGroup.add(highBtn);
        
        dueField.setToolTipText("dd/mm/yyyy");
        
        // form: label, colon, and input on each row
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(new EmptyBorder(5, 20, 5,20));
        addRow(form, 0, "Name", nameField, true);
        addRow(form, 1, "Status", buttonGroupPanel(todoBtn, progressBtn, doneBtn), false);
        addRow(form, 2, "Priority", buttonGroupPanel(lowBtn, mediumBtn, highBtn), false);
        addRow(form, 3, "Due-Date", dueField, true);
        addRow(form, 4, "Description (optional)", descField, true);
        addRow(form, 5, "Dependencies (optional)", depsField, true);
        
        // Cancel/ Confrim
        JButton cancelButton = new JButton("Cancel");
        JButton confrimButton = new JButton("Confirm");
        cancelButton.setPreferredSize(new Dimension(110,28));
        confrimButton.setPreferredSize(new Dimension(110,28));
        
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER, 60, 10));
        bottom.add(cancelButton);
        bottom.add(confrimButton);
        
        // Actions
        helpButton.addActionListener(e -> JOptionPane.showMessageDialog(this, "not built yet"));
        saveExitButton.addActionListener(e -> saveAndExit());
        cancelButton.addActionListener(e -> goBack());
        confrimButton.addActionListener(e-> confrim());
        getRootPane().setDefaultButton(confrimButton); // enter = Confrim
        
        add(header, BorderLayout.NORTH);
        add(form, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);
    }
        
        
        
        
    // Adds one "label : input" row to the form
    private void addRow(JPanel form, int row, String label, JComponent input, boolean stretch){
            GridBagConstraints c = new GridBagConstraints();
            c.gridy = row;
            c.insets = new Insets(6, 4, 6, 4);
            c.anchor = GridBagConstraints.WEST;
            
            c.gridx = 0;
            form.add(new JLabel(label), c);
            
            c.gridx = 1;
            form.add(new JLabel(":"), c);
            
            c.gridx = 2;
            c.weightx = 1;
            c.fill = stretch ? GridBagConstraints.HORIZONTAL : GridBagConstraints.NONE;
            form.add(input, c);
        }
        
    // Three equal width buttons side by side
    private JPanel buttonGroupPanel(JToggleButton a, JToggleButton b, JToggleButton c){
        JPanel p = new JPanel(new GridLayout(1, 3, 8, 0));
        p.add(a);
        p.add(b);
        p.add(c);
        return p;
    }
    
    private Status selectedStatus() {
        if(progressBtn.isSelected()) return Status.IN_PROGRESS;
        if(doneBtn.isSelected()) return Status.DONE;
        return Status.TODO;
    }
    
    private Priority selectedPriority(){
        if(lowBtn.isSelected()) return Priority.LOW;
        if(highBtn.isSelected()) return Priority.HIGH;
        return Priority.MEDIUM;
    }
    
    // Validates everything first. then creates the ticket, so a bad input
    // never enters the system
    private void confrim(){
        // Name
        String title = nameField.getText().trim();
        if (title.isEmpty()){
            error("Name cannot be empty.");
            return;
        }
        if(title.split("\\s+").length > 50){
            error("Title limit is 50 words.");
            return;
        }
        if(title.length() > 500){
            error("This title is too long (400 char max).");
            return;
        }
        
        // Description
        String description = descField.getText().trim();
        if (description.length() > 2000) {
            error("Description is too long (2000 char max).");
            return;
        }

        // Due Date (optional)
        LocalDate due = null;
        String dueText = dueField.getText().trim();
        if (!dueText.isEmpty()) {
            try {
                due = InputHelper.parseDate(dueText);
            } catch (DateTimeParseException ex) {
                error("Due date must look like dd/mm/yyyy");
                return;
            }
        }

        // Dependencies (optional, comma separated)
        List<Integer> deps = new ArrayList<>();
        String depsText = depsField.getText().trim();
        if (!depsText.isEmpty()) {
            for (String part : depsText.split(",")) {
                int id;
                try {
                    id = Integer.parseInt(part.trim());
                } catch (NumberFormatException ex) {
                    error("'" + part.trim() + "' is not a valid ticket ID.");
                    return;
                }
                if (manager.getTicket(id) == null) {
                    error("No ticket found with ID #" + id + ".");
                    return;
                }
                if (!deps.contains(id)) deps.add(id);
            }
        }
        
        //cant start or finish a ticket if dependencies arent met add later
        
        //Everything is valid so create the ticket
        Ticket t = manager.createTicket(title);
        t.setStatus(selectedStatus());
        t.setPriority(selectedPriority());
        if (!description.isEmpty()) t.setDescription(description);
        if (due != null) t.setDuedate(due);
        for (int depId : deps) t.addDependency(depId);
        
        TicketStorage.save(manager);
        goBack();
    }
        
        // Close this window and go to the previous
        private void goBack(){
            dispose();
            mainView.refresh();
            mainView.setVisible(true);
        }
        
        // Save everytthing already in manager and quits
        private void saveAndExit(){
            TicketStorage.save(manager);
            TicketStorage.shutdown();
            System.exit(0);
        }
        
        private void error(String message) {
        JOptionPane.showMessageDialog(this, message, "Can't create ticket", JOptionPane.WARNING_MESSAGE);
    }
    
    

                
            
    
    
    
    
    
    
    
}   


