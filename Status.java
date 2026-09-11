/**
 *
 * @author jenic
 */
// All the possible states a ticket can be in
public enum Status {
    TODO, IN_PROGRESS, IN_REVIEW, DONE, CLOSED;

    // Accepts flexible input: "in progress", "IN-PROGRESS", "to do", etc.
    public static Status parse(String input) {
        String normalized = input.trim().toUpperCase()
                .replace("-", " ")
                .replace("_", " ")
                .replaceAll("\\s+", " ");

        return switch (normalized) {
            case "TODO", "TO DO" -> TODO;
            case "IN PROGRESS" -> IN_PROGRESS;
            case "IN REVIEW" -> IN_REVIEW;
            case "DONE" -> DONE;
            case "CLOSED" -> CLOSED;
            default -> throw new IllegalArgumentException("Unknown status: " + input);
        };
    }
}
