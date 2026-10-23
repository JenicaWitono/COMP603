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

}
