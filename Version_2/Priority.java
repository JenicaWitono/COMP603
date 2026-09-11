public enum Priority {
    LOW, MEDIUM, HIGH;
    
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
