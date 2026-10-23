//written with assistance from Claude.ai
import java.time.LocalDate;
import java.util.List;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class DerbyTicketDAOTest {

    private TicketDAO dao;

    // Runs before each test case: points the storage at a throwaway test
    // database (never the real ticketdb) and makes sure the tables exist
    @Before
    public void setUp() {
        dao = new DerbyTicketDAO("ticketdb_test");
        dao.initialize();
    }

    // Runs after each test case: saving an empty manager wipes both tables,
    // so the next test starts with an empty database
    @After
    public void tearDown() {
        new TicketManager(dao).save();
    }

    // initialize

    @Test
    public void initializeTwiceTest() {
        // setUp() already created the tables, so this checks that
        // "table already exists" is handled instead of failing
        boolean actual = dao.initialize();
        Assert.assertTrue(actual);
    }

    // loading database

    @Test
    public void loadEmptyDatabaseTest() {
        TicketManager loaded = new TicketManager(dao);
        loaded.load();

        int actual = loaded.getAllTickets().size();
        Assert.assertEquals(0, actual);
    }

    // save and load

    @Test
    public void saveAndLoadTicketTest() {
        TicketManager saved = new TicketManager(dao);
        Ticket original = saved.createTicket("Write report");
        original.setDescription("Draft the final report");
        original.setAssignee("Sam");
        original.setStatus(Status.IN_REVIEW);
        original.setPriority(Priority.HIGH);
        original.setDuedate(LocalDate.of(2026, 8, 18));
        saved.save();

        TicketManager loaded = new TicketManager(dao);
        loaded.load();
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
        TicketManager saved = new TicketManager(dao);
        saved.createTicket("Bare ticket");
        saved.save();

        TicketManager loaded = new TicketManager(dao);
        loaded.load();
        Ticket actual = loaded.getTicket(1);

        Assert.assertNull(actual.getDescription());
        Assert.assertNull(actual.getAssignee());
        Assert.assertNull(actual.getDuedate());
    }

    @Test
    public void saveAndLoadDependenciesTest() {
        TicketManager saved = new TicketManager(dao);
        saved.createTicket("One");
        saved.createTicket("Two");
        Ticket three = saved.createTicket("Three");
        three.addDependency(1);
        three.addDependency(2);
        saved.save();

        TicketManager loaded = new TicketManager(dao);
        loaded.load();
        Ticket actual = loaded.getTicket(3);

        Assert.assertEquals(2, actual.getDependencies().size());
        Assert.assertTrue(actual.getDependencies().contains(1));
        Assert.assertTrue(actual.getDependencies().contains(2));
    }

    @Test
    public void saveReplacesOldDataTest() {
        TicketManager manager = new TicketManager(dao);
        manager.createTicket("Keep me");
        manager.createTicket("Remove me");
        manager.save();

        manager.removeTicket(2);
        manager.save();

        TicketManager loaded = new TicketManager(dao);
        loaded.load();

        Assert.assertEquals(1, loaded.getAllTickets().size());
        Assert.assertNull(loaded.getTicket(2));
    }

    @Test
    public void loadKeepsLastModifiedTest() {
        // a ticket last changed in 2020, with a dependency so loadDependencies runs too
        TicketManager saved = new TicketManager(dao);
        saved.createTicket("One");
        Ticket old = new Ticket(2, "Old", null, null, Status.TODO, null, LocalDate.of(2020, 1, 1), Priority.LOW);
        old.setDependencies(List.of(1));
        saved.addLoadedTicket(old);
        saved.save();

        TicketManager loaded = new TicketManager(dao);
        loaded.load();
        LocalDate actual = loaded.getTicket(2).getLastModified();
        Assert.assertEquals(LocalDate.of(2020, 1, 1), actual);
    }

    @Test
    public void nextIdAfterLoadTest() {
        TicketManager saved = new TicketManager(dao);
        saved.createTicket("One");
        saved.createTicket("Two");
        saved.save();

        TicketManager loaded = new TicketManager(dao);
        loaded.load();
        int actual = loaded.createTicket("Three").getId();
        Assert.assertEquals(3, actual);
    }
}
