//written with assistance from Claude.ai
import java.time.LocalDate;
import java.util.Scanner;
public class EditTicket {
    // Finds a ticket, then loops showing an edit menu until the user is done
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
                               7. Dependencies
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
                case "3", "assignee" -> editAssigne(scanner, ticket);
                case "4", "status" -> editStatus(scanner, ticket, manager);
                case "5", "priority" -> editPriority(scanner, ticket);
                case "6", "due date" -> editDuedate(scanner, ticket);
                case "7", "dependencies" -> editDependencies(scanner, ticket, manager);
                case "0", "done editing" -> editing = false;
                default -> System.out.println("Not a valid option.");
            }
        }
        
        System.out.println("Done editing ticket #"+ticket.getId()+"!");
        DetailView.print(ticket);
    }
    
    // Prompts for a ticket ID until a valid one is found, or null if the user backs out
    private static Ticket findTicket(Scanner scanner, TicketManager manager){
        try{
            Ticket ticket = null;
            while (ticket == null){
                String idText = InputHelper.readLine(scanner, "Enter the ID of the ticket you want to edit: ");
                int id;
                try{
                    id = Integer.parseInt(idText);
                } catch (NumberFormatException e){
                    System.out.println("That is not a valid ID number. Please try again.");
                    continue;
                }

                ticket = manager.getTicket(id);
                if (ticket == null){
                    System.out.println("No ticket found with #"+id+". Please try again.");
                }
            }
            return ticket;
        } catch (InputHelper.BackException e){
            System.out.println("Edit cancelled.");
            return null;
        }
    }
    // Edits the title, catching validation errors from the setter
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
    
    //edit assignee, blocks if it's invalid
    private static void editAssigne(Scanner scanner, Ticket ticket){
        try{
            ticket.setAssignee(InputHelper.readLine(scanner, "New assignee: "));
        } catch (IllegalArgumentException e){
            System.out.println(e.getMessage());
        } catch (InputHelper.BackException e){
            System.out.println("Assignee edit cancelled.");
        }
    }
    
    // Edits status, blocking the change if dependencies aren't complete
    private static void editStatus(Scanner scanner, Ticket ticket, TicketManager manager){
        try {
            Status status = InputHelper.promptUntilValid(scanner,
                    "New status (To Do, In Progress, In Review, Done, Closed): ",
                    false,
                    Status::parse);
                // Checks dependecies to see if it can be changed
                if ((status == Status.IN_PROGRESS || status == Status.IN_REVIEW || status == Status.DONE)
                        && !manager.areDependenciesComplete(ticket)){
                    System.out.println("Cannot move this ticket to "+status+" - it has incomplete dependencies");
                    for (int depId : ticket.getDependencies()){
                        Ticket dep = manager.getTicket(depId);
                        if (dep != null && dep.getStatus() != Status.DONE && dep.getStatus() != Status.CLOSED){
                            System.out.println(" #"+dep.getId()+" - "+dep.getTitle()+" ("+dep.getStatus()+")");
                        }
                    }
                    return; // Rejects the change
                }
            ticket.setStatus(status);
        } catch (InputHelper.BackException e){
            System.out.println("Status edit cancelled.");
        }
    }
    
    private static void editPriority(Scanner scanner, Ticket ticket){
        try {
            Priority priority = InputHelper.promptUntilValid(scanner,
                    "New priority (Low, Medium, High): ",
                    false,
                    Priority::parse);
            ticket.setPriority(priority);
        } catch(InputHelper.BackException e){
            System.out.println("Priority edit cancelled.");
        }
    }
    
    private static void editDuedate(Scanner scanner, Ticket ticket){
        try {
            LocalDate duedate = InputHelper.promptUntilValid(scanner,
                    "New due date (dd/mm/yyyy): ",
                    false,
                    InputHelper::parseDate);
            ticket.setDuedate(duedate);
        } catch (InputHelper.BackException e){
            System.out.println("Due date edit canclled.");
        }
    }
    
    // Adds or removes a single dependency based on user choice
    private static void editDependencies(Scanner scanner, Ticket ticket, TicketManager manager){
        try {
            System.out.println("Current dependencies: "+ticket.getDependencies());
            String action = InputHelper.readLine(scanner, "Add or remove a dependency? (add/remove): ").toLowerCase();

            String idText = InputHelper.readLine(scanner, "Enter ticket ID to add as a dependency: ");
            int depId;
            try {
                depId = Integer.parseInt(idText);
            } catch (NumberFormatException e){
                System.out.println("Not a valid ID.");
                return;
            }

            switch (action){
                case "add","/add" -> {
                    if (manager.getTicket(depId) == null){
                        System.out.println("No ticket found with ID #"+depId+".");
                        return;
                    }
                    ticket.addDependency(depId);
                    System.out.println("Added dependency on ticket #"+depId+".");
                }

                case "remove","/remove" -> {
                    ticket.removeDependency(depId);
                    System.out.println("Removed dependency on ticket #"+depId+".");
                }

                default -> System.out.println("Please type 'add' or 'remove'.");
            }

        } catch (IllegalArgumentException e){
            System.out.println(e.getMessage());
        } catch (InputHelper.BackException e){
            System.out.println("Dependency edit cancelled.");
        }
    }
    //catches error for invalid description
    private static void editDescription(Scanner scanner, Ticket ticket){
        try{
            ticket.setDescription(InputHelper.readLine(scanner, "New description: "));
        } catch (IllegalArgumentException e){
            System.out.println(e.getMessage());
        } catch (InputHelper.BackException e){
            System.out.println("Description edit cancelled.");
        }
    }
}
