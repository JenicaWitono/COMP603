//written with assistance from Claude.ai
import java.time.LocalDate;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TicketStorageTest {

    // Runs before each test case: points the storage at a throwaway test
    // database (never the real ticketdb) and makes sure the tables exist
    @Before
    public void setUp() {
        TicketStorage.useDatabase("ticketdb_test");
        TicketStorage.initialize();
    }

    // Runs after each test case: saving an empty manager wipes both tables,
    // so the next test starts with an empty database
    @After
    public void tearDown() {
        TicketStorage.save(new TicketManager());
    }

    // initialize

    @Test
    public void initializeTwiceTest() {
        // setUp() already created the tables, so this checks that
        // "table already exists" is handled instead of failing
        boolean actual = TicketStorage.initialize();
        Assert.assertTrue(actual);
    }

    // loading database

    @Test
    public void loadEmptyDatabaseTest() {
        TicketManager loaded = new TicketManager();
        TicketStorage.load(loaded);

        int actual = loaded.getAllTickets().size();
        Assert.assertEquals(0, actual);
    }

    // save and load

    @Test
    public void saveAndLoadTicketTest() {
        TicketManager saved = new TicketManager();
        Ticket original = saved.createTicket("Write report");
        original.setDescription("Draft the final report");
        original.setAssignee("Sam");
        original.setStatus(Status.IN_REVIEW);
        original.setPriority(Priority.HIGH);
        original.setDuedate(LocalDate.of(2026, 8, 18));
        TicketStorage.save(saved);

        TicketManager loaded = new TicketManager();
        TicketStorage.load(loaded);
        Ticket actual = loaded.getTicket(original.getId());

        Assert.assertNotNull(actual);
        Assert.assertEquals("Write report", actual.getTitle());
        Assert.assertEquals("Draft the final report", actual.getDescription());
        Assert.assertEquals("Sam", actual.getAssignee());
        Assert.assertEquals(Status.IN_REVIEW, actual.getStatus());
        Assert.assertEquals(Priority.HIGH, actual.getPriority());
        Assert.assertEquals(LocalDate.of(2026, 8, 18), actual.getDuedate());
        Assert.assertEquals(original.getLastModified(), actual.getLastModified());
    }

    @Test
    public void saveAndLoadEmptyOptionalFieldsTest() {
        TicketManager saved = new TicketManager();
        saved.createTicket("Bare ticket");
        TicketStorage.save(saved);

        TicketManager loaded = new TicketManager();
        TicketStorage.load(loaded);
        Ticket actual = loaded.getTicket(1);

        Assert.assertNull(actual.getDescription());
        Assert.assertNull(actual.getAssignee());
        Assert.assertNull(actual.getDuedate());
    }

    @Test
    public void saveAndLoadDependenciesTest() {
        TicketManager saved = new TicketManager();
        saved.createTicket("One");
        saved.createTicket("Two");
        Ticket three = saved.createTicket("Three");
        three.addDependency(1);
        three.addDependency(2);
        TicketStorage.save(saved);

        TicketManager loaded = new TicketManager();
        TicketStorage.load(loaded);
        Ticket actual = loaded.getTicket(3);

        Assert.assertEquals(2, actual.getDependencies().size());
        Assert.assertTrue(actual.getDependencies().contains(1));
        Assert.assertTrue(actual.getDependencies().contains(2));
    }

    @Test
    public void saveReplacesOldDataTest() {
        TicketManager manager = new TicketManager();
        manager.createTicket("Keep me");
        manager.createTicket("Remove me");
        TicketStorage.save(manager);

        manager.removeTicket(2);
        TicketStorage.save(manager);

        TicketManager loaded = new TicketManager();
        TicketStorage.load(loaded);

        Assert.assertEquals(1, loaded.getAllTickets().size());
        Assert.assertNull(loaded.getTicket(2));
    }
}
