import java.util.Scanner;
public class RemoveTicket {
    // Remove Ticket
    public static void removeTicket(Scanner scanner, TicketManager manager){
        try{ 
            int id;
            Ticket ticket = null;

            while(ticket == null){
                String idText = InputHelper.readLine(scanner, "Enter the ID of ticket to remove: ");
                try{
                    id = Integer.parseInt(idText);
                } catch (NumberFormatException e){
                    System.out.println(idText+" is not a valid ticket ID.");
                    continue;
                }
                if (manager.isCorrupted(id)) {
                    System.out.println("Ticket #" + id + " is corrupted and cannot be removed this way — it was never fully loaded.");
                    return;
                }
                ticket = manager.getTicket(id);
                if (ticket == null){
                    System.out.println("No ticket found with the ID #"+id+". Please try again");
                }
            }

            boolean removed = manager.removeTicket(ticket.getId());
            if(removed){
                System.out.println("Ticket #"+ticket.getId()+" has been successfully removed.");
            }
        } catch (InputHelper.BackException e){
            System.out.println("Remove cancelled.");
        }
   }
}