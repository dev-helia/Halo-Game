import model.core.Player;
import model.core.Room;
import model.core.HealthStatus;
import model.core.PlayerRank;
import model.elements.Item;
import model.obstacle.Puzzle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Player class using theme-neutral data.
 */
public class PlayerTest {

  private Player player;
  private Room roomA;
  private Room roomB;

  /**
   * Set up a player and two rooms before each test.
   * roomA is the starting point, with a north exit to roomB.
   */
  @BeforeEach
  public void setUp() {
    roomA = new Room(1, "Room A", "Generic room A for testing.");
    roomB = new Room(2, "Room B", "Generic room B for testing.");
    roomA.setExit("N", 2);  // one valid direction
    player = new Player("Tester", roomA);
  }

  /**
   * Verify initial player state: health, room, inventory, and name.
   */
  @Test
  public void testInitialPlayerState() {
    assertEquals("Tester", player.getName());
    assertEquals(100.0, player.getHealth());
    assertEquals(roomA, player.getCurrentRoom());
    assertTrue(player.getInventory().isEmpty());
    assertEquals(HealthStatus.AWAKE, player.getHealthStatus());
  }

  /**
   * Test that the player can move in a valid direction.
   */
  @Test
  public void testMoveToValidRoom() {
    Map<Integer, Room> map = Map.of(1, roomA, 2, roomB);
    assertTrue(player.move("N", map));
    assertEquals(roomB, player.getCurrentRoom());
  }

  /**
   * Test that movement fails in an invalid direction.
   */
  @Test
  public void testMoveInvalidDirection() {
    Map<Integer, Room> map = Map.of(1, roomA, 2, roomB);
    assertFalse(player.move("E", map));
    assertEquals(roomA, player.getCurrentRoom());
  }

  /**
   * Test picking up an item that is within the allowed weight limit.
   */
  @Test
  public void testPickItemWithinWeightLimit() {
    Item item = new Item("Item A", "Basic item", 2.0, 2,
            2, 10, "Item used.");
    roomA.addItem(item);
    assertTrue(player.pickItem("Item A"));
    assertEquals(1, player.getInventory().size());
  }

  /**
   * Test that a too-heavy item cannot be picked up.
   */
  @Test
  public void testPickItemTooHeavy() {
    Item item = new Item("Heavy Item", "Too heavy", 50.0, 1,
            1, 10, "Crash");
    roomA.addItem(item);
    assertFalse(player.pickItem("Heavy Item"));
    assertTrue(player.getInventory().isEmpty());
  }

  /**
   * Test that dropping an item works and returns it to the room.
   */
  @Test
  public void testDropItem() {
    Item item = new Item("DropMe", "Item to drop", 1.0, 1,
            1, 5, "Dropped");
    roomA.addItem(item);
    player.pickItem("DropMe");

    assertTrue(player.dropItem("DropMe"));
    assertTrue(player.getInventory().isEmpty());
    assertNotNull(roomA.getItem("DropMe"));
  }

  /**
   * Test using an item that has uses remaining.
   */
  @Test
  public void testUseItemSuccess() {
    Item item = new Item("Usable", "Test item", 1.0, 2,
            2, 5, "Zap!");
    roomA.addItem(item);
    player.pickItem("Usable");

    String result = player.useItem("Usable");
    assertEquals("Zap!", result);
    assertEquals(1, item.getUsesRemaining());
  }

  /**
   * Test using an item that has no uses remaining.
   */
  @Test
  public void testUseItemExhausted() {
    Item item = new Item("OneShot", "Used once", 1.0, 1,
            1, 5, "Boom");
    roomA.addItem(item);
    player.pickItem("OneShot");

    player.useItem("OneShot"); // Use up the item
    String result = player.useItem("OneShot");
    assertEquals("You can't use that item anymore.", result);
  }

  /**
   * Test trying to use an item not in the inventory.
   */
  @Test
  public void testUseItemNotInInventory() {
    String result = player.useItem("GhostItem");
    assertEquals("Item not found in inventory.", result);
  }

  /**
   * Test the health system and transitions between different health statuses.
   */
  @Test
  public void testTakeDamageAndHealthStatus() {
    player.takeDamage(30);
    assertEquals(70, player.getHealth());
    assertEquals(HealthStatus.AWAKE, player.getHealthStatus());

    player.takeDamage(35);
    assertEquals(35, player.getHealth());
    assertEquals(HealthStatus.WOOZY, player.getHealthStatus());

    player.takeDamage(50);
    assertEquals(0, player.getHealth());
    assertEquals(HealthStatus.SLEEP, player.getHealthStatus());
    assertFalse(player.isAlive());
  }

  /**
   * Test score updating and correct player rank mapping.
   */
  @Test
  public void testScoringAndRank() {
    assertEquals(PlayerRank.NOVICE, player.getRank());

    player.updateScore(80);
    assertEquals(PlayerRank.INTERMEDIATE, player.getRank());

    player.updateScore(100);
    assertEquals(PlayerRank.EXPERT, player.getRank());
  }

  /**
   * Test solving a puzzle with the correct answer updates score and deactivates puzzle.
   */
  @Test
  public void testSolvePuzzleCorrectAnswer() {
    Puzzle puzzle = new Puzzle("Door", "Locked door", true, 50,
            "'yes'", true, false, "You cannot pass.", 1, "Say the magic word.");
    roomA.setObstacle(puzzle);

    boolean solved = player.answerCorrect("yes", roomA);
    assertTrue(solved);
    assertFalse(roomA.hasObstacle());
    assertEquals(50, player.getScore());
  }

  /**
   * Test failing to solve a puzzle with the wrong answer.
   */
  @Test
  public void testSolvePuzzleWrongAnswer() {
    Puzzle puzzle = new Puzzle("Door", "Locked door", true, 50,
            "'yes'", true, false, "You cannot pass.", 1, "Say the magic word.");
    roomA.setObstacle(puzzle);

    boolean solved = player.answerCorrect("nope", roomA);
    assertFalse(solved);
    assertTrue(roomA.hasObstacle());
    assertEquals(0, player.getScore());
  }
}
