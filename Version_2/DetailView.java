//written with assistance from Claude.ai
import java.time.format.DateTimeFormatter;
import java.util.List;

public class DetailView extends AbstractTicketView {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final int LABEL_WIDTH = 13; // length of "Last modified", the longest label

    private static String formatLabel(String label) {
        return String.format("%-" + LABEL_WIDTH + "s : ", label);
    }

    //prints all the details of a ticket
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
    //printing description to match the table
    private static void printDescription(String text) {
        String label = formatLabel("Description");
        int indent = label.length();
        int contentWidth = ROW_WIDTH - indent;
        //should print -N/A- if empty
        if (text == null || text.isBlank()) {
            System.out.println(label + "-N/A-");
            return;
        }
        
        //description formatting so it doesn't wrap around the field name
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
    //printing the formatted description
    private static void printDescLine(String label, int indent, String content, boolean first) {
        if (first) {
            System.out.println(label + content);
        } else {
            System.out.println(" ".repeat(indent) + content);
        }
    }
    //formatting dependencies to print -N/A- if empty
    private static String formatDependencies(List<Integer> deps) {
        if (deps == null || deps.isEmpty()) return "-N/A-";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < deps.size(); i++) {
            if (i > 0) sb.append(", ");
            sb.append(deps.get(i));
        }
        return sb.toString();
    }
}
