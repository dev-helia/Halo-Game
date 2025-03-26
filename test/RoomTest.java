import model.core.Room;
import model.elements.Item;
import model.elements.Fixture;
import model.obstacle.Puzzle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

/**
 * Unit tests for the Room class.
 */
public class RoomTest {

  private Room room;

  /**
   * Sets up a basic, reusable room before each test.
   * Data can be changed without tying to one specific world.
   */
  @BeforeEach
  public void setUp() {
    room = new Room(101, "Test Room",
            "This is a test room for unit testing.");
  }

  /**
   * Test room name and number getters.
   */
  @Test
  public void testRoomIdentity() {
    assertEquals(101, room.getRoomNumber());
    assertEquals("Test Room", room.getName());
  }

  /**
   * Test setting and getting directional exits.
   */
  @Test
  public void testSetAndGetExits() {
    room.setExit("N", 202);
    room.setExit("S", 0); // wall
    assertEquals(202, room.getExit("N"));
    assertEquals(0, room.getExit("S"));
    assertEquals(0, room.getExit("W")); // not set, should return 0
  }

  /**
   * Test adding and retrieving an item from the room.
   */
  @Test
  public void testAddAndGetItem() {
    Item testItem = new Item("Lantern", "A glowing lantern.", 2.0,
            3, 3, 10, "The lantern glows brightly.");
    room.addItem(testItem);
    Item result = room.getItem("Lantern");
    assertNotNull(result);
    assertEquals("Lantern", result.getName());
  }

  /**
   * Test removing an item.
   */
  @Test
  public void testRemoveItem() {
    Item i = new Item("Map", "A dusty old map.", 0.5, 1,
            1, 3, "You examine the map.");
    room.addItem(i);
    Item removed = room.removeItem("Map");
    assertNotNull(removed);
    assertEquals("Map", removed.getName());
    assertNull(room.getItem("Map"));
  }

  /**
   * Test fixture behavior.
   */
  @Test
  public void testAddAndGetFixture() {
    Fixture fix = new Fixture("Statue", "A heavy bronze statue.", 500);
    room.addFixture(fix);
    Fixture result = room.getFixture("Statue");
    assertNotNull(result);
    assertEquals("Statue", result.getName());
  }

  /**
   * Test obstacle handling.
   */
  @Test
  public void testSetAndDeactivateObstacle() {
    Puzzle p = new Puzzle("Gate", "A glowing forcefield.", true, 100,
            "'OPEN'", true, false, "Blocks your path.",
            101, "Try a password.");
    room.setObstacle(p);
    assertTrue(room.hasObstacle());
    assertEquals("Gate", room.getObstacle().getName());

    room.deactivateObstacle();
    assertFalse(room.hasObstacle()); // should now be deactivated
  }

  /**
   * Test setting and getting raw string fields (from JSON).
   */
  @Test
  public void testRawFieldStorage() {
    room.setRawField("items", "Lantern, Map, Coin");
    assertEquals("Lantern, Map, Coin", room.getRawField("items"));
  }

  /**
   * Test empty lists for items and fixtures before any are added.
   */
  @Test
  public void testEmptyState() {
    assertTrue(room.getItems().isEmpty());
    assertTrue(room.getFixtures().isEmpty());
    assertNull(room.getObstacle());
  }

  /**
   * Test description and toString output.
   */
  @Test
  public void testDescriptionAndToString() {
    assertEquals("This is a test room for unit testing.", room.getRoomDescription());
    room.setDescription("Updated room description.");
    assertEquals("Updated room description.", room.getRoomDescription());

    String str = room.toString();
    assertTrue(str.contains("Room 101: Test Room"));
  }
}
