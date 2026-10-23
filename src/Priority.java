public enum Priority {
    LOW, MEDIUM, HIGH;
    
    // Text shown in GUI
    public String getLabel(){
        String s = name();
        return s.charAt(0) + s.substring(1).toLowerCase();
    }   
    
    //accepts flexible input
    public static Priority parse(String input) {
        String normalized = input.trim().toUpperCase();
        return switch (normalized) {
            case "LOW" -> LOW;
            case "MEDIUM", "MED" -> MEDIUM;
            case "HIGH" -> HIGH;
            default -> throw new IllegalArgumentException("Unknown priority: " + input);
        };
    }
}
