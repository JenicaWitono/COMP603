/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author Jamie
 */
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Scanner;
public class EditTicket {
    
    public static void edit(Scanner scanner, TicketManager manager){
        System.out.println("Enter Id of ticket to edit: ");
        String idText = scanner.nextLine().trim();
        
        int id;
        try{
            id = Integer.parseInt(idText);
        } catch (NumberFormatException e) {
            System.out.println("That is not a valid ID number.");
            return;
        }
        
        Ticket ticket = manager.getTicket(id);
        if (ticket == null){
            System.out.println("no ticket found with #"+id+".");
            return;
        }
        
        boolean editing = true;
        while (editing){
            System.out.println(ticket.toString());
            System.out.println("""
                               What would you like to edit?
                               1. Title
                               2. Description
                               3. Assigne
                               4. Status
                               5. Priority
                               6. Due date
                               0. Done editing
                               """);
            System.out.print("> ");
            String choice = scanner.nextLine().toLowerCase().trim();
            
            switch(choice){
                case "1", "title" -> editTitle(scanner, ticket);
                case "2", "description" -> editDescription(scanner, ticket);
                case "3", "Assigne" -> editAssigne(scanner, ticket);
                case "4", "Status" -> editStatus(scanner, ticket);
                case "5", "Priority" -> editPriority(scanner, ticket);
                case "6", "Due Date" -> editDuedate(scanner, ticket);
                case "0", "Done editing" -> editing = false;
                default -> System.out.println("Not a valid option.");
            }
        }
        
        System.out.println("Done editing ticket #"+id+"!");
        System.out.println(ticket.toString());
    }
    
    private static void editTitle(Scanner scanner, Ticket ticket){
        System.out.print("New title:");
        String title = scanner.nextLine();
        try{
            ticket.setTitle(title);
        } catch (IllegalArgumentException e){
            System.out.println(e.getMessage());
        }
    }
    
    private static void editDescription(Scanner scanner, Ticket ticket){
        System.out.print("New description: ");
        ticket.setDescription(scanner.nextLine());
    }
    
    private static void editAssigne(Scanner scanner, Ticket ticket){
        System.out.print("New Assigne");
        ticket.setAssignee(scanner.nextLine());
    }
    
    private static void editStatus(Scanner scanner, Ticket ticket){
        System.out.print("New status (TO DO, IN PROGRESS, IN_REVIEW, DONE, CLOSED): ");
        String status = scanner.nextLine().trim().toUpperCase();
        try{
            ticket.setStatus(Status.valueOf(status)); // havented added status yet MUST DO on 09-09-2026 IN OTHER WORDS TONIGHT YES DO IT YUO FAT LAZY BUM
        } catch (IllegalArgumentException e){
            System.out.println("Invalid status entered.");
        }
        
    }
    
    private static void editPriority(Scanner scanner, Ticket ticket){
        System.out.println("New Priority (LOW, MEDIUM, HIGH): ");
        String priority = scanner.nextLine().trim().toUpperCase();
        try{
            ticket.setPriority(Priority.valueOf(priority));
        } catch (IllegalArgumentException e){
            System.out.println("Invalid Priority set.");
        }
    }
    
    private static void editDuedate(Scanner scanner, Ticket ticket){
        System.out.println("New Due date (yyyy-mm-dd): ");
        String duedate = scanner.nextLine().trim();
        try{
            ticket.setDuedate(LocalDate.parse(duedate));
        } catch (DateTimeParseException e){
            System.out.println("Invalid date format.");
        }
    }
}
