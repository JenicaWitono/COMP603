/**
 *
 * @author jenica + jamie
 */

import java.util.Scanner;

public class Main {
    
    public static void main(String[] args){
        Scanner scanner = new Scanner(System.in);
        TicketManager manager = new TicketManager();
        new ConsoleInput(scanner, manager).run();
    }
}
    