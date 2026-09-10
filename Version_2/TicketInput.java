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
                "What is the Status of this Ticket? (TODO, IN_PROGRESS, IN_REVIEW, DONE, CLOSED): ",
                true,
                Status::valueOf);
        if (status != null){
            ticket.setStatus(status);
        }
        
        // Priotty input optional as will deagult to MEDIUM if no answer
        Priority priority  = InputHelper.promptUntilValid(scanner,
                "What Priorty Level is This Ticket? (LOW, MEDIUM, HIGH): ",
                true,
                Priority::valueOf);
        if (priority != null){
            ticket.setPriority(priority);
        }
        
        // Dependencies input optional
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
                }
            }
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
