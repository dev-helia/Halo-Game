package utils.obstacleparser;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.Map;

import model.core.Room;
import model.obstacle.Puzzle;

import static utils.roomparser.RoomNumberParser.parseRoomNumber;

public class PuzzleParser {

  /**
   * Parses the "puzzles" section from the JSON root.
   * For each puzzle entry, this method creates a Puzzle instance
   * and assigns it to the corresponding room in the game world.
   *
   * @param root     the root JSON object containing puzzles
   * @param worldMap the map of room IDs to Room objects
   */
  public static void parsePuzzles(JsonObject root, Map<Integer, Room> worldMap) {
    if (!root.has("puzzles") || !root.get("puzzles").isJsonArray()) {
      System.err.println("Warning: No 'puzzles' array found.");
      return;
    }

    JsonArray puzzlesArray = root.getAsJsonArray("puzzles");

    for (JsonElement element : puzzlesArray) {
      if (!element.isJsonObject()) continue;
      JsonObject p = element.getAsJsonObject();

      try {
        // Required fields
        String name = p.get("name").getAsString();
        boolean active = p.get("active").getAsBoolean();
        int value = p.get("value").getAsInt();
        String solution = p.get("solution").getAsString();
        boolean affectsTarget = p.get("affects_target").getAsBoolean();
        boolean affectsPlayer = p.get("affects_player").getAsBoolean();
        String effects = p.get("effects").getAsString();
        int targetRoom = parseRoomNumber(p.get("target").getAsString());

        // Optional fields
        String description = p.has("description") && !p.get("description").isJsonNull()
                ? p.get("description").getAsString()
                : "An unsolved puzzle.";

        String hintMessage = p.has("hintMessage") && !p.get("hintMessage").isJsonNull()
                ? p.get("hintMessage").getAsString()
                : "";

        // Create puzzle object
        Puzzle puzzle = new Puzzle(
                name,
                description,
                active,
                value,
                solution,
                affectsTarget,
                affectsPlayer,
                effects,
                targetRoom,
                hintMessage
        );

        // Attach to correct room
        Room r = worldMap.get(targetRoom);
        if (r != null) {
          r.setObstacle(puzzle);
        } else {
          System.err.printf("Room #%d not found — puzzle '%s' not assigned.%n", targetRoom, name);
        }

      } catch (Exception e) {
        System.err.println("Skipping puzzle due to parsing error: " + e.getMessage());
      }
    }
  }
}
