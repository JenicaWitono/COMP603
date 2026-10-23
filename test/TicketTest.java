//written with assistance from Claude.ai
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TicketTest {

    private Ticket ticket;

    // Runs before each test case: gives every test a fresh ticket
    @Before
    public void setUp() {
        ticket = new Ticket(1, "Test ticket");
    }

    // new ticket default status and priority

    @Test
    public void newTicketDefaultStatusTest() {
        Status actual = ticket.getStatus();
        Status expected = Status.TODO;
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void newTicketDefaultPriorityTest() {
        Priority actual = ticket.getPriority();
        Priority expected = Priority.MEDIUM;
        Assert.assertEquals(expected, actual);
    }

    // title

    @Test
    public void setTitleTest() {
        ticket.setTitle("New title");
        String actual = ticket.getTitle();
        String expected = "New title";
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void emptyTitleRejectedTest() {
        try {
            ticket.setTitle("");
            Assert.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            //an empty title is not allowed
        }
    }

    @Test
    public void tooLongTitleRejectedTest() {
        try {
            ticket.setTitle("a".repeat(501));
            Assert.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            //titles are max 500 characters
        }
    }

    @Test
    public void tooManyWordsTitleRejectedTest() {
        try {
            ticket.setTitle("word ".repeat(51));
            Assert.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            //titles are max 50 words
        }
    }

    // description and assignee limits

    @Test
    public void tooLongDescriptionRejectedTest() {
        try {
            ticket.setDescription("d".repeat(2001));
            Assert.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            //descriptions are limited to 2000 characters
        }
    }

    @Test
    public void tooLongAssigneeRejectedTest() {
        try {
            ticket.setAssignee("n".repeat(101));
            Assert.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            //assignee names are limited to 100 characters
        }
    }

    // dependencies

    @Test
    public void addDependencyTest() {
        ticket.addDependency(2);
        boolean actual = ticket.getDependencies().contains(2);
        Assert.assertTrue(actual);
    }

    @Test
    public void selfDependencyRejectedTest() {
        try {
            ticket.addDependency(1);
            Assert.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            //a ticket cant depend on itself
        }
    }

    @Test
    public void removeDependencyTest() {
        ticket.addDependency(2);
        ticket.removeDependency(2);
        boolean actual = ticket.getDependencies().contains(2);
        Assert.assertFalse(actual);
    }
}
