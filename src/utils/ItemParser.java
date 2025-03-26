package utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.Map;

import model.core.Room;
import model.elements.Item;

public class ItemParser {

  /**
   * Parses the "items" section from the root JSON object.
   * For each item entry, this method extracts item data, clamps missing values,
   * creates an Item instance, and assigns it to the corresponding room.
   *
   * @param root     the root JSON object containing items
   * @param worldMap the map of room IDs to Room objects
   */
  public static void parseItems(JsonObject root, Map<Integer, Room> worldMap) {
    if (root.has("items")) {
      JsonArray itemsArray = root.getAsJsonArray("items");
      for (JsonElement element : itemsArray) {
        JsonObject itemObj = element.getAsJsonObject();

        // Extract item fields and handle missing data (clamping)
        String name = itemObj.has("name") ? itemObj.get("name").getAsString() : "Unnamed Item";  // Clamp missing name
        String description = itemObj.has("description") ? itemObj.get("description").getAsString() : "No description available";  // Clamp missing description
        double weight = itemObj.has("weight") ? itemObj.get("weight").getAsDouble() : 0.0;  // Clamp missing weight
        int maxUses = itemObj.has("max_uses") ? itemObj.get("max_uses").getAsInt() : 1;  // Clamp missing max_uses
        int usesRemaining = itemObj.has("uses_remaining") ? itemObj.get("uses_remaining").getAsInt() : maxUses;  // Clamp missing uses_remaining
        int value = itemObj.has("value") ? itemObj.get("value").getAsInt() : 0;  // Clamp missing value
        String whenUsed = itemObj.has("when_used") ? itemObj.get("when_used").getAsString() : "No information";  // Clamp missing when_used

        // Create the Item object
        Item item = new Item(name, description, weight, maxUses, usesRemaining, value, whenUsed);

        // Assign the item to the appropriate room
        if (itemObj.has("room_number")) {
          int roomNumber = itemObj.get("room_number").getAsInt();
          Room room = worldMap.get(roomNumber);
          if (room != null) {
            room.addItem(item);  // Add the item to the room
          } else {
            System.err.println("Room " + roomNumber + " not found for item " + name);  // Error handling for missing room
          }
        }
      }
    }
  }
}

