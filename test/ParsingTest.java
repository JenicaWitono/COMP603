//written with assistance from Claude.ai
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import org.junit.Assert;
import org.junit.Test;

public class ParsingTest {

    // parsing for dates
    @Test
    public void parseDateSlashFormatTest() {
        LocalDate actual = Formats.parseDate("18/08/2026");
        LocalDate expected = LocalDate.of(2026, 8, 18);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void parseDateDashFormatTest() {
        LocalDate actual = Formats.parseDate("18-08-2026");
        LocalDate expected = LocalDate.of(2026, 8, 18);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void parseDateInvalidTest() {
        try {
            Formats.parseDate("tomorrow");
            Assert.fail("Expected DateTimeParseException");
        } catch (DateTimeParseException e) {
            // expected: "tomorrow" is not a date
        }
    }
}
