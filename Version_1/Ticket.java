/**
 *
 * @author jenic
 */
import java.time.LocalDate;
import java.util.List;

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
    
    public Ticket(int id, String title) {
        this.id = id;
        this.title = title;
        this.status = Status.TODO;
        this.lastModified = LocalDate.now();
        this.priority = Priority.MEDIUM;
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
    
    public void updateLastModified(){
        this.lastModified = LocalDate.now();
    }
}
