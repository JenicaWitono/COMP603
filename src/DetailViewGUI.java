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
import java.awt.Insets;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.time.format.DateTimeFormatter;
import java.util.List;

// Detail view of a ticket, opened from the main view by clicking on a ticket's row
// shows every field of one ticket, black / closing the window returns to the main view
public class DetailViewGUI extends JFrame{
    
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    
    private final TicketManager manager;
    private final MainViewGUI mainView;
    private final Ticket ticket;
    
    public DetailViewGUI(TicketManager manager, MainViewGUI mainView, Ticket ticket){
        this.manager = manager;
        this.mainView = mainView;
        this.ticket = ticket;
        
        // Window setup
        setTitle("TicketManager");
        setSize(700,500);
        setLocationRelativeTo(null);
        
        // Closing the windows acts like back
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
        
        //Page Title
        JLabel pageTitle = new JLabel("Main View");
        pageTitle.setFont(pageTitle.getFont().deriveFont(Font.PLAIN, 24f));
        pageTitle.setBorder(new EmptyBorder(5, 10, 5, 0));
        pageTitle.setAlignmentX(LEFT_ALIGNMENT);
        topBar.setAlignmentX(LEFT_ALIGNMENT);
        
        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.add(topBar);
        header.add(pageTitle);
        
        // Form
        String due = ticket.getDuedate() == null ? "-" : ticket.getDuedate().format(DATE_FORMAT);
        String desc = (ticket.getDescription() == null || ticket.getDescription().isBlank()) ? "-" : ticket.getDescription();
        
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(new EmptyBorder(5,20,5,20));
        addRow(form, 0, "ID", readOnlyField(String.format("%03d", ticket.getId())), false);
        addRow(form, 1, "Name", readOnlyField(ticket.getTitle()), false);
        addRow(form, 2, "Status", readOnlyField(formatStatus(ticket.getStatus())), false);
        addRow(form, 3, "Priority", readOnlyField(formatPriority(ticket.getPriority())), false);
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
        helpButton.addActionListener(e -> placeholder("Help screen"));
        saveExitButton.addActionListener(e -> saveAndExit());
        backButton.addActionListener(e -> goBack());
        editButton.addActionListener(e -> openEdit());
        
        add(header, BorderLayout.NORTH);
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
                sb.append(" - ").append(dep.getTitle()).append(" (").append(formatStatus(dep.getStatus())).append(")");
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
    
    // Saves everything in the manager and quits
    private void saveAndExit(){
        TicketStorage.save(manager);
        TicketStorage.shutdown();
        System.exit(0);
    }
    
    // Placeholdeers
    private void placeholder(String what){
        JOptionPane.showMessageDialog(this, what + "not built yet");
    }
    
    // Status display label
    private static String formatStatus(Status s) {
        return switch (s) {
            case TODO -> "To-Do";
            case IN_PROGRESS -> "In Progress";
            case IN_REVIEW -> "In Review";
            case DONE -> "Done";
            case CLOSED -> "Closed";
        };
    }
    
    // Priority display lbel
    private static String formatPriority(Priority p){
        String s = p.toString();
        return s.charAt(0) + s.substring(1).toLowerCase();
    }
}
