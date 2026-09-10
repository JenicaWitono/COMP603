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
        Ticket ticket = null;
        try{ 
      
        // Title is required for createTicket() for ticket constructor
        String title = InputHelper.readLine(scanner, "Enter ticket title: ");
        ticket = manager.createTicket(title);
        
        // Description input optional for user
        String description = InputHelper.readLine(scanner, "Enter A Description of the ticket (optional): ");
        if(!description.trim().isEmpty()){ // If the description is empty will skip instead of overrighting
            ticket.setDescription(description);
        }
        
        // Assignee input optional for user
        String assignee = InputHelper.readLine(scanner, "Enter A Assignee (optional_: ");
        if(!assignee.trim().isEmpty()){ // If the description is empty will skip instead of overrighting
            ticket.setAssignee(assignee);
        }
        
        // Status input optional as will defualt to TODO if no answer
        Status status = InputHelper.promptUntilValid(scanner,
                "What Status would you like this ticket to be at? (TODO, IN_PROGRESS, IN_REVIEW, DONE, CLOSED)",
                true,
                Status::valueOf);
        if (status != null){
            ticket.setStatus(status);
        }
        
        // Priotty input optional as will deagult to MEDIUM if no answer
        Priority priority  = InputHelper.promptUntilValid(scanner,
                "What Priorty Level Is This Ticket? (LOW, MEDIUM, HIGH): ",
                true,
                Priority::valueOf);
        if (priority != null){
            ticket.setPriority(priority);
        }
        
        
        // Duedate input optional for user
        LocalDate duedate = InputHelper.promptUntilValid(scanner, 
                "Enter due date (yyyy-mm-dd) (optional): ", // will change date input to dd/mm/yyyy but dont know how yet
                true,
                LocalDate::parse);
        
        if (duedate != null){
            ticket.setDuedate(duedate);
        }

        System.out.println("Ticket created successfully!");
        return ticket;
        } catch (InputHelper.BackException e){
            if (ticket != null){
                manager.removeTicket(ticket.getId());
            }
            System.out.println("Ticket creation cancelled.");
            return null;
        } 
    }
}
