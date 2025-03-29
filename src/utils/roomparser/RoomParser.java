package utils.roomparser;

import com.google.gson.JsonObject;

import java.util.List;

import model.core.Room;

/**
 * The type Room parser.
 */
public class RoomParser {
  /**
   * Parse the specific room.
   *
   * @param obj the obj
   * @return the room
   */
  public static Room parseRoom(JsonObject obj) {
    // Get the room number from JSON file.
    int num = obj.get("room_number").getAsInt();
    // Get the room name from JSON file.
    String name = obj.get("room_name").getAsString();
    // Set the room description if it exists
    String description = (obj.has("description") && !obj.get("description").isJsonNull())
            ? obj.get("description").getAsString()
            : null;
    // Create a Room object using room number and name.
    Room r = new Room(num, name, description);

    // Set exits (N/S/E/W) to their target room numbers
    for (String dir : List.of("N", "S", "E", "W")) {
      if (obj.has(dir) && !obj.get(dir).isJsonNull()) {
        try {
          int targetRoom = obj.get(dir).getAsInt();
          if (targetRoom != 0) {
            r.setExit(dir, targetRoom); // only set exit if it's not 0
          }
        } catch (NumberFormatException e) {
          System.err.println("Exit from direction " + dir + " in room " + num + "is not valid");
        }
      }
    }

    // Temporarily store the raw "items" string (e.g. "Lamp, Key")
    if (obj.has("items") && !obj.get("items").isJsonNull()) {
      r.setRawField("items", obj.get("items").getAsString()); // You'll implement this in the Room class
    }

    // Temporarily store the raw "fixtures" string (e.g. "Desk, Painting")
    if (obj.has("fixtures") && !obj.get("fixtures").isJsonNull()) {
      r.setRawField("fixtures", obj.get("fixtures").getAsString()); // Also to be added in Room class
    }

    // Done parsing this Room
    return r;
  }
}