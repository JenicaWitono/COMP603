
import java.util.Scanner;

/**
 *
 * @author jenic + J_DOG234
 */
public class Main {
    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        TicketManager manager = new TicketManager();
        
        //command to create new ticket
        while (true) {
            System.out.println("> ");
            String input = scan.nextLine().trim();
            
            if (input.equalsIgnoreCase("new")) {
                createNewTicket (scan, manager);
            }
            
            if (input.equalsIgnoreCase("exit")){
                break;
            }
        }
    }
    
    //creating a new ticket method
    public static void createNewTicket(Scanner scan, TicketManager manager) {
        
        Ticket ticket = TicketInput.userInput(scan, manager);
        System.out.println("New ticket created (ID: " + ticket.getId() + ")");
        System.out.println(ticket.toString());
        
    }
}
