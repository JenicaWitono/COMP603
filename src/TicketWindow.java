/**
 *
 * @author Jamie
 */

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

// Base class for every screen of the ticket manager program.
// It does everything that the screens have in common:
// - window setup
// - header
// - saveAndExit()
// - help()
// - afterReset()
// - onWindowClosing()

public abstract class TicketWindow extends JFrame {

    protected final TicketManager manager;
    protected final HeaderPanel header;

    private final String screenName;

    protected TicketWindow(TicketManager manager, String pageTitle, int width, int height){
        this.manager = manager;
        this.screenName = pageTitle;

        // Window setup
        setTitle("TicketManager");
        setSize(width, height);
        setLocationRelativeTo(null); // Centre of screen

        // The x button is handled by onWindowClosing() so each screen can decide what it does
        // on some of the different screens it goes back instead of closing
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e){
                onWindowClosing();
            }
        });

        // Shared header
        header = new HeaderPanel(pageTitle, this::showHelp, this::saveAndExit, this::resetData);
        add(header, BorderLayout.NORTH);
    }

    // What each screen must provide
    // The help dialogue text
    protected abstract String helpText();

    // What to do after reset button has wiped the data
    // refresh the table or go back to main view
    protected abstract void afterReset();

    // What the windows x button does. The default is to save and quit
    protected void onWindowClosing(){
        saveAndExit();
    }

    // Writes every ticket to the database and shuts derby down then ends the program
    // if the save fails it asks before quitting so nothing is lost by accident
    protected void saveAndExit(){
        if (!manager.save()) {
            boolean exitAnyway = ConfirmDialog.ask(this, "Save failed",
                    "Tickets could not be saved to the database.\nExit without saving?", "Exit anyway");
            if (!exitAnyway) return;
        }
        manager.closeDatabase();
        System.exit(0);
    }

    // Shows the screens help text
    protected void showHelp(){
        JOptionPane.showMessageDialog(this, helpText(), "Help - " + screenName, JOptionPane.INFORMATION_MESSAGE);
    }

    // Header reset button
    protected void resetData(){
        DatabaseReset.confirmAndReset(this, manager, this::afterReset);
    }

}
