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
import javax.swing.text.JTextComponent;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

// Edit screen, can be openede from the detail view edit button
// each field will have an edit button pressing the button turns the field into an input
// will show a confrim button next to it, when a field is bening edited all other edit buttons will disspear
// 
public class EditTicketGUI extends JFrame{
    
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    
    private final TicketManager manager;
    private final MainViewGUI mainView;
    private final DetailViewGUI detailView;
    private final Ticket ticket;
    
    // copy of the ticket's editable fields so that the real ticket is only changed when the confrim button has been pressed
    private String editedTitle;
    private String editedDescription;
    private Status editedStatus;
    private Priority editedPriority;
    private LocalDate editedDue;
    private List<Integer> editedDeps;
    private boolean changed = false; // will turn true when a field has been edited
    
    // Inputs shown while a field is being edited
    private final JTextField nameEditor = new JTextField();
    private final JComboBox<String> statusEditor = new JComboBox<>(statusLables());
    private final JComboBox<String> priorityEditor = new JComboBox<>(statusLables());
    private final JTextField dueEditor = new JTextField();
    private final JTextArea descEditor = new JTextArea(4, 20);
    private final JTextField depsEditor = new JTextField();
    private final JPanel form = new JPanel(new GridBagLayout());
    private final List<FieldRow> rows = new ArrayList<>();
    private FieldRow active = null; // the row is currently being edited
    
    public EditTicketGUI(TicketManager manager, MainViewGUI mainView, DetailViewGUI detailView, Ticket ticket){
        this.manager = manager;
        this.mainView = mainView;
        this.detailView = detailView;
        this. ticket = ticket;
        
        // Starts the working copy from the tickets current values
        editedTitle = ticket.getTitle();
        editedDescription = ticket.getDescription();
        editedStatus = ticket.getStatus();
        editedPriority = ticket.getPriority();
        editedDue = ticket.getDuedate();
        editedDeps = new ArrayList<>(ticket.getDependencies());
        
        // Window setup
        setTitle("TicketManager");
        setSize(700, 500);
        setLocationRelativeTo(null);
        
        // Closing the window
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter(){
            @Override
            public void windowClosing(WindowEvent e){
                cancel();
            }
        });
        
        // Top bar (same as the other screens)
        JButton helpButton = new JButton("Help");
        JButton saveExitButton = new JButton("Save & Exit");
 
        JPanel buttonRow = new JPanel();
        buttonRow.add(helpButton);
        buttonRow.add(saveExitButton);
 
