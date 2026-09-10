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
            return;
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
                    processBlock(fields, manager);
                    fields = new HashMap<>();
                }
                continue;
            }
            int colon = line.indexOf(':');
            if (colon == -1) continue;
            fields.put(line.substring(0, colon), line.substring(colon + 1));
        }
        if (!fields.isEmpty()) {
            processBlock(fields, manager);
        }
    }

    // Tries to load one ticket block; if it fails, marks that ID (if recoverable) as corrupted
    private static void processBlock(Map<String, String> fields, TicketManager manager) {
        try {
            manager.addLoadedTicket(buildTicket(fields));
        } catch (Exception e) {
            int id = extractId(fields);
            if (id >= 0) {
                System.out.println("Warning: ticket #" + id + " is corrupted and could not be loaded (" + e.getMessage() + ").");
                manager.markCorrupted(id);
            } else {
                System.out.println("Warning: a ticket block with no readable ID was skipped entirely.");
            }
        }
    }

    // Recovers just the ID from a broken block, separately from full parsing, so we can flag it
    private static int extractId(Map<String, String> fields) {
        try {
            return Integer.parseInt(fields.get("ID"));
        } catch (Exception e) {
            return -1;
        }
    }

    private static Ticket buildTicket(Map<String, String> f) {
        if (!f.containsKey("ID")) throw new IllegalStateException("missing ID");
        int id = Integer.parseInt(f.get("ID"));

        String title = f.get("Title");
        if (title == null || title.isBlank()) throw new IllegalStateException("missing title");

        String description = emptyToNull(f.get("Description"));
        String assignee = emptyToNull(f.get("Assignee"));

        if (!f.containsKey("Status")) throw new IllegalStateException("missing status");
        Status status = Status.valueOf(f.get("Status"));

        if (!f.containsKey("Priority")) throw new IllegalStateException("missing priority");
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