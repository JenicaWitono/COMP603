/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author Jamie
 */
import java.time.LocalDate;
import java.util.Scanner;
public class EditTicket {
    
    public static void edit(Scanner scanner, TicketManager manager){
        
        Ticket ticket = findTicket(scanner, manager);
        if (ticket == null) return;

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
            
            String choice;
            try {
                choice = InputHelper.readLine(scanner, "> ").toLowerCase();
            } catch (InputHelper.BackException e ){
                System.out.println("Exiting edit menu.");
                break;
            }
            
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
        
        System.out.println("Done editing ticket #"+ticket.getId()+"!");
        System.out.println(ticket.toString());
    }
    
    // Handles the enter and ID loop, will return null if the nuser backs out
    private static Ticket findTicket(Scanner scanner, TicketManager manager){
        try{
            Ticket ticket = null;
            while (ticket == null){
                String idText = InputHelper.readLine(scanner, "Enter the ID of the ticket you want to edit:  ");
                int id;
                try{
                    id = Integer.parseInt(idText);
                } catch (NumberFormatException e){
                    System.out.println("That is not a valid ID number. Please try again.");
                    continue;
                }
                ticket = manager.getTicket(id);
                if (ticket == null){
                    System.out.println("No ticket found with #"+id+".Please try again.");
                }
            }
            return ticket;
        } catch (InputHelper.BackException e){
            System.out.println("Edit cancelled.");
            return null;
        }
    }
    private static void editTitle(Scanner scanner, Ticket ticket){
        try {
            String title = InputHelper.readLine(scanner, "New title: ");
            ticket.setTitle(title);
        } catch (IllegalArgumentException e){
            System.out.println(e.getMessage());
        } catch (InputHelper.BackException e){
            System.out.println("Title edit cancelled.");
        }
    }
    
    private static void editDescription(Scanner scanner, Ticket ticket){
        try{
            ticket.setDescription(InputHelper.readLine(scanner, "New description: "));
        } catch (InputHelper.BackException e){
            System.out.println("Description edit cancelled.");
        }
    }
    
    private static void editAssigne(Scanner scanner, Ticket ticket){
        try{
            ticket.setAssignee(InputHelper.readLine(scanner, "New assignee: "));
        } catch (InputHelper.BackException e){
            System.out.println("Assignee edit cancelled.");
        }
    }
    
    private static void editStatus(Scanner scanner, Ticket ticket){
        try {
            Status status = InputHelper.promptUntilValid(scanner,
                    "New status (TODO, IN_PROGRESS, IN_REVIEW, DONE, CLOSED: ",
                    false,
                    s -> Status.valueOf(s.toUpperCase()));
            ticket.setStatus(status);
        } catch (InputHelper.BackException e){
            System.out.println("Status edit cancelled.");
        }
    }
    
    private static void editPriority(Scanner scanner, Ticket ticket){
        try {
            Priority priority = InputHelper.promptUntilValid(scanner,
                    "New priority (LOW, MEDIUM, HIGH); ",
                    false,
                    s -> Priority.valueOf(s.toUpperCase()));
            ticket.setPriority(priority);
        } catch(InputHelper.BackException e){
            System.out.println("Priority edit cancelled.");
        }
    }
    
    private static void editDuedate(Scanner scanner, Ticket ticket){
        try {
            LocalDate duedate = InputHelper.promptUntilValid(scanner,
                    "New due date (yyyy-mm-dd): ",
                    false,
                    LocalDate::parse);
            ticket.setDuedate(duedate);
        } catch (InputHelper.BackException e){
            System.out.println("Due date edit canclled.");
        }
    }
}
