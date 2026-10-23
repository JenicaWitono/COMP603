//written with assistance from Claude.ai
import javax.swing.JOptionPane;
import java.awt.Component;

public class DatabaseReset {

    private DatabaseReset(){
        // Static helper class
    }

    // Shows a confirmation popup, then resets and runs afterReset
    public static boolean confirmAndReset(Component parent, TicketManager manager, Runnable afterReset){
        int count = manager.getAllTickets().size();
        if(count == 0){
            JOptionPane.showMessageDialog(parent, "There are no tickets to delete");
            return false;
        }

        boolean confirmed = ConfirmDialog.ask(parent,
                "Reset database",
                "This will permanently delete all " + count + " ticket(s) and start fresh.\n"
                        + "This cannot be undone.",
                "Delete Everything");
        if(!confirmed){
            return false;
        }

        if(!reset(manager)){
            JOptionPane.showMessageDialog(parent, "The tickets were cleared, but the database could not be updated.",
                    "Save failed", JOptionPane.ERROR_MESSAGE);
        }
        afterReset.run();
        return true;
    }

    // Clears every ticket and saves the empty list, returns false if the save failed
    public static boolean reset(TicketManager manager){
        manager.reset();
        return manager.save();
    }
}
