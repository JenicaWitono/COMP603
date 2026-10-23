//written with assistance from Claude.ai
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Holds and manages all tickets in memory
public class TicketManager {
    //hashmap to store data
    private Map<Integer, Ticket> tickets = new HashMap<>();

    //assigning IDs to new tickets
    private int nextID = 1;
    // creates and stores a new ticket. Auto assigns id based on nextID
    public Ticket createTicket(String title) {
        Ticket t = new Ticket(nextID, title);
        tickets.put(nextID, t);
        nextID++;
        return t;
    }

    // In Progress, In Review and Done all need every dependency finished first
    public static boolean needsCompleteDependencies(Status s){
        return s == Status.IN_PROGRESS || s == Status.IN_REVIEW || s == Status.DONE;
    }

    // Turns text like "2, 5" into a list of IDs, checking each one exists
    // ticketId is the ticket being edited, use 0 for a new ticket (IDs start at 1)
    // throws an IllegalArgumentException with a message that can be shown to the user
    public List<Integer> parseDependencies(String text, int ticketId){
        List<Integer> deps = new ArrayList<>();
        if (text == null || text.trim().isEmpty()){
            return deps; // empty means no dependencies
        }
        for (String part : text.split(",")){
            int id;
            try {
                id = Integer.parseInt(part.trim());
            } catch (NumberFormatException e){
                throw new IllegalArgumentException("'" + part.trim() + "' is not a valid ticket ID.");
            }
            if (id == ticketId){
                throw new IllegalArgumentException("A ticket cannot depend on itself.");
            }
            if (!tickets.containsKey(id)){
                throw new IllegalArgumentException("No ticket found with ID #" + id + ".");
            }
            if (!deps.contains(id)){
                deps.add(id);
            }
        }
        return deps;
    }

    // One line per dependency that isn't Done or Closed yet, empty if they're all finished
    public String incompleteDependencyText(List<Integer> deps){
        StringBuilder sb = new StringBuilder();
        for (int depId : deps){
            Ticket dep = tickets.get(depId);
            if (dep != null && dep.getStatus() != Status.DONE && dep.getStatus() != Status.CLOSED){
                sb.append("\n #").append(Formats.formatId(dep.getId())).append(" - ").append(dep.getTitle());
            }
        }
        return sb.toString();
    }

    // One line per dependency for the detail and edit screens, "-" if there are none
    public String dependencyText(List<Integer> deps){
        if (deps.isEmpty()) return "-";

        StringBuilder sb = new StringBuilder();
        for (int depId : deps){
            sb.append("#").append(Formats.formatId(depId));
            Ticket dep = tickets.get(depId);
            if (dep != null){
                sb.append(" - ").append(dep.getTitle()).append(" (").append(Formats.formatStatus(dep.getStatus())).append(")");
            } else {
                sb.append(" (missing)");
            }
            sb.append("\n");
        }
        return sb.toString().trim();
    }

    // How many other tickets depend on this one
    public int countDependents(int id){
        int count = 0;
        for (Ticket t : tickets.values()){
            if (t.getDependencies().contains(id)) count++;
        }
        return count;
    }

    // Returns true if giving ticketId these dependencies would create a loop
    // (e.g. #1 depends on #2 and #2 depends on #1), which would block both forever
    public boolean wouldCreateCycle(int ticketId, List<Integer> newDeps){
        List<Integer> toVisit = new ArrayList<>(newDeps);
        List<Integer> visited = new ArrayList<>();
        while (!toVisit.isEmpty()){
            int id = toVisit.remove(toVisit.size() - 1);
            if (id == ticketId){
                return true; // followed the chain back to the ticket itself
            }
            if (visited.contains(id)){
                continue;
            }
            visited.add(id);
            Ticket t = tickets.get(id);
            if (t != null){
                toVisit.addAll(t.getDependencies());
            }
        }
        return false;
    }

    //returns all tickets as a list sorted by ID
    //MainView and DetailView will pull from here
    public List<Ticket> getAllTickets(){
        List<Ticket> list = new ArrayList<>(tickets.values());
        list.sort(Comparator.comparing(Ticket::getId));
        return list;
    }

    // Removes a ticket by ID and will return whether or not it existed
    public boolean removeTicket(int id){
        boolean removed = tickets.remove(id) != null;
        if (removed) {
            // clean up any other ticket that depended on this one
            for (Ticket t : tickets.values()) {
                t.removeDependency(id);
            }
        }
        return removed;
    }

    // Inserts a ticket during load, preserving its saved ID, and keeps nextID ahead of it
    public void addLoadedTicket(Ticket t){
        tickets.put(t.getId(), t);
        if (t.getId() >= nextID) {
            nextID = t.getId() + 1;
        }
    }

    // Clears all tickets and resets ID counter
    public void reset(){
        tickets.clear();
        nextID = 1;
    }

    // Ticket lookup, looks up by ID
    public Ticket getTicket(int id){
        return tickets.get(id);
    }

}
