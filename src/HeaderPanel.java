/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author Jamie
 */

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;

// Shared Header for every screen: the help. save and exit/ reset buttons
// Ticket Manager title and the page title
// screens that need more rows in the header eg new task button use the addExtra()
public class HeaderPanel extends JPanel{
    
    private final JLabel pageTitle;
    private final JPanel topBar = new JPanel(new BorderLayout());
    
    public HeaderPanel(String title, Runnable onHelp, Runnable onSaveExit, Runnable onReset){
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        
         // Top Bar: Help and Save buttons, and the title
        JButton helpButton = new JButton("Help");
        JButton saveExitButton = new JButton("Save & Exit");
        JButton resetButton = new JButton("Reset");
        
        JPanel buttonRow = new JPanel();
        buttonRow.add(helpButton);
        buttonRow.add(saveExitButton);
        buttonRow.add(resetButton);
        
        JLabel appTitle = new JLabel("Ticket Manager", SwingConstants.CENTER);
        appTitle.setFont(appTitle.getFont().deriveFont(Font.BOLD, 18f));
        
        topBar.add(buttonRow, BorderLayout.WEST);
        topBar.add(appTitle, BorderLayout.CENTER);
        topBar.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, Color.BLACK));
        topBar.setAlignmentX(LEFT_ALIGNMENT);
        
        // Page title
        pageTitle = new JLabel(title);
        pageTitle.setFont(pageTitle.getFont().deriveFont(Font.PLAIN, 24f));
        pageTitle.setBorder(new EmptyBorder(5, 10, 5, 0));
        pageTitle.setAlignmentX(LEFT_ALIGNMENT);
        
        add(topBar);
        add(pageTitle);
        
        helpButton.addActionListener(e -> onHelp.run());
        saveExitButton.addActionListener(e -> onSaveExit.run());
        resetButton.addActionListener(e -> onReset.run());
    }
    
    public void setPageTitle(String title){
        pageTitle.setText(title);
    }
    
    public void centerPageTitle() {
        pageTitle.setAlignmentX(CENTER_ALIGNMENT);
        topBar.setAlignmentX(CENTER_ALIGNMENT);
    }
    
    // Adds an extra row under the page title
    public void addExtra(JComponent c){
        c.setAlignmentX(LEFT_ALIGNMENT);
        add(c);
    }
}
