//written with assistance from Claude.ai
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
import java.util.List;

// New Task screen. will be opened from the main view, with cancel or confirm closing the window
// both return to the main view, which will refresh and show the newly created ticket
public class NewTaskGUI extends JFrame {

    private final TicketManager manager;
    private final MainViewGUI mainView;

    // Text inputs
    private final JTextField nameField = new JTextField();
    private final JTextField assigneeField = new JTextField();
    private final JTextField dueField = new JTextField();
    private final JTextField descField = new JTextField();
    private final JTextField depsField = new JTextField();

    // Status buttons (defaults to To-Do)
    private final JToggleButton todoBtn = new JToggleButton("To-Do", true);
    private final JToggleButton progressBtn = new JToggleButton("In Progress");
    private final JToggleButton doneBtn = new JToggleButton("Done");

    // Priority Buttons (defaults to Medium)
    private final JToggleButton lowBtn = new JToggleButton("Low");
    private final JToggleButton mediumBtn = new JToggleButton("Medium", true);
    private final JToggleButton highBtn = new JToggleButton("High");

    public NewTaskGUI(TicketManager manager, MainViewGUI mainView) {
        this.manager = manager;
        this.mainView = mainView;

        // Window setup
        setTitle("TicketManager");
        setSize(700,480);
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

        // Page Title
        JLabel pageTitle = new JLabel("New Task");
        pageTitle.setFont(pageTitle.getFont().deriveFont(Font.PLAIN, 24f));
        pageTitle.setAlignmentX(CENTER_ALIGNMENT);
        topBar.setAlignmentX(CENTER_ALIGNMENT); // both centred so the title sits in the middle of the window

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
        addRow(form, 3, "Assignee (optional)", assigneeField, true);
        addRow(form, 4, "Due-Date (optional)", dueField, true);
        addRow(form, 5, "Description (optional)", descField, true);
        addRow(form, 6, "Dependencies (optional)", depsField, true);

        // Cancel/ Confirm
        JButton cancelButton = new JButton("Cancel");
        JButton confirmButton = new JButton("Confirm");
        cancelButton.setPreferredSize(new Dimension(110,28));
        confirmButton.setPreferredSize(new Dimension(110,28));

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER, 60, 10));
        bottom.add(cancelButton);
        bottom.add(confirmButton);

        // Actions
        helpButton.addActionListener(e -> help());
        saveExitButton.addActionListener(e -> saveAndExit());
        cancelButton.addActionListener(e -> goBack());
        confirmButton.addActionListener(e-> confirm());
        getRootPane().setDefaultButton(confirmButton); // enter = Confirm
        resetButton.addActionListener(e -> DatabaseReset.confirmAndReset(this, manager, this::goBack));

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

    // Validates everything first, then creates the ticket, so a bad input
    // never enters the system
    private void confirm(){
        String title = nameField.getText().trim();
        String assignee = assigneeField.getText().trim();
        String description = descField.getText().trim();

        // Name, assignee and description (the rules live in Ticket)
        try {
            Ticket.validateTitle(title);
            Ticket.validateAssignee(assignee);
            Ticket.validateDescription(description);
        } catch (IllegalArgumentException ex) {
            error(ex.getMessage());
            return;
        }

        // Due Date (optional)
        LocalDate due = null;
        String dueText = dueField.getText().trim();
        if (!dueText.isEmpty()) {
            try {
                due = Formats.parseDate(dueText);
            } catch (DateTimeParseException ex) {
                error("Due date must look like dd/mm/yyyy.");
                return;
            }
        }

        // Dependencies (optional, comma separated), 0 because this ticket has no ID yet
        List<Integer> deps;
        try {
            deps = manager.parseDependencies(depsField.getText(), 0);
        } catch (IllegalArgumentException ex) {
            error(ex.getMessage());
            return;
        }

        // Can't start or finish a ticket if its dependencies aren't Done or Closed
        Status status = selectedStatus();
        if (TicketManager.needsCompleteDependencies(status)) {
            String incomplete = manager.incompleteDependencyText(deps);
            if (!incomplete.isEmpty()) {
                error("Cannot create this ticket as " + Formats.formatStatus(status)
                        + " - it has incomplete dependencies:" + incomplete);
                return;
            }
        }

        //Everything is valid so create the ticket
        Ticket t = manager.createTicket(title);
        t.setStatus(status);
        t.setPriority(selectedPriority());
        if (!assignee.isEmpty()) t.setAssignee(assignee);
        if (!description.isEmpty()) t.setDescription(description);
        if (due != null) t.setDuedate(due);
        for (int depId : deps) t.addDependency(depId);

        if (!TicketStorage.save(manager)) {
            JOptionPane.showMessageDialog(this, "The ticket was created, but could not be saved to the database.",
                    "Save failed", JOptionPane.ERROR_MESSAGE);
        }
        goBack();
    }

    // help screen
    private void help(){
        String text = """
                      NEW TASK

                      Fill in the form and press Confirm to create a new ticket.
                      Only Name is required to create a ticket. Status defaults to To-Do, and Priority to Medium.

                      Buttons:
                        Confirm - Creates the ticket and returns you to the main view.
                        Cancel - Returns to main view without creating anything.
                        Save & Exit - Saves existing tickets and closes the program.
                        Reset - Deletes every ticket and starts fresh (asks first).
                      """;
        JOptionPane.showMessageDialog(this, text, "Help - New Task", JOptionPane.INFORMATION_MESSAGE);
    }

    // Close this window and go to the previous
    private void goBack(){
        dispose();
        mainView.refresh();
        mainView.setVisible(true);
    }

    // Save everything already in manager and quits
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

    private void error(String message) {
        JOptionPane.showMessageDialog(this, message, "Can't create ticket", JOptionPane.WARNING_MESSAGE);
    }
}
