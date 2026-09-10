import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileStorage {

    public static void save(String filepath, TicketManager manager) {
        StringBuilder sb = new StringBuilder();

        for (Ticket t : manager.getAllTickets()) {
            sb.append("ID:").append(t.getId()).append("\n");
            sb.append("Title:").append(t.getTitle()).append("\n");
            sb.append("Description:").append(nullToEmpty(t.getDescription())).append("\n");
            sb.append("Assignee:").append(nullToEmpty(t.getAssignee())).append("\n");
            sb.append("Status:").append(t.getStatus()).append("\n");
            sb.append("Priority:").append(t.getPriority()).append("\n");
            sb.append("DueDate:").append(t.getDuedate() == null ? "" : t.getDuedate()).append("\n");
            sb.append("LastModified:").append(t.getLastModified()).append("\n");
            sb.append("\n"); // blank line separates tickets
        }

        try {
            Files.writeString(Paths.get(filepath), sb.toString());
        } catch (IOException e) {
            System.out.println("Failed to save tickets: " + e.getMessage());
        }
    }

    public static void load(String filepath, TicketManager manager) {
        Path path = Paths.get(filepath);
        if (!Files.exists(path)) {
            return; // no save file yet, e.g. first run
        }

        List<String> lines;
        try {
            lines = Files.readAllLines(path);
        } catch (IOException e) {
            System.out.println("Failed to load tickets: " + e.getMessage());
            return;
        }

        Map<String, String> fields = new HashMap<>();
        for (String line : lines) {
            if (line.isBlank()) {
                if (!fields.isEmpty()) {
                    manager.addLoadedTicket(buildTicket(fields));
                    fields = new HashMap<>();
                }
                continue;
            }
            int colon = line.indexOf(':');
            if (colon == -1) continue; // skip a malformed line rather than crash
            fields.put(line.substring(0, colon), line.substring(colon + 1));
        }
        if (!fields.isEmpty()) { // in case the file doesn't end with a blank line
            manager.addLoadedTicket(buildTicket(fields));
        }
    }

    private static Ticket buildTicket(Map<String, String> f) {
        int id = Integer.parseInt(f.get("ID"));
        String title = f.get("Title");
        String description = emptyToNull(f.get("Description"));
        String assignee = emptyToNull(f.get("Assignee"));
        Status status = Status.valueOf(f.get("Status"));
        Priority priority = Priority.valueOf(f.get("Priority"));
        String dueStr = f.get("DueDate");
        LocalDate duedate = (dueStr == null || dueStr.isEmpty()) ? null : LocalDate.parse(dueStr);
        String lastModStr = f.get("LastModified");
        LocalDate lastModified = (lastModStr == null || lastModStr.isEmpty()) ? LocalDate.now() : LocalDate.parse(lastModStr);
        return new Ticket(id, title, description, assignee, status, duedate, lastModified, priority);
    }

    private static String nullToEmpty(String s) {
        return s == null ? "" : s;
    }

    private static String emptyToNull(String s) {
        return (s == null || s.isEmpty()) ? null : s;
    }
}