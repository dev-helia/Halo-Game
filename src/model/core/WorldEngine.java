package model.core;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import model.elements.*;
import model.obstacle.*;
import utils.JsonUtils;
import utils.RoomsParser;

import java.io.*;
import java.util.*;

/**
 * WorldEngine: Responsible for loading JSON data,
 * generating game maps,
 * and providing world status.
 */
public class WorldEngine implements Serializable {
  // fields and the default constructor
  private Map<Integer, Room> worldMap; // Whole room map: Room number -> Room object

  /**
   * Constructor (no seed default build)
   * Initialize the world map object:
   * key is the room number and the value is the Room object.
   */
  public WorldEngine() {
    this.worldMap = new HashMap<>();
  }

  /**
   * Main function to generate worlds from JSON files (map + elements).
   * Read the file → parse the room → parse items / fixtures / puzzles → stuff back to the room.
   *
   * @param jsonFilePath the file path of the target json file
   * @throws IOException input and output exception
   */
  public void generateWorld(String jsonFilePath) throws IOException {
    JsonObject root = JsonUtils.safeParseJson(jsonFilePath);
    RoomsParser.parseRooms(root, worldMap);


    //TODO Han
    public void parseItems(JsonObject root) {
      if (root.has("items")) {
        JsonArray itemsArray = root.getAsJsonArray("items");
        for (JsonElement element : itemsArray) {
          JsonObject itemObject = element.getAsJsonObject();

          // 从 JSON 中提取每个 item 的相关数据
          String name = itemObject.get("name").getAsString();
          double weight = itemObject.get("weight").getAsDouble();
          int maxUses = itemObject.get("max_uses").getAsInt();
          int usesRemaining = itemObject.get("uses_remaining").getAsInt();
          int value = itemObject.get("value").getAsInt();
          String whenUsed = itemObject.get("when_used").getAsString();

          // 创建 Item 对象并存入全局的 globalItems Map 中
          globalItems.put(name, new Item(name, weight, maxUses, usesRemaining, value, whenUsed));
        }
      }
    }

    // todo han）
    public void parseFixtures(JsonObject root) {
      if (root.has("fixtures")) {
        JsonArray fixturesArray = root.getAsJsonArray("fixtures");
        for (JsonElement element : fixturesArray) {
          JsonObject fixtureObject = element.getAsJsonObject();

          // 从 JSON 中提取每个 fixture 的相关数据
          String name = fixtureObject.get("name").getAsString();
          String description = fixtureObject.get("description").getAsString();
          int weight = fixtureObject.get("weight").getAsInt();

          // 创建 Fixture 对象并存入全局的 globalFixtures Map 中
          globalFixtures.put(name, new Fixture(name, description, weight));
        }
      }
    }
    // todo red bean
    // 5.解析 puzzles（用于匹配房间障碍）
    if (root.has("puzzles")) {
      JsonArray puzzlesArray = root.getAsJsonArray("puzzles");
      for (JsonElement element : puzzlesArray) {
        JsonObject p = element.getAsJsonObject();
        String name = p.get("name").getAsString();
        boolean active = p.get("active").getAsBoolean();
        boolean affectsTarget = p.get("affects_target").getAsBoolean();
        boolean affectsPlayer = p.get("affects_player").getAsBoolean();
        String solution = p.get("solution").getAsString();
        int value = p.get("value").getAsInt();
        String effects = p.get("effects").getAsString();
        int targetRoom = parseRoomNumber(p.get("target").getAsString());

        Puzzle puzzle = new Puzzle(name, name, active, value, solution, affectsTarget, affectsPlayer, effects, targetRoom);

        Room r = worldMap.get(targetRoom);
        if (r != null) r.setObstacle(puzzle);
      }
    }

    // TODO: red veab 解析怪物 monster（方式类似 Puzzle）

    // todo ht
    // 第二轮：给房间塞入 items 和 fixtures puzzles monsters
    for (Room room : worldMap.values()) {
      // 添加 items
      if (room.getItems() == null) continue;

      for (String itemName : extractNames(room, "items")) {
        if (globalItems.containsKey(itemName)) {
          room.addItem(cloneItem(globalItems.get(itemName))); // 深拷贝
        }
      }

      // 添加 fixtures
      for (String fixName : extractNames(room, "fixtures")) {
        if (globalFixtures.containsKey(fixName)) {
          room.addFixture(globalFixtures.get(fixName)); // fixtures 可以复用
        }
      }
    }
  }

