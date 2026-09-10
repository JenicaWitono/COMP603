/**
 *
 * @author jenic + the J_DOG
 */
import java.time.LocalDate;
import java.util.List;

public class Ticket {
    private int id;
    private String title;
    private String description;
    private String assignee;
    private Status status; // ADD
    private LocalDate duedate;
    private LocalDate lastModified;
    private List<Integer> dependencies; // ADD
    private Priority priority;
    
    // Required fields for ticket object
    public Ticket(int id, String title) {
        this.id = id;
        this.title = title;
        this.status = Status.TODO;
        this.lastModified = LocalDate.now();
        this.priority = Priority.MEDIUM;
    }
    
    // Used only when rebuilding a ticket from saved data — skips the "new ticket" defaults
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
    
     //title setter
    public void setTitle(String title) {
        //exception for empty titles
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Title cannot be empty.");
        }
        //word limit exception
        if (title.split("\\s+").length > 50) {
            throw new IllegalArgumentException("Title limit is 50 words.");
        }
        this.title = title;
        updateLastModified();
    }
    
    // Description set method
    public void setDescription(String description){
        this.description = description;
        updateLastModified();
    }
    
    // Assigne set method
    public void setAssignee(String assignee){
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
    
    //
    
    public void updateLastModified(){
        this.lastModified = LocalDate.now();
    }
    
    // output
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
