//written with assistance from Claude.ai
public abstract class AbstractTicketView {

    // Shared row width used by both the table view and the detail view,
    // so their border widths always stay in sync
    protected static final int ROW_WIDTH = 8 + 30 + 14 + 11 + 10;

    // Converts a Status into the display label
    protected static String formatStatus(Status status) {
        switch (status) {
            case TODO: return "To-Do";
            case IN_PROGRESS: return "In Progress";
            case IN_REVIEW: return "In Review";
            case DONE: return "Done";
            case CLOSED: return "Closed";
            default: return status.toString();
        }
    }

    // Converts a Priority into the display label
    protected static String formatPriority(Priority priority) {
        String s = priority.toString();
        return s.charAt(0) + s.substring(1).toLowerCase();
    }

    // Centers text by padding both sides with spaces
    protected static String centerText(String text, int width) {
        int totalPadding = width - text.length();
        if (totalPadding <= 0) return text;
        int left = totalPadding / 2;
        int right = totalPadding - left;
        return " ".repeat(left) + text + " ".repeat(right);
    }
}
