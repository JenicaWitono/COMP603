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
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

// Edit screen, can be opened from the detail view edit button
// each field has an edit button, pressing it turns the field into an input
// and shows a confirm button next to it, while a field is being edited all other edit buttons disappear
public class EditTicketGUI extends TicketWindow{

    private final MainViewGUI mainView;
    private final DetailViewGUI detailView;
    private final Ticket ticket;

    // copy of the ticket's editable fields so that the real ticket is only changed when the confirm button has been pressed
    private String editedTitle;
    private String editedDescription;
    private Status editedStatus;
    private Priority editedPriority;
    private String editedAssignee;
    private LocalDate editedDue;
    private List<Integer> editedDeps;
    private boolean changed = false; // will turn true when a field has been edited

    // Inputs shown while a field is being edited
    private final JTextField nameEditor = new JTextField();
    private final JComboBox<String> statusEditor = new JComboBox<>(statusLabels());
    private final JComboBox<String> priorityEditor = new JComboBox<>(priorityLabels());
    private final JTextField assigneeEditor = new JTextField();
    private final JTextField dueEditor = new JTextField();
    private final JTextArea descEditor = new JTextArea(4, 20);
    private final JTextField depsEditor = new JTextField();
    private final JPanel form = new JPanel(new GridBagLayout());
    private final List<FieldRow> rows = new ArrayList<>();
    private FieldRow active = null; // the row currently being edited

