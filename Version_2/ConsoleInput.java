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
        //load save file if exists
        FileStorage.load(SAVE_FILE, manager);
        System.out.println("Ticket manager. Type 'help' to see commands.");
        
        while(running){
            System.out.print("> ");
            String line = scanner.nextLine().trim();
            
            if (line.isEmpty()){
                continue;
            }
          
            dispatch(line);
        }
        
        
    }
    
    // Figuring out what command was typed
    private void dispatch(String line){
        // Handles user input such as 'New ticket please' as new
        String[] parts = line.split("\\s+", 2);
        String command = parts[0].toLowerCase();
        String rest = parts.length > 1 ? parts[1] : ""; // turns the 'rest' (second half of input) into an empty string    
        
        // Comands (more to come)
        switch(command){
            case "/new" -> createNewTicket();
            case "/mainview" -> view();
            case "/help" -> help();
            case "/remove" -> removeTicket();
            case "/edit" -> EditTicket.edit(scanner, manager);
            case "/exit" -> exit();
            default -> System.out.println("Unknown command: '" +command+ "'.type ' /help for some help.");
        }
    }
    
    private void exit(){
        FileStorage.save(SAVE_FILE, manager);
        System.out.println("Tickets saved. Goodbye!");
        running = false;
    }
    
    // creates a new ticket
   private void createNewTicket(){
       Ticket ticket = TicketInput.userInput(scanner, manager);
        System.out.println("New ticket created (ID: " + ticket.getId() + ")");
        System.out.println(ticket.toString());
   }
   
   // MainView
   private void view(){
       MainView MainView = new MainView();
       MainView.print(manager.getAllTickets());

   }
   
   // Remove Ticket
   private void removeTicket(){
       System.out.println("Please enter the ID of ticket to be removed: ");
       String idText = scanner.nextLine().trim();
       
       int id;
       try { 
           id = Integer.parseInt(idText);
       } catch (NumberFormatException e){
           System.out.println(""+idText+" Is not a valid ticket ID.");
           return;
       }
       
       boolean removed = manager.removeTicket(id);
       if(removed){
           System.out.println("Ticket #"+id+" Has been succsefully removed.");
       }
       else {
           System.out.println("No ticket found with ID "+id+".");
       }
   }
   
   // ALlows the user to edit and already existing ticket
   private void editTicket(){
       
   }
   
   // outputs all commands that a user can use 
   // Plan to implement a menu where it asks what aera the user needs help in
   private void help(){
       System.out.println("""
                          Comands: 
                            /new          - create a new ticket
                            /MainView     - shows details for one ticket
                            /remove       - deletes a ticket by ID
                          
                          """);
   }
    
    
    
    
    
    
}
