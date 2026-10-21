//written with assistance from Claude.ai
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import org.junit.Assert;
import org.junit.Test;

public class ParsingTest {

    //parsing for status

    @Test
    public void statusParseToDoTest() {
        Status actual = Status.parse("to do");
        Status expected = Status.TODO;
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void statusParseInProgressTest() {
        Status actual = Status.parse("IN-PROGRESS");
        Status expected = Status.IN_PROGRESS;
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void statusParseInvalidTest() {
        try {
            Status.parse("finished");
            Assert.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // expected: "finished" is not a status
        }
    }

    // parsing for priority

    @Test
    public void priorityParseTest() {
        Priority actual = Priority.parse("High");
        Priority expected = Priority.HIGH;
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void priorityParseShortcutTest() {
        Priority actual = Priority.parse("med");
        Priority expected = Priority.MEDIUM;
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void priorityParseInvalidTest() {
        try {
            Priority.parse("urgent");
            Assert.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // expected: "urgent" is not a priority
        }
    }

    // parsing for inputHelper

    @Test
    public void parseDateSlashFormatTest() {
        LocalDate actual = InputHelper.parseDate("18/08/2026");
        LocalDate expected = LocalDate.of(2026, 8, 18);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void parseDateDashFormatTest() {
        LocalDate actual = InputHelper.parseDate("18-08-2026");
        LocalDate expected = LocalDate.of(2026, 8, 18);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void parseDateInvalidTest() {
        try {
            InputHelper.parseDate("tomorrow");
            Assert.fail("Expected DateTimeParseException");
        } catch (DateTimeParseException e) {
            // expected: "tomorrow" is not a date
        }
    }
}