  /**
   * Print the current world map for simple smoke testing.
   */
  public void printWorldMap() {
    System.out.println("=== Game World Map ===");
    // print rooms
    for (Map.Entry<Integer, Room> entry : worldMap.entrySet()) {
      Room room = entry.getValue();
      System.out.println("Room " + room.getRoomNumber() + ": " + room.getName());
      System.out.println("Description " + room.getRoomDescription());
      System.out.println("  Exits -> N: " + room.getExit("N")
              + ", S: " + room.getExit("S")
              + ", E: " + room.getExit("E")
              + ", W: " + room.getExit("W"));

      // items and fixtures in the current room
      if (room.getItems() != null && !room.getItems().isEmpty()) {
        System.out.println("  Items: ");
        for (Item item : room.getItems()) {
          System.out.println("    - " + item.getName());
        }
      }

      if (room.getFixtures() != null && !room.getFixtures().isEmpty()) {
        System.out.println("  Fixtures: ");
        for (Fixture fixture : room.getFixtures()) {
          System.out.println("    - " + fixture.getName());
        }
      }

      System.out.println();
    }
  }

  // ==== getter ====

  /**
   * Room getter.
   *
   * @param roomNumber the room number
   * @return the room
   */
  public Room getRoom(int roomNumber) {
    return worldMap.get(roomNumber);
  }

  /**
   * worldMap getter.
   *
   * @return the world map
   */
  public Map<Integer, Room> getWorldMap() {
    return worldMap;
  }

  // ==== helper ====

  /**
   * Safely parse rooms from root object.
   *
   * @param root the root json object
   * @throws IOException file not found
   */
  private void parseRooms(JsonObject root) throws IOException {
    // Check if the rooms field is included
    if (!root.has("rooms") || !root.get("rooms").isJsonArray()) {
      throw new IOException("Invalid JSON file: Missing 'rooms' field, or the field is not an array!");
    }

    JsonArray roomsArray = root.getAsJsonArray("rooms");

    for (JsonElement element : roomsArray) {
      // Make sure each element is an object
      if (!element.isJsonObject()) {
        throw new IOException("There are illegal elements (not objects) in the rooms array:" + element);
      }

      JsonObject roomObj = element.getAsJsonObject();

      try {
        // Call parseRoom and handle exceptions
        Room room = parseRoom(roomObj);

        if (room == null) {
          throw new IOException("parseRoom returns null, please check the field:" + roomObj);
        }

        int number = room.getRoomNumber();
        if (worldMap.containsKey(number)) {
          throw new IOException("Repeated room numbers: " + number + ". Please check the JSON configuration!");
        }

        worldMap.put(number, room);

      } catch (Exception e) {
        throw new IOException("Failed to parse the room: " + roomObj + ", reason:" + e.getMessage(), e);
      }
    }
  }





  // 提取以逗号分隔的名字列表
  // 你还没实现：需要你去 Room 类存储原始 "Pen, Eraser" 这样的字符串字段
  private List<String> extractNames(Room room, String field) {
    List<String> names = new ArrayList<>();
    String raw = room.getRawField(field);  // gets the raw string like "Key, Lamp"
    if (raw != null && !raw.isBlank()) {
      for (String name : raw.split(",")) { // splits into ["Key", " Lamp"]
        names.add(name.trim());            // trims whitespace: "Key", "Lamp"
      }
    }
    return names;
  }

  // 拷贝物品（避免同一个 item 被多个房间引用） 避免共享引用造成冲突
  private Item cloneItem(Item i) {
    return new Item(
            i.getName(),
            i.getDescription(),    // 2nd argument
            i.getWeight(),         // double
            i.getMaxUses(),
            i.getUsesRemaining(),
            i.getValue(),
            i.getWhenUsed()
    );
  }

  // 解析 room target 格式 “1:RoomName” → 1 只提取前面的房间号
  private int parseRoomNumber(String input) {
    String[] parts = input.split(":");
    return Integer.parseInt(parts[0].trim());
  }

  public boolean saveState(String filePath, Player player) {
    try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filePath))) {
      out.writeObject(this);
      out.writeObject(player);
      return true;
    } catch (IOException e) {
      return false;
    }
  }

  public boolean restoreState(String filePath, Player playerRef) {
    try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(filePath))) {
      WorldEngine loadedWorld = (WorldEngine) in.readObject();
      Player loadedPlayer = (Player) in.readObject();

      this.worldMap = loadedWorld.worldMap;
      playerRef.copyFrom(loadedPlayer);
      return true;
    } catch (IOException | ClassNotFoundException e) {
      return false;
    }
  }
}
