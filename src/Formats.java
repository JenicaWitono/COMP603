//written with assistance from Claude.ai
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

// Shared formatting helpers so every screen shows IDs, dates and labels the same way
public class Formats {

    public static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    public static final DateTimeFormatter SHORT_DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yy"); // main view table

    // Date formats the user is allowed to type
    private static final DateTimeFormatter[] INPUT_DATE_FORMATS = {
        DateTimeFormatter.ofPattern("dd/MM/yyyy"),
        DateTimeFormatter.ofPattern("dd-MM-yyyy"),
        DateTimeFormatter.ofPattern("yyyy-MM-dd")
    };

    private Formats(){
        // Static helper class
    }

    // IDs are shown as three digits, e.g. 7 -> "007"
    public static String formatId(int id){
        return String.format("%03d", id);
    }

    // dd/MM/yyyy, or "-" when there is no date
    public static String formatDate(LocalDate date){
        return date == null ? "-" : date.format(DATE_FORMAT);
    }

    // The text itself, or "-" when it's empty (used for optional fields)
    public static String textOrDash(String text){
        return (text == null || text.isBlank()) ? "-" : text;
    }

    // Tries each accepted date format in turn; throws if none match
    public static LocalDate parseDate(String input) {
        for (DateTimeFormatter fmt : INPUT_DATE_FORMATS) {
            try {
                return LocalDate.parse(input.trim(), fmt);
            } catch (DateTimeParseException ignored) {
                // try the next format
            }
        }
        throw new DateTimeParseException("Unrecognised date format: " + input, input, 0);
    }
}
