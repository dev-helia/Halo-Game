package utils.obstacleparser;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.Map;

import model.core.Room;
import model.obstacle.Monster;

import static utils.roomparser.RoomNumberParser.parseRoomNumber;

public class MonsterParser {

  /**
   * Parses the "monsters" section from the JSON root.
   * For each monster entry, this method creates a Monster instance
   * and assigns it to the corresponding room in the game world.
   *
   * @param root     the root JSON object containing monsters
   * @param worldMap the map of room IDs to Room objects
   */
  public static void parseMonsters(JsonObject root, Map<Integer, Room> worldMap) {
    if (root.has("monsters")) {
      JsonArray monstersArray = root.getAsJsonArray("monsters");

      for (JsonElement element : monstersArray) {
        JsonObject m = element.getAsJsonObject();

        String name = getAsStringOrDefault(m, "name", "Unknown");
        String description = getAsStringOrDefault(m, "description", "");
        boolean active = getAsBooleanOrDefault(m, "active", false);
        int value = getAsIntOrDefault(m, "value", 0);
        int damage = getAsIntOrDefault(m, "damage", 0);
        boolean canAttack = getAsBooleanOrDefault(m, "can_attack", false);
        String attackMessage = getAsStringOrDefault(m, "attack", "");
        String defeatItem = getAsStringOrDefault(m, "solution", "");
        int targetRoom = parseRoomNumber(getAsStringOrDefault(m, "target", "0:Unknown"));


        // Create a Monster object with parsed attributes
        Monster monster = new Monster(
                name,
                description,
                active,
                value,
                damage,
                canAttack,
                attackMessage,
                defeatItem
        );

        // Assign the monster to the correct room in the world map
        Room r = worldMap.get(targetRoom);
        if (r != null) {
          r.setObstacle(monster);
        } else {
          System.err.printf("Room #%d not found — monster '%s' not assigned.%n", targetRoom, name);
        }
      }
    }
  }
  private static String getAsStringOrDefault(JsonObject obj, String key, String defaultVal) {
    return obj.has(key) && obj.get(key).isJsonPrimitive() ? obj.get(key).getAsString() : defaultVal;
  }

  private static int getAsIntOrDefault(JsonObject obj, String key, int defaultVal) {
    return obj.has(key) && obj.get(key).isJsonPrimitive() ? obj.get(key).getAsInt() : defaultVal;
  }

  private static boolean getAsBooleanOrDefault(JsonObject obj, String key, boolean defaultVal) {
    return obj.has(key) && obj.get(key).isJsonPrimitive() ? obj.get(key).getAsBoolean() : defaultVal;
  }

}
