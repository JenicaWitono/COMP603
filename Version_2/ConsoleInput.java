/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author Jamie
 */

import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

public class ConsoleInput {
    
    // Fields
    private final Scanner scanner;
    private final TicketManager manager;
    private boolean running;
    private static final String SAVE_FILE = "tickets.txt";
    
    // Constructor
    public ConsoleInput(Scanner scanner, TicketManager manager){
        this.scanner = scanner;
        this.manager = manager;
        this.running = true;
    }
    
    // Main loop
    public void run(){
        System.out.println("Ticket manager. Type 'help' to see commands.");
        FileStorage.load(SAVE_FILE, manager);
        
        while(running){
            try{
                String line = InputHelper.readLine(scanner, "> ");
            
                if (line.isEmpty()){
                    continue;
                }
          
                dispatch(line);
            } catch (InputHelper.ExitException e){
                System.out.println("File saved. Goodbye!");
                FileStorage.save(SAVE_FILE, manager);
                running = false;
            } catch (InputHelper.BackException e){
                System.out.println("You are at the main menu. Cannot go back anyfuther!");
            }
        }
        
        
    }
    
    // Figuring out what command was typed
    private void dispatch(String line){
        String[] parts = line.split("\\s+", 2);
        String command = parts[0].toLowerCase();
        String rest = parts.length > 1 ? parts[1].trim().toLowerCase() : "";

        switch(command){
            case "/new" -> createNewTicket();
            case "/view" -> viewMain(rest);
            case "/archiveview" -> viewArchive(rest);
            case "/help" -> help();
            case "/remove" -> RemoveTicket.removeTicket(scanner, manager);
            case "/edit" -> EditTicket.edit(scanner, manager);
            case "/reset" -> resetTickets();
            case "/detailview" -> viewDetail();
            default -> System.out.println("Unknown command: '" +command+ "'.type ' /help for some help.");
        }
    }

    private void viewMain(String sortBy){
        List<Ticket> tickets = manager.getAllTickets();
        tickets.sort(getComparator(sortBy));
        MainView.print(tickets);
    }

    private void viewArchive(String sortBy){
        List<Ticket> tickets = manager.getAllTickets();
        tickets.sort(getComparator(sortBy));
        MainView.printArchived(tickets);
    }

    // Maps a sort keyword to a comparator; defaults to ID order if unrecognized/blank
    private Comparator<Ticket> getComparator(String sortBy){
        switch (sortBy) {
            case "priority":
                return Comparator.comparing(Ticket::getPriority).reversed(); // HIGH first
            case "status":
                return Comparator.comparing(Ticket::getStatus);
            case "duedate":
                return Comparator.comparing(Ticket::getDuedate, Comparator.nullsLast(Comparator.naturalOrder()));
            default:
                return Comparator.comparing(Ticket::getId);
        }
    }
   
    // creates a new ticket
   private void createNewTicket(){
       Ticket ticket = TicketInput.userInput(scanner, manager);
       if (ticket == null) {
           return; // for when user types 'back' mid-creation
       }
        System.out.println("New ticket created (ID: " + ticket.getId() + ")");
        System.out.println(ticket.toString());
   }
   
    // Wipes all tickets after confirming with the user
    private void resetTickets(){
        try {
            String confirm = InputHelper.readLine(scanner, "This will delete ALL tickets. Are you sure? (yes/no): ").toLowerCase();
            if (confirm.equals("yes")) {
                manager.reset();
                System.out.println("All tickets have been wiped.");
            } else {
                System.out.println("Reset cancelled.");
            }
        } catch (InputHelper.BackException e) {
            System.out.println("Reset cancelled.");
        }
    }

   // outputs all commands that a user can use 
   // Plan to implement a menu where it asks what aera the user needs help in
   private void help(){
        System.out.println("""
                           Commands: 
                             /new                                      - Create a new ticket
                             /view                                     - Shows active tickets
                             /view [id|priority|status|duedate]        - Shows active tickets and sort based on the added field
                             /archiveview                              - Shows completed/closed tickets
                             /archiveview [id|priority|status|duedate] - Sort completed/closed tickets and sort based on the added field
                             /detailview                               - Shows details for one ticket
                             /remove                                   - Deletes a ticket by ID
                             /edit                                     - Edit an already existing ticket
                             /reset                                    - Clear the current save file and remove all tickets
                           The system saves upon quitting.
                           Type '/exit' any time to quit, '/back' or '/cancel' to cancel the current action.    
                           """);
    }
   
   //trigger view detail
    private void viewDetail(){
        try {
            String idText = InputHelper.readLine(scanner, "Enter the ID of the ticket to view: ");
            int id;

            try {
                id = Integer.parseInt(idText);
            } catch (NumberFormatException e) {
                System.out.println(idText + " is not a valid ticket ID.");
                return;
            }
            
            Ticket ticket = manager.getTicket(id);
            if (ticket == null) {
                System.out.println("No ticket found with ID " + id + ".");
                return;
            }
            if (manager.isCorrupted(id)) {
                    System.out.println("Ticket #" + id + " is corrupted and cannot be accessed.");
                    return;
            }
            DetailView.print(ticket);
        } catch (InputHelper.BackException e) {
            System.out.println("Cancelled.");
        }
    }
    
    
    
    
    
}
