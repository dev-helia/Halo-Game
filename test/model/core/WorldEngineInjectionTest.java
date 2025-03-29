package model.core;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import model.elements.Fixture;
import model.elements.Item;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


/**
 * The type World engine injection test.
 */
public class WorldEngineInjectionTest {
  private WorldEngine engine;

  /**
   * Sets up.
   *
   * @throws IOException the io exception
   */
  @Before
  public void setUp() throws IOException {
    engine = new WorldEngine();
    String mapPath = Paths.get(System.getProperty("user.dir"), "src", "resources", "maps", "Museum_of_Planet_of_the_Apes.json").toString();

  }

  /**
   * Test items and fixtures injection.
   */
  @Test
  public void testItemsAndFixturesInjection() {
    Map<Integer, Room> worldMap = engine.getWorldMap();

    for (Map.Entry<Integer, Room> entry : worldMap.entrySet()) {
      Room room = entry.getValue();
      int roomNum = room.getRoomNumber();

      String itemRaw = room.getRawField("items");
      if (itemRaw != null && !itemRaw.isBlank()) {
        List<Item> items = room.getItems();
        assertNotNull("Room " + roomNum + " should have item list", items);
        String[] expectedNames = itemRaw.split(",");
        assertEquals("Room " + roomNum + " should have correct item count",
                expectedNames.length, items.size());
      }

      String fixtureRaw = room.getRawField("fixtures");
      if (fixtureRaw != null && !fixtureRaw.isBlank()) {
        List<Fixture> fixtures = room.getFixtures();
        assertNotNull("Room " + roomNum + " should have fixture list", fixtures);
        String[] expectedNames = fixtureRaw.split(",");
        assertEquals("Room " + roomNum + " should have correct fixture count",
                expectedNames.length, fixtures.size());
      }
    }
  }
}