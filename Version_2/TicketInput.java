/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Scanner;



/**
 *
 * @author Jamie
 */
public class TicketInput {
    
    public static Ticket userInput(Scanner scanner, TicketManager manager){
        
        // Title is required for createTicket() for ticket constructor
        System.out.print("Enter ticket title: ");
        String title = scanner.nextLine();
        
        Ticket ticket = manager.createTicket(title);
        
        // Description input optional for user
        System.out.println("Enter A Description of the ticket (optional): ");
        String description = scanner.nextLine();
        if(!description.trim().isEmpty()){ // If the description is empty will skip instead of overrighting
            ticket.setDescription(description);
        }
        
        // Assignee input optional for user
        System.out.println("Enter A Assignee (optional): ");
        String assignee = scanner.nextLine();
        if(!assignee.trim().isEmpty()){
            ticket.setAssignee(assignee);
        }
        
        // Priotty input optional for now but would like to make it required latter
        System.out.println("What Priorty Level Is This Ticket? (LOW, MEDIUM, HIGH): ");
        String priorty = scanner.nextLine().trim().toUpperCase();
        if(!priorty.isEmpty()){
            try{
                ticket.setPriority(Priority.valueOf(priorty));
            }catch (IllegalArgumentException e){ // Only allows values stored in Piortiy enum to pass
                System.out.println("Invalid priority entered");
            }
        }
        
        // Duedate input optional for user
        System.out.println("Enter due date (yyyy-mm-dd) (optional): "); // will change date input to dd/mm/yyyy but dont know how yet
        String duedate = scanner.nextLine().trim();
        if(!duedate.isEmpty()){
            try{
                ticket.setDuedate(LocalDate.parse(duedate));
            } catch (DateTimeParseException e){
                System.out.println("Invalid date format");
            }
        }   
        
        System.out.println("Ticket created successfully!");
        return ticket;
                
    }
  
}
