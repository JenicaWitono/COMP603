//written with assistance from Claude.ai
import java.time.LocalDate;
import org.junit.Assert;
import org.junit.Test;

public class FormatsTest {

    // ticket IDs

    @Test
    public void formatIdPadsToThreeDigitsTest() {
        String actual = Formats.formatId(7);
        String expected = "007";
        Assert.assertEquals(expected, actual);
    }

    // dates

    @Test
    public void formatDateTest() {
        String actual = Formats.formatDate(LocalDate.of(2026, 8, 18));
        String expected = "18/08/2026";
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void formatDateNullTest() {
        String actual = Formats.formatDate(null);
        String expected = "-";
        Assert.assertEquals(expected, actual);
    }
}
