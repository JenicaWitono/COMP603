public enum Priority {
    LOW, MEDIUM, HIGH;
    
    // Text shown in GUI
    public String getLabel(){
        String s = name();
        return s.charAt(0) + s.substring(1).toLowerCase();
    }   
    
}
