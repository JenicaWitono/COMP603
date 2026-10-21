/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author jenic
 */
public class CheckDerby {
   public static void main(String[] args) {
        try {
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
            System.out.println("Derby embedded driver found — you're good to go.");
        } catch (ClassNotFoundException e) {
            System.out.println("Derby driver NOT found: " + e.getMessage());
        } catch (Throwable t) {
            System.out.println("Something else went wrong loading the driver: " + t);
        }
    } 
}
