package model.core;

import java.io.IOException;

import static org.junit.Assert.*;
import static utils.fileutil.PathUtils.getMapPath;


import model.obstacle.Puzzle;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;


/**
 * The type World engine test.
 */
public class WorldEngineTest {
  private WorldEngine engine;

  /**
   * Sets up.
   *
   * @throws IOException the io exception
   */
  @Before
  public void setUp() throws IOException {
    engine = new WorldEngine();
    engine.generateWorld(getMapPath("Museum_of_Planet_of_the_Apes"));
  }

  /**
   * Test room count.
   */
  @Test
  public void testRoomCount() {
    Map<Integer, Room> worldMap = engine.getWorldMap();
    assertEquals("Room count should be 4", 4, worldMap.size());
  }

  /**
   * Test room has puzzle.
   */
  @Test
  public void testRoomHasPuzzle() {
    Room room = engine.getRoom(1);
    assertNotNull("Room 1 should have an obstacle", room.getObstacle());
    assertTrue("Obstacle should be a Puzzle", room.getObstacle() instanceof Puzzle);
    assertEquals("Puzzle name mismatch", "TURNSTILE", room.getObstacle().getName().toUpperCase());
  }

  /**
   * Test room has items.
   */
  @Test
  public void testRoomHasItems() {
    Room room = engine.getRoom(1);
    assertNotNull("Room 1 should have items", room.getItems());
    assertFalse("Room 1 items should not be empty", room.getItems().isEmpty());
    assertEquals("First item should be Ticket", "Ticket", room.getItems().get(0).getName());
  }

  /**
   * Test room with fixture.
   */
  @Test
  public void testRoomWithFixture() {
    Room room = engine.getRoom(2);
    assertNotNull("Room 2 should have fixture", room.getFixtures());
    assertEquals("First fixture should be Computer", "Computer", room.getFixtures().get(0).getName());
  }
}



