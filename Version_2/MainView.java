// Generated with assistance from Claude
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class MainView extends AbstractTicketView {
    
    //main view base formatting; title will get truncated if too long
    private static final String ROW_FORMAT = "%-8s%-30s%-14s%-11s%-10s%n";
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yy");
    private static final int TITLE_CONTENT_WIDTH = 25;
    
    //header for main view
    public static void print(List<Ticket> tickets) {
        printTable(filterActive(tickets), "Main View");
    }
    
    //header for archived tickets
    public static void printArchived(List<Ticket> tickets) {
        printTable(filterArchived(tickets), "Archived Tickets");
    }
    
    //truncate title if too long
    private static String truncateTitle(String title) {
        if (title.length() <= TITLE_CONTENT_WIDTH) {
            return title;
        }
        return title.substring(0, TITLE_CONTENT_WIDTH - 3) + "...";
    }
    
    //printing main view
    private static void printTable(List<Ticket> tickets, String heading) {
        System.out.println(centerText(heading, ROW_WIDTH));
        System.out.printf(ROW_FORMAT, "ID", "Name", "Status", "Priority", "Due Date");
        System.out.println("-".repeat(ROW_WIDTH));

        for (Ticket t : tickets) {
            String id = String.format("%03d", t.getId());
            String due = (t.getDuedate() == null) ? "-N/A-" : t.getDuedate().format(DATE_FORMAT);

            System.out.printf(ROW_FORMAT,
                    id,
                    truncateTitle(t.getTitle()),
                    formatStatus(t.getStatus()),
                    formatPriority(t.getPriority()),
                    due);
        }
        //border
        System.out.println("-".repeat(ROW_WIDTH));
    }
    
    //printing archive view when sorted
    private static List<Ticket> filterActive(List<Ticket> tickets) {
        List<Ticket> result = new ArrayList<>();
        for (Ticket t : tickets) {
            if (t.getStatus() != Status.DONE && t.getStatus() != Status.CLOSED) {
                result.add(t);
            }
        }
        return result;
    }
    
    //printing archive view
    private static List<Ticket> filterArchived(List<Ticket> tickets) {
        List<Ticket> result = new ArrayList<>();
        for (Ticket t : tickets) {
            if (t.getStatus() == Status.DONE || t.getStatus() == Status.CLOSED) {
                result.add(t);
            }
        }
        return result;
    }

}