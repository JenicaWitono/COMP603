public enum Status {
    TODO, IN_PROGRESS, IN_REVIEW, DONE, CLOSED;
    
    // Text Shown in THE GUI
    public String getLabel() {
       return switch (this) {
            case TODO -> "To-Do";
            case IN_PROGRESS -> "In Progress";
            case IN_REVIEW -> "In Review";
            case DONE -> "Done";
            case CLOSED -> "Closed";
        };
    }
    
    // Done or closed: the ticket counts as archived / complete
    public boolean isFinished(){
        return this == DONE || this == CLOSED;
    }
    
    // Moving to one of these needs all dependencies to be finished first
    public boolean needsCompleteDependencies(){
        return this == IN_PROGRESS || this == IN_REVIEW || this == DONE;
    }

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
