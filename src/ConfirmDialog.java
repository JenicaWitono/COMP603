//written with assistance from Claude.ai
import javax.swing.JOptionPane;
import java.awt.Component;

// Warning popup with a confirm option and Cancel, used before anything that can't be undone
public class ConfirmDialog {

    private ConfirmDialog(){
        // Static helper class
    }

    // Returns true only if the user picked the confirm option
    // Cancel is the default choice so pressing Enter doesn't confirm by accident
    public static boolean ask(Component parent, String title, String message, String confirmText){
        Object[] options = {confirmText, "Cancel"};
        int choice = JOptionPane.showOptionDialog(parent,
                message,
                title,
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.WARNING_MESSAGE,
                null,
                options,
                options[1]);
        return choice == 0;
    }
}

