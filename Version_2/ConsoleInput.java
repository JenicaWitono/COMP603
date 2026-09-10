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
        System.out.print("This will delete ALL tickets. Are you sure? (yes/no): ");
        String confirm = scanner.nextLine().trim().toLowerCase();

        if (confirm.equals("yes")) {
            manager.reset();
            System.out.println("All tickets have been wiped.");
        } else {
            System.out.println("Reset cancelled.");
        }
    }

   // outputs all commands that a user can use 
   // Plan to implement a menu where it asks what aera the user needs help in
   private void help(){
       System.out.println("""
                          Comands: 
                            /new          - create a new ticket
                            /MainView     - shows details for one ticket
                            /remove       - deletes a ticket by ID
                            /edit         - edit a already exisiting ticket
                            /reset        - will clear the current save file and remove all tickets
                          Type 'exit' any time to quit, 'back' to cancel the current action.    
                          """);
   }
    
    
    
    
    
    
}
