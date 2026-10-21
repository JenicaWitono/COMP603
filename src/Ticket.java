//written with assistance from Claude.ai
import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;

// Represents a single ticket and its data
public class Ticket {
    private int id;
    private String title;
    private String description;
    private String assignee;
    private Status status;
    private LocalDate duedate;
    private LocalDate lastModified;
    private List<Integer> dependencies;
    private Priority priority;
    
    // Constructor for creating a brand new ticket (applies defualts)
    public Ticket(int id, String title) {
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
    
    public List<Integer> getDependencies(){
       if (dependencies == null){
           dependencies = new ArrayList<>();
       }
       return dependencies;
   }

    // title setter with a validatoin
    public void setTitle(String title) {
        //exception for empty titles
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Title cannot be empty.");
        }
        //character limit exception (500)
        if (title.length() > 500) {
            throw new IllegalArgumentException("Title cannot exceed 500 characters.");
        }
        this.title = title;
        updateLastModified();
    }

    // Description set method
    public void setDescription(String description){
        if (description != null && description.length() > 2000) {
            throw new IllegalArgumentException("Description cannot exceed 2000 characters.");
        }
        this.description = description;
        updateLastModified();
    }
    
    // Assigne set method
    public void setAssignee(String assignee){
        //check if the length is too long
        if (assignee != null && assignee.length() > 100) {
            throw new IllegalArgumentException("Assignee cannot exceed 100 characters.");
        }
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
    
    // Used by FileStorage when reconstructing a ticket's dependency list from saved data
    public void setDependencies(List<Integer> dependencies){
        this.dependencies = (dependencies != null) ? dependencies : new ArrayList<>();
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
    
    // removing a dependency by ID
    public void removeDependency(int ticketID){
        dependencies.remove(Integer.valueOf(ticketID));
        updateLastModified();
    }
    
    // Debug/ summary string for a ticket (used only in testing)
    @Override
    public String toString(){
        return"Ticket{" +
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
