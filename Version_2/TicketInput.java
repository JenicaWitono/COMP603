//written with assistance from Claude.ai
import java.time.LocalDate;
import java.util.Scanner;
public class TicketInput {
    
    // Walks the user through creating a new ticket, field by field
    public static Ticket userInput(Scanner scanner, TicketManager manager){
        Ticket ticket = null;
        try{ 
      
        // Title is required for createTicket() for ticket constructor
        String title = InputHelper.promptUntilValid(scanner, "Enter ticket title: ", false, TicketInput::validateTitle);
        ticket = manager.createTicket(title);
        
        // Description input optional for user
        String description = InputHelper.readLine(scanner, "Enter a Description of the ticket (optional): ");
        if(!description.trim().isEmpty()){ // If the description is empty will skip instead of overrighting
            ticket.setDescription(description);
        }
        
        // Assignee input optional for user
        String assignee = InputHelper.readLine(scanner, "Enter an Assignee (optional): ");
        if(!assignee.trim().isEmpty()){ // If the description is empty will skip instead of overrighting
            ticket.setAssignee(assignee);
        }
        
        // Status input optional as will defualt to TODO if no answer
        Status status = InputHelper.promptUntilValid(scanner,
                "What is the Status of this Ticket? (To Do, In Progress, In Review, Done, Closed): ",
                true,
                Status::parse); 
        if (status != null){
            ticket.setStatus(status);
        }
        
        // Priority input optional as will deagult to MEDIUM if no answer
        Priority priority  = InputHelper.promptUntilValid(scanner,
                "What Priority Level is This Ticket? (Low, Medium, High): ",
                true,
                Priority::parse);
        if (priority != null){
            ticket.setPriority(priority);
        }
        
        // Dependencies are optional, comma separated list of ticket IDs
        String depsInput = InputHelper.readLine(scanner, "Enter dependency ticket IDs, comma separated (optional): ");
        if (!depsInput.isEmpty()){
            for (String part : depsInput.split(",")){
                try {
                    int depId = Integer.parseInt(part.trim());
                    if (manager.getTicket(depId) != null){
                        ticket.addDependency(depId);
                        System.out.println("Added dependency on ticket #" + depId + ".");
                    } else {
                        System.out.println("No ticket found with ID #"+depId+" - skipped.");
                    }
                } catch (NumberFormatException e){
                    System.out.println("'"+part.trim()+"' is not a valid ID - skipped.");
                } catch (IllegalArgumentException e){
                    System.out.println(e.getMessage() + " - skipped.");
                }
            }
        }
        
        // Duedate input optional for user
        LocalDate duedate = InputHelper.promptUntilValid(scanner, 
                "Enter due date (dd/mm/yyyy) (optional): ",
                true,
                InputHelper::parseDate);   // was LocalDate::parse
        
        if (duedate != null){
            ticket.setDuedate(duedate);
        }

        System.out.println("Ticket created successfully!");
        return ticket;
        } catch (InputHelper.BackException e){
            // User backed out mid-creation, undo the partial ticket
            if (ticket != null){
                manager.removeTicket(ticket.getId());
            }
            System.out.println("Ticket creation cancelled.");
            return null;
        } 
    }
    
    // Validates a title before ticket creation — same rules as Ticket.setTitle()
    private static String validateTitle(String input){
        if (input == null || input.trim().isEmpty()) {
            throw new IllegalArgumentException("Title cannot be empty.");
        }
        if (input.split("\\s+").length > 50) {
            throw new IllegalArgumentException("Title limit is 50 words.");
        }
        return input;
    }
}
