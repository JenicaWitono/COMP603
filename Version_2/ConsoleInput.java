/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author Jamie
 */

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
        // Handles user input such as 'New ticket please' as new
        String[] parts = line.split("\\s+", 2);
        String command = parts[0].toLowerCase();
        
        // Comands (more to come)
        switch(command){
            case "/new" -> createNewTicket();
            case "/mainview" -> MainView.print(manager.getAllTickets());
            case "/help" -> help();
            case "/remove" -> RemoveTicket.removeTicket(scanner, manager);
            case "/edit" -> EditTicket.edit(scanner, manager);
            case "/reset" -> resetTickets();
            case "/detailview" -> viewDetail();
            default -> System.out.println("Unknown command: '" +command+ "'.type ' /help for some help.");
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
                            /new          - Create a new ticket
                            /mainview     - Shows a brief view of all tickets
                            /detailview   - Shows details for one ticket
                            /remove       - Deletes a ticket by ID
                            /edit         - Edit a already exisiting ticket
                            /reset        - Clear the current save file and remove all tickets
                          The system saves upon quitting.
                          Type 'exit' any time to quit, 'back' to cancel the current action.    
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
