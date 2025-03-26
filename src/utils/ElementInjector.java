package utils;

import model.elements.*;
import model.obstacle.*;
import model.core.Room;

import java.util.*;

/**
 * The type Element injector.
 */
public class ElementInjector {

  /**
   * Inject items and fixtures.
   *
   * @param worldMap       the world map
   * @param globalItems    the global items
   * @param globalFixtures the global fixtures
   */
  public static void injectItemsAndFixtures(Map<Integer, Room> worldMap,
                                            Map<String, Item> globalItems,
                                            Map<String, Fixture> globalFixtures) {
    for (Room room : worldMap.values()) {
      if (room.getItems() == null) continue;

      for (String itemName : extractNames(room, "items")) {
        if (globalItems.containsKey(itemName)) {
          room.addItem(cloneItem(globalItems.get(itemName)));
        }
      }

      for (String fixName : extractNames(room, "fixtures")) {
        if (globalFixtures.containsKey(fixName)) {
          room.addFixture(globalFixtures.get(fixName));
        }
      }
    }
  }

  /**
   * Inject puzzles.
   *
   * @param puzzles  the puzzles
   * @param worldMap the world map
   */
// 🧩 塞 puzzle
  public static void injectPuzzles(List<Puzzle> puzzles, Map<Integer, Room> worldMap) {
    for (Puzzle p : puzzles) {
      int targetRoom = p.getTargetRoom();
      Room r = worldMap.get(targetRoom);
      if (r != null) {
        r.setObstacle(p);
      } else {
        System.err.printf("[Puzzle] Room %d not found for puzzle '%s'.\n", targetRoom, p.getName());
      }
    }
  }

  /**
   * Inject monsters.
   *
   * @param monsters the monsters
   * @param worldMap the world map
   */
// 👹 塞 monster
  public static void injectMonsters(List<Monster> monsters, Map<Integer, Room> worldMap) {
    for (Monster m : monsters) {
      int targetRoom = m.getTargetRoom();
      Room r = worldMap.get(targetRoom);
      if (r != null) {
        r.setObstacle(m);
      } else {
        System.err.printf("[Monster] Room %d not found for monster '%s'.\n", targetRoom, m.getName());
      }
    }
  }

  // 工具函数：提取逗号分隔字段
  private static List<String> extractNames(Room room, String field) {
    List<String> names = new ArrayList<>();
    String raw = room.getRawField(field);
    if (raw != null && !raw.isBlank()) {
      for (String name : raw.split(",")) {
        names.add(name.trim());
      }
    }
    return names;
  }

  // 深拷贝 item（避免引用冲突）
  private static Item cloneItem(Item i) {
    return new Item(
            i.getName(),
            i.getDescription(),
            i.getWeight(),
            i.getMaxUses(),
            i.getUsesRemaining(),
            i.getValue(),
            i.getWhenUsed()
    );
  }
}
