//written with assistance from Claude.ai
import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;

// Represents a single ticket and its data
public class Ticket {
    // Limits for the text fields (they match the database column sizes)
    public static final int MAX_TITLE_LENGTH = 500;
    public static final int MAX_TITLE_WORDS = 50;
    public static final int MAX_DESCRIPTION_LENGTH = 2000;
    public static final int MAX_ASSIGNEE_LENGTH = 100;

    private int id;
    private String title;
    private String description;
    private String assignee;
    private Status status;
    private LocalDate duedate;
    private LocalDate lastModified;
    private List<Integer> dependencies;
    private Priority priority;

    // Constructor for creating a brand new ticket (applies defaults)
    public Ticket(int id, String title) {
        validateTitle(title);
        this.id = id;
        this.title = title;
        this.status = Status.TODO;
        this.lastModified = LocalDate.now();
        this.priority = Priority.MEDIUM;
        this.dependencies = new ArrayList<>();
    }

    // Constructor for rebuilding a ticket from saved data (no defaults applied)
    public Ticket(int id, String title, String description, String assignee, Status status,
                  LocalDate duedate, LocalDate lastModified, Priority priority) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.assignee = assignee;
        this.status = status;
        this.duedate = duedate;
        this.lastModified = lastModified;
        this.priority = priority;
        this.dependencies = new ArrayList<>();
    }

    // Validation rules, the GUI screens call these too so the rules are only written once
    // each one throws an IllegalArgumentException with a message that can be shown to the user
    public static void validateTitle(String title) {
        //exception for empty titles
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty.");
        }
        //word limit exception (50)
        if (title.trim().split("\\s+").length > MAX_TITLE_WORDS) {
            throw new IllegalArgumentException("Name limit is " + MAX_TITLE_WORDS + " words.");
        }
        //character limit exception (500)
        if (title.length() > MAX_TITLE_LENGTH) {
            throw new IllegalArgumentException("Name is too long (" + MAX_TITLE_LENGTH + " char max).");
        }
    }

    public static void validateDescription(String description) {
        if (description != null && description.length() > MAX_DESCRIPTION_LENGTH) {
            throw new IllegalArgumentException("Description is too long (" + MAX_DESCRIPTION_LENGTH + " char max).");
        }
    }

    public static void validateAssignee(String assignee) {
        if (assignee != null && assignee.length() > MAX_ASSIGNEE_LENGTH) {
            throw new IllegalArgumentException("Assignee is too long (" + MAX_ASSIGNEE_LENGTH + " char max).");
        }
    }

    public int getId(){
        return id;
    }

    public String getTitle(){
        return title;
    }

    public Priority getPriority(){
        return priority;
    }

    public LocalDate getDuedate(){
        return duedate;
    }

    public String getDescription(){
        return description;
    }

    public String getAssignee(){
        return assignee;
    }

    public Status getStatus(){
        return status;
    }

    public LocalDate getLastModified(){
        return lastModified;
    }

    // Returns a read-only copy, changes go through addDependency / removeDependency / setDependencies
    public List<Integer> getDependencies(){
        return List.copyOf(dependencies);
    }

    // Title set method
    public void setTitle(String title) {
        validateTitle(title);
        this.title = title;
        updateLastModified();
    }

    // Description set method
    public void setDescription(String description){
        validateDescription(description);
        this.description = description;
        updateLastModified();
    }

    // Assignee set method
    public void setAssignee(String assignee){
        validateAssignee(assignee);
        this.assignee = assignee;
        updateLastModified();
    }

    // Status set method
    public void setStatus(Status status){
        this.status = status;
        updateLastModified();
    }

    // Priority set method
    public void setPriority(Priority priority){
        this.priority = priority;
        updateLastModified();
    }

    // Duedate set method
    public void setDuedate(LocalDate duedate){
        this.duedate = duedate;
        updateLastModified();
    }

    // Replaces the whole dependency list (used by the edit screen and DerbyTicketDAO when loading)
    // doesn't change last modified, so loading keeps the saved date
    public void setDependencies(List<Integer> dependencies){
        this.dependencies = (dependencies != null) ? new ArrayList<>(dependencies) : new ArrayList<>();
    }

    // Updates the last modified to the date of the day
    public void updateLastModified(){
        this.lastModified = LocalDate.now();
    }

    // adding a dependency
    public void addDependency(int ticketId){
        // to handle the error of a ticket being dependent on itself
        if (ticketId == this.id){
            throw new IllegalArgumentException("A ticket cannot depend on itself");
        }
        if (!dependencies.contains(ticketId)){
            dependencies.add(ticketId);
            updateLastModified();
        }
    }

    // removing a dependency by ID, only counts as a change if it was actually there
    public void removeDependency(int ticketId){
        if (dependencies.remove(Integer.valueOf(ticketId))){
            updateLastModified();
        }
    }

    // Debug/ summary string for a ticket (used only in testing)
    @Override
    public String toString(){
        return "Ticket{" +
                "id= " + id +
                ", title= '" + title + '\'' +
                ", description= '" + description + '\'' +
                ", assignee= '" + assignee + '\'' +
                ", status= " + status +
                ", priority= " + priority +
                ", duedate= " + duedate +
                ", lastModified= " + lastModified +
                '}';
    }
}