    public EditTicketGUI(TicketManager manager, MainViewGUI mainView, DetailViewGUI detailView, Ticket ticket){
        super(manager, "Edit Ticket", 700, 560);
        this.mainView = mainView;
        this.detailView = detailView;
        this.ticket = ticket;

        // Starts the working copy from the ticket's current values
        editedTitle = ticket.getTitle();
        editedDescription = ticket.getDescription();
        editedStatus = ticket.getStatus();
        editedPriority = ticket.getPriority();
        editedAssignee = ticket.getAssignee();
        editedDue = ticket.getDuedate();
        editedDeps = new ArrayList<>(ticket.getDependencies());

        // Small reminder under the title about the two confirm steps
        JLabel hint = new JLabel("You must press confirm next to each change before pressing the Confirm button at the bottom.");
        hint.setFont(hint.getFont().deriveFont(Font.ITALIC, 11f));
        hint.setForeground(java.awt.Color.GRAY);
        hint.setBorder(new EmptyBorder(0,10,5,0)); // lines up with the title's left edge
        header.addExtra(hint);

        // Input setup
        descEditor.setLineWrap(true);
        descEditor.setWrapStyleWord(true);
        assigneeEditor.setToolTipText("Leave empty for unassigned");
        dueEditor.setToolTipText("dd/mm/yyyy (leave empty for no due date)");
        depsEditor.setToolTipText("Ticket IDs separated by commas (leave empty for none)");

        // Pressing enter in a one-line input confirms that field
        ActionListener enterConfirms = e -> {
            if(active != null) confirmEdit(active);
        };
        nameEditor.addActionListener(enterConfirms);
        assigneeEditor.addActionListener(enterConfirms);
        dueEditor.addActionListener(enterConfirms);
        depsEditor.addActionListener(enterConfirms);

        // The editable rows
        rows.add(new FieldRow("Name", false, readOnlyField(), nameEditor,
                () -> editedTitle,
                () -> nameEditor.setText(editedTitle),
                this::editedName));

        rows.add(new FieldRow("Status", false, readOnlyField(), statusEditor,
                () -> editedStatus.getLabel(),
                () -> statusEditor.setSelectedIndex(editedStatus.ordinal()),
                this::editedStatus));

        rows.add(new FieldRow("Priority", false, readOnlyField(), priorityEditor,
                () -> editedPriority.getLabel(),
                () -> priorityEditor.setSelectedIndex(editedPriority.ordinal()),
                this::editedPriority));

        rows.add(new FieldRow("Assignee", false, readOnlyField(), assigneeEditor,
                () -> Formats.textOrDash(editedAssignee),
                () -> assigneeEditor.setText(editedAssignee == null ? "" : editedAssignee),
                this::editedAssignee));

        rows.add(new FieldRow("Due Date", false, readOnlyField(), dueEditor,
                () -> Formats.formatDate(editedDue),
                () -> dueEditor.setText(editedDue == null ? "" : editedDue.format(Formats.DATE_FORMAT)),
                this::editedDue));

        rows.add(new FieldRow("Description", true, readOnlyArea(4), new JScrollPane(descEditor),
                () -> Formats.textOrDash(editedDescription),
                () -> {
                    descEditor.setText(editedDescription == null ? "" : editedDescription);
                    descEditor.setCaretPosition(0);
                },
                this::editedDescription));

        rows.add(new FieldRow("Dependencies", true, readOnlyArea(3), depsEditor,
                () -> manager.dependencyText(editedDeps),
                this::loadDepsEditor,
                this::editedDeps));

        // Lay the rows out [edit] [label] [input] [confirm]
        form.setBorder(new EmptyBorder(5,20,5,20));
        int gridRow = 0;
        for(FieldRow r : rows){
            addToForm(gridRow++, r);
        }

        // Last modified is automatically set, so no need for an edit button
        // it shows when the ticket was last edited
        JTextField lastModified = readOnlyField();
        lastModified.setText(Formats.formatDate(ticket.getLastModified()));
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

        // Cancel / Confirm
        JButton cancelButton = new JButton("Cancel");
        JButton confirmButton = new JButton("Confirm");
        cancelButton.setPreferredSize(new Dimension(110, 28));
        confirmButton.setPreferredSize(new Dimension(110, 28));

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER, 60, 1));
        bottom.add(cancelButton);
        bottom.add(confirmButton);

        // Actions
        cancelButton.addActionListener(e -> cancel());
        confirmButton.addActionListener(e -> confirmAll());

        add(formHolder, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);
    }

    // One editable row

    private class FieldRow {
        final String label;
        final boolean tall; // text area rows keep their buttons at the top
        final JButton editBtn = new JButton("Edit");
        final JButton confirmBtn = new JButton("Confirm");
        final JPanel valuePanel = new JPanel(new BorderLayout()); // swaps between the value and the input
        final JTextComponent viewText; // read only display of the current value
        final JComponent viewComp; // viewText or a scroll panel around it
        final JComponent editorComp; // the input shown while editing
        final Supplier<String> shownText; // text to display the current value
        final Runnable loadEditor; // puts the current value into the input
        final BooleanSupplier stageEditor; // reads the input, validates and stores it. false if invalid

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

    // Turns a field into its input; every other row's edit button will disappear
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

    // Checks the input, if it's valid the field shows the new value
    private void confirmEdit(FieldRow row) {
        if(!row.stageEditor.getAsBoolean()){
            return; // an error was shown, stays in edit mode so it can be fixed
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

    // Reading and checking each input (the text rules live in Ticket)
    private boolean editedName() {
        String text = nameEditor.getText().trim();
        try {
            Ticket.validateTitle(text);
        } catch (IllegalArgumentException ex) {
            error(ex.getMessage());
            return false;
        }
        editedTitle = text;
        return true;
    }

    private boolean editedStatus() {
        Status newStatus = Status.values()[statusEditor.getSelectedIndex()];
        if (newStatus.needsCompleteDependencies()) {
            String incomplete = manager.incompleteDependencyText(editedDeps);
            if (!incomplete.isEmpty()) {
                error("Cannot move this ticket to " + newStatus.getLabel()
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

    private boolean editedAssignee(){
        String text = assigneeEditor.getText().trim();
        try {
            Ticket.validateAssignee(text);
        } catch (IllegalArgumentException ex) {
            error(ex.getMessage());
            return false;
        }
        editedAssignee = text.isEmpty() ? null : text; // empty means unassigned
        return true;
    }

    private boolean editedDue(){
        String text = dueEditor.getText().trim();
        if (text.isEmpty()) {
            editedDue = null; // empty clears the due date
            return true;
        }
        try {
            editedDue = Formats.parseDate(text);
            return true;
        } catch (DateTimeParseException ex) {
            error("Due date must look like dd/mm/yyyy.");
            return false;
        }
    }

    private boolean editedDescription(){
        String text = descEditor.getText().trim();
        try {
            Ticket.validateDescription(text);
        } catch (IllegalArgumentException ex) {
            error(ex.getMessage());
            return false;
        }
        editedDescription = text.isEmpty() ? null : text;
        return true;
    }

    private boolean editedDeps(){
        List<Integer> newDeps;
        try {
            newDeps = manager.parseDependencies(depsEditor.getText(), ticket.getId());
        } catch (IllegalArgumentException ex) {
            error(ex.getMessage());
            return false;
        }
        // stops loops like #1 -> #2 -> #1, neither ticket could ever be started
        if(manager.wouldCreateCycle(ticket.getId(), newDeps)){
            error("These dependencies would create a loop (a ticket ends up depending on itself).");
            return false;
        }
        // a ticket that is already started can't pick up unfinished dependencies
        if(editedStatus.needsCompleteDependencies()){
            String incomplete = manager.incompleteDependencyText(newDeps);
            if(!incomplete.isEmpty()){
                error("This ticket is " + editedStatus.getLabel()
                        + " so its dependencies must be Done or Closed:" + incomplete);
                return false;
            }
        }
        editedDeps = newDeps;
        return true;
    }

    // IDs as "2, 5" for the dependencies input
    private void loadDepsEditor(){
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < editedDeps.size(); i++){
            if(i>0) sb.append(", ");
            sb.append(editedDeps.get(i));
        }
        depsEditor.setText(sb.toString());
    }

    // Leaving the screen

    // Bottom confirm button, writes the working copy to the ticket, saves and then returns to detail view
    private void confirmAll(){
        // if a field is still open, try to confirm it first (stays here if it's invalid)
        if(active != null){
            confirmEdit(active);
            if(active != null) return;
        }

        if(changed){
            // each setter also updates the last modified date
            ticket.setTitle(editedTitle);
            ticket.setDescription(editedDescription);
            ticket.setStatus(editedStatus);
            ticket.setPriority(editedPriority);
            ticket.setAssignee(editedAssignee);
            ticket.setDuedate(editedDue);
            ticket.setDependencies(editedDeps);
            if(!manager.save()){
                JOptionPane.showMessageDialog(this, "Your changes were made, but could not be saved to the database.",
                        "Save failed", JOptionPane.ERROR_MESSAGE);
            }

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

    // After a reset: close this page and the hidden detail page, and show the main view
    @Override
    protected void afterReset() {
        detailView.dispose();
        dispose();
        mainView.refresh();
        mainView.setVisible(true);
    }

    // X button acts like Cancel
    @Override
    protected void onWindowClosing(){
        cancel();
    }

    // Help screen
    @Override
    protected String helpText(){
        return """
                      EDIT TICKET

                      This page allows you to edit an existing ticket.
                      Press Edit next to the field you would like to change,
                      then press Confirm beside it.
                      To finish, press Confirm at the bottom of the page,
                      this will save the changes to the database.

                      Buttons:
                        Confirm (bottom) - Save your changes and return to the detail view.
                        Cancel - Discard your changes and return.
                        Save & Exit - Save existing tickets and close the program.
                        Reset - Delete every ticket and start fresh (asks first).
                      """;
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

    // Drop down labels, in the same order as the enum values
    private static String[] statusLabels(){
        String[] labels = new String[Status.values().length];
        for (Status s : Status.values()){
            labels[s.ordinal()] = s.getLabel();
        }
        return labels;
    }

    private static String[] priorityLabels(){
        String[] labels = new String[Priority.values().length];
        for (Priority p : Priority.values()){
            labels[p.ordinal()] = p.getLabel();
        }
        return labels;
    }
}
