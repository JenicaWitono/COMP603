/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author Jamie
 */

import javax.swing.JOptionPane;
import java.awt.Component;

public class DatabaseReset {
    
    private DatabaseReset(){
        // Static helper class
    }
    
    // For the main view: asks first, resets, then refreshes the main view's table.
    public static boolean confirmAndReset(MainViewGUI mainView, TicketManager manager) {
        return confirmAndReset(mainView, manager, mainView::refresh);
    }
    
    // Shows a conformation page for the user to choose, returns true if data is reset
    // false if no data to be deleted or user cancelled.
    public static boolean confirmAndReset(Component parent, TicketManager manager, Runnable afterReset){
        int count =manager.getAllTickets().size();
        if(count == 0){
            JOptionPane.showMessageDialog(parent, "There are no tickets to delete");
            return false;
        }
        
        Object[] options = {"Delete Everything", "Cancel"};
        int choice = JOptionPane.showOptionDialog(parent, 
                                "This will permanently delete all " + count + " ticket(s) and start fresh.\n"
                        + "This cannot be undone.",
                "Reset database",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.WARNING_MESSAGE,
                null,
                options,
                options[1]); // Cancel is the default choice
        if(choice != 0){
            return false;
        }
        
        reset(manager);
        afterReset.run();
        return true;
    }
    
    public static void reset(TicketManager manager){
        manager.reset();
        TicketStorage.save(manager);
    }
    
    
}
