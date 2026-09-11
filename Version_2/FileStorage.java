// Generated with assistance from Claude
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
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

        writeToFile(filepath, sb.toString());
    }

    // Writes a String out to disk using BufferedWriter/FileWriter
    private static void writeToFile(String filepath, String content) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filepath))) {
            writer.write(content);
        } catch (IOException e) {
            System.out.println("Failed to save tickets: " + e.getMessage());
        }
    }

    public static void load(String filepath, TicketManager manager) {
        File file = new File(filepath);
        if (!file.exists()) {
            return; // no save file yet, e.g. first run
        }

        List<String> lines = readLinesFromFile(file);
        if (lines == null) {
            return; // read failed; message already printed
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

    // Reads every line of a file into a List, using BufferedReader/FileReader
    private static List<String> readLinesFromFile(File file) {
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            System.out.println("Failed to load tickets: " + e.getMessage());
            return null;
        }
        return lines;
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