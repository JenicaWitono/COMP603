import java.time.format.DateTimeFormatter;
import java.util.List;

public class DetailView {
    
    //setting borders and date formatting
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final int ROW_WIDTH = 8 + 30 + 14 + 11 + 10; // matches MainView's ROW_WIDTH
    private static final int LABEL_WIDTH = 13; // length of "Last modified", the longest label

    // Pads labels so every ':' lines up in the same column
    private static String formatLabel(String label) {
        return String.format("%-" + LABEL_WIDTH + "s : ", label);
    }
    
    //printing all the elements with formatting
    public static void print(Ticket t) {
        System.out.println("-".repeat(ROW_WIDTH));
        System.out.println(centerText("Detailed View", ROW_WIDTH));

        System.out.println(formatLabel("ID") + String.format("%03d", t.getId()));
        System.out.println(formatLabel("Name") + t.getTitle());
        System.out.println(formatLabel("Status") + formatStatus(t.getStatus()));
        System.out.println(formatLabel("Priority") + formatPriority(t.getPriority()));

        String due = (t.getDuedate() == null) ? "-N/A-" : t.getDuedate().format(DATE_FORMAT);
        System.out.println(formatLabel("Due Date") + due);

        printDescription(t.getDescription());

        System.out.println();
        System.out.println(formatLabel("Dependencies") + formatDependencies(t.getDependencies()));
        System.out.println(formatLabel("Last modified") + t.getLastModified().format(DATE_FORMAT));
        System.out.println("-".repeat(ROW_WIDTH));
    }

    // Centers text within a given width by padding both sides with spaces
    private static String centerText(String text, int width) {
        int totalPadding = width - text.length();
        if (totalPadding <= 0) return text;
        int left = totalPadding / 2;
        int right = totalPadding - left;
        return " ".repeat(left) + text + " ".repeat(right);
    }

    // Prints "Description : <first line of text>", wrapping continuation
    // lines indented to line up under where the text starts, not the label
    private static void printDescription(String text) {
        String label = formatLabel("Description");
        int indent = label.length();
        int contentWidth = ROW_WIDTH - indent;

        if (text == null || text.isBlank()) {
            System.out.println(label + "-N/A-");
            return;
        }

        StringBuilder line = new StringBuilder();
        boolean firstLine = true;

        for (String word : text.split("\\s+")) {
            if (line.length() + word.length() + 1 > contentWidth) {
                printDescLine(label, indent, line.toString(), firstLine);
                firstLine = false;
                line.setLength(0);
            }
            if (line.length() > 0) line.append(" ");
            line.append(word);
        }
        if (line.length() > 0) {
            printDescLine(label, indent, line.toString(), firstLine);
        }
    }
    
    //spacing for the description
    private static void printDescLine(String label, int indent, String content, boolean first) {
        if (first) {
            System.out.println(label + content);
        } else {
            System.out.println(" ".repeat(indent) + content);
        }
    }
    
    //formatting dependecies
    private static String formatDependencies(List<Integer> deps) {
        if (deps == null || deps.isEmpty()) return "-N/A-";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < deps.size(); i++) {
            if (i > 0) sb.append(", ");
            sb.append(deps.get(i));
        }
        return sb.toString();
    }
    
    //formatting status
    private static String formatStatus(Status status) {
        switch (status) {
            case TODO: return "To-Do";
            case IN_PROGRESS: return "In Progress";
            case IN_REVIEW: return "In Review";
            case DONE: return "Done";
            case CLOSED: return "Closed";
            default: return status.toString();
        }
    }
    
    //formatting priority
    private static String formatPriority(Priority priority) {
        String s = priority.toString();
        return s.charAt(0) + s.substring(1).toLowerCase();
    }
}