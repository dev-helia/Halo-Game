package model.obstacle;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the GameObstacle abstract class via a concrete test subclass.
 */
public class GameObstacleTest {

  private GameObstacle obstacle;

  /**
   * A concrete implementation of GameObstacle used for testing.
   */
  private static class TestObstacle extends GameObstacle {
    public TestObstacle(String name, String description, boolean active, int value) {
      super(name, description, active, value);
    }
  }

  /**
   * Set up a default test obstacle instance before each test.
   */
  @BeforeEach
  public void setUp() {
    obstacle = new TestObstacle("Puzzle Box", "Solve the ancient puzzle to proceed.", true, 100);
  }

  /**
   * Tests that the name field is correctly initialized and returned.
   */
  @Test
  public void testGetName() {
    assertEquals("Puzzle Box", obstacle.getName(), "Obstacle name should match the constructor value.");
  }

  /**
   * Tests that the description field is correctly initialized and returned.
   */
  @Test
  public void testGetDescription() {
    assertEquals("Solve the ancient puzzle to proceed.", obstacle.getDescription(),
            "Obstacle description should match the constructor value.");
  }

  /**
   * Tests that the active status is correctly initialized.
   */
  @Test
  public void testIsActiveInitially() {
    assertTrue(obstacle.isActive(), "Obstacle should be active initially.");
  }

  /**
   * Tests the deactivate() method sets active to false.
   */
  @Test
  public void testDeactivate() {
    obstacle.deactivate();
    assertFalse(obstacle.isActive(), "Obstacle should be inactive after deactivation.");
  }

  /**
   * Tests that the value field is correctly initialized and returned.
   */
  @Test
  public void testGetValue() {
    assertEquals(100, obstacle.getValue(), "Obstacle value should match the constructor value.");
  }
}
