package utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.Map;

import model.core.Room;
import model.obstacle.Monster;

import static utils.RoomNumberParser.parseRoomNumber;

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

        // Extract monster properties from JSON
        String name = m.get("name").getAsString();
        String description = m.get("description").getAsString();
        boolean active = m.get("active").getAsBoolean();
        int value = m.get("value").getAsInt();
        int damage = m.get("damage").getAsInt();
        boolean canAttack = m.get("can_attack").getAsBoolean();
        String attackMessage = m.get("attack_message").getAsString();
        String defeatItem = m.get("defeat_item").getAsString();
        int targetRoom = parseRoomNumber(m.get("target").getAsString());

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
}
