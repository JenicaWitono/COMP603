import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Prints the main ticket list view (CLI table).
 */
public class MainView {
    
    private static final String ROW_FORMAT = "%-8s%-30s%-12s%-10s%-10s%n";
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yy");
    private static final int TITLE_CONTENT_WIDTH = 25; // max chars shown before "..."
    private static final int ROW_WIDTH = 8 + 30 + 12 + 10 + 10; // keep in sync with ROW_FORMAT
    
    //truncate long titles to have "..." instead
    private static String truncateTitle(String title) {
        if (title.length() <= TITLE_CONTENT_WIDTH) {
            return title;
        }
        // Leave room for the "..." within the content width
        return title.substring(0, TITLE_CONTENT_WIDTH - 3) + "...";
    }
    
    public static void print(List<Ticket> tickets) {
        System.out.printf(ROW_FORMAT, "ID", "Name", "Status", "Priority", "Due Date");
        System.out.println("-".repeat(ROW_WIDTH));
        
        for (Ticket t : tickets) {
            // Don't show completed tickets in the main view
            if (t.getStatus() == Status.DONE || t.getStatus() == Status.CLOSED) {
                continue;
            }
            
            String id = String.format("%03d", t.getId());
            String due = (t.getDuedate() == null) ? "-N/A-" : t.getDuedate().format(DATE_FORMAT);
            
            System.out.printf(ROW_FORMAT,
                    id,
                    truncateTitle(t.getTitle()),
                    formatStatus(t.getStatus()),
                    formatPriority(t.getPriority()),
                    due);
        }
        
        System.out.println("-".repeat(ROW_WIDTH));
    }
    
    // Converts enum constants into the display style from the mockup
    private static String formatStatus(Status status) {
        switch (status) {
            case TODO: return "To-Do";
            case IN_PROGRESS: return "In Progress";
            case IN_REVIEW: return "In Review";
            default: return status.toString();
        }
    }
    
    private static String formatPriority(Priority priority) {
        String s = priority.toString();
        return s.charAt(0) + s.substring(1).toLowerCase();
    }
}