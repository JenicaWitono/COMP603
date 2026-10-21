//written with assistance from Claude.ai
import java.util.Scanner;
public class RemoveTicket {
    // Prompts for a ticket ID and removes it
    public static void removeTicket(Scanner scanner, TicketManager manager){
        try{ 
            int id;
            Ticket ticket = null;
            // Loops until a existing  ticket ID is entered
            while(ticket == null){
                
                String idText = InputHelper.readLine(scanner, "Enter the ID of ticket to remove: ");
                try{
                    id = Integer.parseInt(idText);
                } catch (NumberFormatException e){
                    System.out.println(idText+" is not a valid ticket ID.");
                    continue;
                }
                
                ticket = manager.getTicket(id);
                if (ticket == null){
                    System.out.println("No ticket found with the ID #"+id+". Please try again");
                }
            }
            
            // Deletes the ticket and reports result
            boolean removed = manager.removeTicket(ticket.getId());
            if(removed){
                System.out.println("Ticket #"+ticket.getId()+" has been successfully removed.");
            }
        } catch (InputHelper.BackException e){
            System.out.println("Remove cancelled.");
        }
   }
}