        JLabel appTitle = new JLabel("Ticket Manager", SwingConstants.CENTER);
        appTitle.setFont(appTitle.getFont().deriveFont(Font.BOLD, 18f));
 
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.add(buttonRow, BorderLayout.WEST);
        topBar.add(appTitle, BorderLayout.CENTER);
        topBar.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, java.awt.Color.BLACK));
    
        // Page title
        JLabel pageTitle = new JLabel("Edit ticket");
        pageTitle.setFont(pageTitle.getFont().deriveFont(Font.PLAIN, 24f));
        pageTitle.setBorder(new EmptyBorder(5,10,5,0));
        pageTitle.setAlignmentX(LEFT_ALIGNMENT);
        topBar.setAlignmentX(LEFT_ALIGNMENT);
        
        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.add(topBar);
        header.add(pageTitle);
        
        // Input setup
        descEditor.setLineWrap(true);
        descEditor.setWrapStyleWord(true);
        dueEditor.setToolTipText("dd/mm/yyyy (leave empty for no due date)");
        depsEditor.setToolTipText("Ticket IDs separated by commas (leave empty for none)");
        
        // Pressing enter in a one-line input confrims that field
        ActionListener enterConfirms = e -> {
            if(active != null) confirmEdit(active);
        };
        nameEditor.addActionListener(enterConfirms);
        dueEditor.addActionListener(enterConfirms);
        depsEditor.addActionListener(enterConfirms);
        
        // The editable rows
        rows.add(new FieldRow("Name", false, readOnlyField(), nameEditor,
        ()-> editedTitle,
        () -> nameEditor.setText(editedTitle),
                this::editedName));
        
        rows.add(new FieldRow("Status", false, readOnlyField(), statusEditor,
        () -> formatStatus(editedStatus),
        () -> statusEditor.setSelectedIndex(editedStatus.ordinal()),
                this::editedStatus));
        
        rows.add(new FieldRow("Priority", false, readOnlyField(), priorityEditor,
        () -> formatPriority(editedPriority),
        () -> priorityEditor.setSelectedIndex(editedPriority.ordinal()),
                this::editedPriority));
        
        rows.add(new FieldRow("Due Date", false, readOnlyField(), dueEditor,
                () -> editedDue == null ? "-" : editedDue.format(DATE_FORMAT),
                () -> dueEditor.setText(editedDue == null ? "": editedDue.format(DATE_FORMAT)),
                this::editedDue));
        
        rows.add(new FieldRow("Description", true, readOnlyArea(4), new JScrollPane(descEditor),
        () -> (editedDescription == null || editedDescription.isBlank()) ? "-" : editedDescription,
        () -> {descEditor.setText(editedDescription == null ? "" : editedDescription);
        descEditor.setCaretPosition(0);
        },
                this::editedDescription));
        
        rows.add(new FieldRow("Dependencies", true, readOnlyArea(3), depsEditor,
                this::dependencyText,
                this::LoadDepsEditor,
                this::editedDeps));
        
        // Lay the rows out ([edit] [label] [input] [confrim]
        form.setBorder(new EmptyBorder(5,20,5,20));
        int gridRow = 0;
        for(FieldRow r : rows){
            addToForm(gridRow++, r);
        }
        
        // Last modified is automatically set, so no need for an edit button
        //also its supposed to inform when it was last eddited
        JTextField lastModified = readOnlyField();
        lastModified.setText(ticket.getLastModified().format(DATE_FORMAT));
        GridBagConstraints c = new GridBagConstraints();
        c.gridy = gridRow;
        c.insets = new Insets(6, 4, 6, 4);
        c.anchor = GridBagConstraints.WEST;
        c.gridx = 1;
        form.add(new JLabel("Last Modified:"), c);
        c.gridx = 2;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        form.add(lastModified, c);
        
        // Keeps the rows at the top of the window
        JPanel formHolder = new JPanel(new BorderLayout());
        formHolder.add(form, BorderLayout.NORTH);
        
        // Cancel / Confrim
        JButton cancelButton = new JButton ("Cancel");
        JButton confirmButton = new JButton ("Confrim");
        cancelButton.setPreferredSize(new Dimension(110, 28));
        confirmButton.setPreferredSize(new Dimension(110, 28));
        
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER, 60, 1));
        bottom.add(cancelButton);
        bottom.add(confirmButton);
        
        // Actions
        helpButton.addActionListener(e -> JOptionPane.showMessageDialog(this, "Help screen - not built yet."));
        saveExitButton.addActionListener(e -> saveAndExit());
        cancelButton.addActionListener(e -> cancel());
        confirmButton.addActionListener(e -> confrimAll());
 
        add(header, BorderLayout.NORTH);
        add(formHolder, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);
        }
    
    // One editable row
    
    private class FieldRow {
        final String label;
        final boolean tall; // text area rows keep their buttons at the top
        final JButton editBtn = new JButton("Edit");
        final JButton confirmBtn = new JButton("confirm);");
        final JPanel valuePanel = new JPanel(new BorderLayout()); // swaps between the value and the input
        final JTextComponent viewText; // read only display of the current value
        final JComponent viewComp; // viewtext or a scroll panel around it
        final JComponent editorComp; // the input shown while editing
        final Supplier<String> shownText; // text to display the current value
        final Runnable loadEditor; // puts the current value input into the input
        final BooleanSupplier stageEditor; // reads the input validates and stores it. false if invalid
        
        FieldRow(String label, boolean tall, JTextComponent viewText, JComponent editorComp,
                 Supplier<String> shownText, Runnable loadEditor, BooleanSupplier stageEditor) {
            this.label = label;
            this.tall = tall;
            this.viewText = viewText;
            this.viewComp = tall ? new JScrollPane(viewText) : viewText;
            this.editorComp = editorComp;
            this.shownText = shownText;
            this.loadEditor = loadEditor;
            this.stageEditor = stageEditor;
        
            editBtn.setPreferredSize(new Dimension(70, 26));
            confirmBtn.setPreferredSize(new Dimension(85, 26));
            confirmBtn.setVisible(false); // only shown while this row is being edited
        
            editBtn.addActionListener(e -> startEdit(this));
            confirmBtn.addActionListener(e -> confirmEdit(this));
        
            showView();
        }
        
        void showView(){
            viewText.setText(shownText.get());
            viewText.setCaretPosition(0);
            swap(viewComp);
        }
    
        void showEditor(){
            loadEditor.run();
            swap(editorComp);
        }
    
        private void swap(JComponent comp){
            valuePanel.removeAll();
            valuePanel.add(comp, BorderLayout.CENTER);
            valuePanel.revalidate();
            valuePanel.repaint();
        }          
    }
    
    private void addToForm(int gridRow, FieldRow r){
        GridBagConstraints c = new GridBagConstraints();
        c.gridy = gridRow;
        c.insets = new Insets(6, 4, 6, 4);
        c.anchor = r.tall ? GridBagConstraints.NORTHWEST : GridBagConstraints.WEST;
        
        c.gridx = 0;
        form.add(r.editBtn, c);
 
        c.gridx = 1;
        form.add(new JLabel(r.label + ":"), c);
 
        c.gridx = 2;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        form.add(r.valuePanel, c);
 
        c.gridx = 3;
        c.weightx = 0;
        c.fill = GridBagConstraints.NONE;
        form.add(r.confirmBtn, c);
    }
    
    // Edit flow
    
    //Turns a field into its input; everyother rows edit button will disappear
    private void startEdit(FieldRow row){
        if (active != null) return;
        active = row;
        
        for (FieldRow r: rows){
            r.editBtn.setVisible(r == row);
        }
        row.editBtn.setEnabled(false);
        row.confirmBtn.setVisible(true);
        row.showEditor();
        
        Component target = (row.editorComp instanceof JScrollPane)
                ? ((JScrollPane) row.editorComp).getViewport().getView() : row.editorComp;
        target.requestFocusInWindow();
        
        form.revalidate();
        form.repaint();
    }
    
    // Checks the input, if its valid the field shows the new value
    private void confirmEdit(FieldRow row) {
        if(!row.stageEditor.getAsBoolean()){
            return;// an error was shown, stays in edit mode so it can be fixed
        }
        
        changed = true;
        row.showView();
        row.confirmBtn.setVisible(false);
        row.editBtn.setEnabled(true);
        
        for(FieldRow r : rows){
            r.editBtn.setVisible(true);
        }
        active = null;
        
        form.revalidate();
        form.repaint();
    }
    
    // Reading and checking each input
    private boolean editedName() {
        String text = nameEditor.getText().trim();
        if(text.isEmpty()){
            error("Name cannot be empty.");
            return false;
        }
        if(text.split("\\s+").length > 50){
            error("Title limit is 50 words.");
            return false;
        }
        if(text.length() > 500){
            error("Title is too long (500 char max).");
            return false;
        }
        editedTitle = text;
        return true;
    }
    
    private boolean editedStatus() {
        Status newStatus = Status.values()[statusEditor.getSelectedIndex()];
        if (newStatus == Status.IN_PROGRESS || newStatus == Status.IN_REVIEW || newStatus == Status.DONE) {
            String incomplete = incompleteDependencies();
            if (!incomplete.isEmpty()) {
                error("Cannot move this ticket to " + formatStatus(newStatus)
                        + " - it has incomplete dependencies:" + incomplete);
                return false;
            }
        }
        editedStatus = newStatus;
        return true;
    }
    
    private boolean editedPriority() {
        editedPriority = Priority.values()[priorityEditor.getSelectedIndex()];
        return true;
    }
    
    private boolean editedDue(){
        String text = dueEditor.getText().trim();
        if (text.isEmpty()) {
            editedDue = null; // empty clears the due date
            return true;
        }
        try {
            editedDue = InputHelper.parseDate(text);
            return true;
        } catch (DateTimeParseException ex) {
            error("Due date must look like dd/mm/yyyy.");
            return false;
        }
    }
    
    private boolean editedDescription(){
        String text = descEditor.getText().trim();
        if(text.length() > 2000){
            error("Description is too long (2000 char max). ");
            return false;
        }
        editedDescription = text.isEmpty() ? null : text;
        return true;
    }
    
    private boolean editedDeps(){
        List<Integer> newDeps = new ArrayList<>();
        String text = depsEditor.getText().trim();
        if(!text.isEmpty()){
            for(String part : text.split(",")){
                int id;
                try{
                    id = Integer.parseInt(part.trim());
                } catch (NumberFormatException ex){
                    error("'" + part.trim() + "' is not a valid ticket ID.");
                    return false;
                }
                if(id == ticket.getId()){
                    error("A ticket cannot depend on itself.");
                    return false;
                }
                if(manager.getTicket(id) == null){
                    error("No ticket found with ID #"+id+".");
                    return false;
                }
                if(!newDeps.contains(id)) newDeps.add(id);
            }
        }
        editedDeps = newDeps;
        return true;
    }
    
    // Lines for any dependency that isn't done or closed yet
    private String incompleteDependencies(){
        StringBuilder sb = new StringBuilder();
        for(int depId: editedDeps){
            Ticket dep = manager.getTicket(depId);
            if(dep != null && dep.getStatus() != Status.DONE && dep.getStatus() != Status.CLOSED){
                sb.append("\n #").append(String.format("%03d", dep.getId())).append(" - ").append(dep.getTitle());
            }
        }
        return sb.toString();
    }
    
    // IDs as "2, 5" for the dependencies input
    private void LoadDepsEditor(){
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < editedDeps.size(); i++){
            if(i>0) sb.append(", ");
            sb.append(editedDeps.get(i));
        }
        depsEditor.setText(sb.toString());
    }
    
    // One line per dependecy for the display 
    private String dependencyText(){
        if(editedDeps.isEmpty()) return "-";
        StringBuilder sb = new StringBuilder();
        for(int depId : editedDeps){
            sb.append("#").append(String.format("%03d", depId));
            Ticket dep = manager.getTicket(depId);
            if(dep != null){
                sb.append(" - ").append(dep.getTitle()).append(" (").append(formatStatus(dep.getStatus())).append(")");
            } else{
                sb.append(" (missing)");
            }
            sb.append("\n");
        }
        return sb.toString().trim();
    }
    
    // Leaving the screen
    
    // Bottom confrim button, writes the working copy to the ticket saves and then returns to detail view
    private void confrimAll(){
        // if a field is still open cannot confrim
        if(active != null){
            confirmEdit(active);
            if(active !=  null) return;
        }
        
        if(changed){
            ticket.setTitle(editedTitle);
            ticket.setDescription(editedDescription);
            ticket.setStatus(editedStatus);
            ticket.setPriority(editedPriority);
            ticket.setDuedate(editedDue);
            ticket.setDependencies(new ArrayList<>(editedDeps));
            ticket.updateLastModified();
            TicketStorage.save(manager);
            
            detailView.dispose();
            new DetailViewGUI(manager, mainView, ticket).setVisible(true);
        } else{
            detailView.setVisible(true); // nothing changed
        }
        dispose();
    }
    
    // Cancel/ X throws away the working copy and goes back to the detail view
    private void cancel() {
        detailView.setVisible(true);
        dispose();
    }
    
    // Save the tickets already in the manager and quits
    private void saveAndExit(){
        TicketStorage.save(manager);
        TicketStorage.shutdown();
        System.exit(0);
    }
    
    private void error(String message){
        JOptionPane.showMessageDialog(this, message, "Can't save change", JOptionPane.WARNING_MESSAGE);
    }
    
    // Small Helpers
    private static JTextField readOnlyField(){
        JTextField field = new JTextField();
        field.setEditable(false);
        return field;
    }
    
    private static JTextArea readOnlyArea(int rows){
        JTextArea area = new JTextArea(rows, 20);
        area.setEditable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        return area;
    }
    
    // Drop down lables, in the same order as the enum values
    private static String[] statusLables(){
        String[] labels = new String[Status.values().length];
        for (Status s : Status.values()){
            labels[s.ordinal()] = formatStatus(s);
        }
        return labels;
    }
    
    private static String[] priorityLables(){
        String[] labels = new String[Priority.values().length];
        for (Priority p : Priority.values()){
            labels[p.ordinal()] = formatPriority(p);
        }
        return labels;
    }
    private static String formatStatus(Status s) {
        return switch (s) {
            case TODO -> "To-Do";
            case IN_PROGRESS -> "In Progress";
            case IN_REVIEW -> "In Review";
            case DONE -> "Done";
            case CLOSED -> "Closed";
        };
    }
 
    private static String formatPriority(Priority p) {
        String s = p.toString();
        return s.charAt(0) + s.substring(1).toLowerCase();
    }
    
}
