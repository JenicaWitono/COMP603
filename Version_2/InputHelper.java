/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/**
 *
 * @author asuka
 */

import java.util.Scanner;
import java.time.format.DateTimeParseException;
import java.util.function.Function;


public class InputHelper {
    
    public static class ExitException extends RuntimeException {}
    public static class BackException extends RuntimeException {}
    
    public static String readLine(Scanner scanner, String prompt){
        System.out.print(prompt);
        String input = scanner.nextLine().trim();
        String lower = input.toLowerCase();
        
        if (lower.equals("/exit") || lower.equals("/quit")){
            throw new ExitException();
        }
        
        if (lower.equals("/back") || lower.equals("/cancel")){
            throw new BackException();
        }
        
        return input;
    }
    
    // Makes it so that the question will loop until the users enter a valid answer or if they can cancel the action
    public static <T> T promptUntilValid(Scanner scanner, String prompt, boolean optional, Function<String, T> parser){
        while (true){
            
            String input = readLine(scanner, prompt);
            
            // For optional questions it will allow the program to accept empty answers (optional)
            if (optional && input.isEmpty()){
                return null;
            }
            
            try{
                return parser.apply(input);
            } catch (IllegalArgumentException | DateTimeParseException e) {
                System.out.println("Invalid input. Please try again!");
            }
        }
    }
    
}