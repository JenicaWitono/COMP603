/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author Jamie
 */
import java.util.Scanner;
public class RemoveTicket {
    // Remove Ticket
    public static void removeTicket(Scanner scanner, TicketManager manager){
        
        try{ 
            int id = 0;
            Ticket ticket = null;
            
            //check if the ticket is corrupted
            ticket = manager.getTicket(id);
            if (manager.isCorrupted(id)) {
                System.out.println("Ticket #" + id + " is corrupted and cannot be accessed.");
                return;
            }
            if (ticket == null){
                System.out.println("No ticket found with the ID #"+id+". Please try again");
            }
            
            while(ticket == null){
                String idText = InputHelper.readLine(scanner, "Enter the ID of ticket to remove: ");
                try{
                    id = Integer.parseInt(idText);
                } catch (NumberFormatException e){
                    System.out.println(idText+" Is not a valid ticket ID.");
                    continue;
                }
                ticket = manager.getTicket(id);
                if (ticket == null){
                    System.out.println("No ticket found with the ID #"+id+". Please try again");
                }
            }
       
            boolean removed = manager.removeTicket(id);
            if(removed){
                System.out.println("Ticket #"+id+" Has been succsefully removed.");
            }
        } catch (InputHelper.BackException e){
            System.out.println("Remove cancelled.");
        }
   }
}
