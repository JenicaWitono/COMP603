//written with assistance from Claude.ai
import java.util.List;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TicketManagerTest {

    private TicketManager manager;

    // Runs before each test case: gives every test an empty manager
    @Before
    public void setUp() {
        manager = new TicketManager();
    }

    // creating and finding tickets

    @Test
    public void createTicketIdsTest() {
        Ticket first = manager.createTicket("First");
        Ticket second = manager.createTicket("Second");

        int actualFirst = first.getId();
        int actualSecond = second.getId();
        Assert.assertEquals(1, actualFirst);
        Assert.assertEquals(2, actualSecond);
    }

    @Test
    public void getTicketTest() {
        Ticket created = manager.createTicket("Find me");
        Ticket actual = manager.getTicket(created.getId());
        Assert.assertSame(created, actual);
    }

    @Test
    public void getUnknownTicketTest() {
        Ticket actual = manager.getTicket(42);
        Assert.assertNull(actual);
    }

    @Test
    public void getAllTicketsSortedByIdTest() {
        manager.addLoadedTicket(new Ticket(9, "Nine"));
        manager.addLoadedTicket(new Ticket(2, "Two"));
        manager.addLoadedTicket(new Ticket(5, "Five"));

        List<Ticket> all = manager.getAllTickets();
        Assert.assertEquals(2, all.get(0).getId());
        Assert.assertEquals(5, all.get(1).getId());
        Assert.assertEquals(9, all.get(2).getId());
    }

    // removing tickets

    @Test
    public void removeTicketTest() {
        Ticket t = manager.createTicket("Remove me");
        manager.removeTicket(t.getId());
        Ticket actual = manager.getTicket(t.getId());
        Assert.assertNull(actual);
    }

    @Test
    public void removeTicketCleansUpDependenciesTest() {
        Ticket one = manager.createTicket("One");
        Ticket two = manager.createTicket("Two");
        two.addDependency(one.getId());

        manager.removeTicket(one.getId());

        boolean actual = two.getDependencies().contains(one.getId());
        Assert.assertFalse(actual);
    }

    // dependency rules

    @Test
    public void unfinishedDependencyBlocksTest() {
        Ticket dependency = manager.createTicket("Dependency");
        Ticket main = manager.createTicket("Main");
        main.addDependency(dependency.getId());
        dependency.setStatus(Status.IN_PROGRESS);

        boolean actual = manager.incompleteDependencyText(main.getDependencies()).isEmpty();
        Assert.assertFalse(actual);
    }

    @Test
    public void doneDependencyAllowsTest() {
        Ticket dependency = manager.createTicket("Dependency");
        Ticket main = manager.createTicket("Main");
        main.addDependency(dependency.getId());
        dependency.setStatus(Status.DONE);

        boolean actual = manager.incompleteDependencyText(main.getDependencies()).isEmpty();
        Assert.assertTrue(actual);
    }

    // parsing the dependencies input

    @Test
    public void parseDependenciesTest() {
        manager.createTicket("One");
        manager.createTicket("Two");
        List<Integer> actual = manager.parseDependencies(" 1, 2 ", 0);
        Assert.assertEquals(List.of(1, 2), actual);
    }

    @Test
    public void parseDependenciesUnknownIdTest() {
        try {
            manager.parseDependencies("42", 0);
            Assert.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            //ticket 42 doesn't exist
        }
    }

    // dependency loops

    @Test
    public void directLoopDetectedTest() {
        Ticket one = manager.createTicket("One");
        Ticket two = manager.createTicket("Two");
        two.addDependency(one.getId());

        // one -> two would make one -> two -> one
        boolean actual = manager.wouldCreateCycle(one.getId(), List.of(two.getId()));
        Assert.assertTrue(actual);
    }

    // reset

    @Test
    public void resetTest() {
        manager.createTicket("One");
        manager.createTicket("Two");

        manager.reset();

        int actualCount = manager.getAllTickets().size();
        int actualNextId = manager.createTicket("Fresh start").getId();
        Assert.assertEquals(0, actualCount);
        Assert.assertEquals(1, actualNextId);
    }
}
