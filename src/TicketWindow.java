/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author Jamie
 */

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

// Base calss for every screen of the ticket manager program. 
// It does everything that the screens have in comon:
// - window setup
// - header
// - saveAndExit()
// - help()
// -afterRest()
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
        
        // The x button is handled by onWindowClsoing() so each screen can decide what it does
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
    // The help dialouge text
    protected abstract String helpText();
    
    // What to do after reset button has wiped the data
    // refresh the table or go back to main view
    protected abstract void afterReset();
    
    // What the windows x button does. The default is to save and quit
    protected void onWindowClosing(){ 
        saveAndExit();
    }
    
    // Writes every ticket to the database and shuts derby down then ends the program
    protected void saveAndExit(){
        TicketStorage.save(manager);
        TicketStorage.shutdown();
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
