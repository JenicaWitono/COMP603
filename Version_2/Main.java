
import java.util.Scanner;

/**
 *
 * @author jenic
 */
public class Main {
    //handling global commands
    private static boolean handleGlobalCommands(String input, TicketManager manager) {
        //back command - returns user to the main view
        if (input.equalsIgnoreCase("back")) {
            System.out.println("Returning to main menu...");
            return true; // tells caller to stop what it's doing
        }
        
        //exit command - save and exit the application
        if (input.equalsIgnoreCase("exit")) {
            System.out.println("Saving (placeholder)... and exiting.");
            // manager.saveToJson("tickets.json");   // placeholder
            System.exit(0);
        }
        
        // no global command detected
        return false; 
    }

    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        TicketManager manager = new TicketManager();
        
        //command to create new ticket
        while (true) {
            System.out.println("> ");
            String input = scan.nextLine().trim();
            
            //check for global command
            if (handleGlobalCommands(input, manager)) {
                continue;
            }
            
            if (input.equalsIgnoreCase("new")) {
                createNewTicket (scan, manager);
                continue;
            }
            
            System.out.println("Unknown command.");
        }
    }
    
  //creating a new ticket method
    public static void createNewTicket(Scanner scan, TicketManager manager) {
        
        Ticket ticket = TicketInput.userInput(scan, manager);
        System.out.println("New ticket created (ID: " + ticket.getId() + ")");
        System.out.println(ticket.toString());
        
    }
}
