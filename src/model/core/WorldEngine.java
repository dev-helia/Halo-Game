package model.core;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import model.elements.*;
import model.obstacle.*;

import java.io.*;
import java.util.*;

/**
 * WorldEngine：负责加载 JSON 数据、生成游戏地图、提供世界状态
 * 只负责读 JSON + 构建 world map，没依赖 controller
 * Render the game
 */

public class WorldEngine {
  // fields and the default constructor
  // 整个房间地图: 房间编号 -> Room 对象
  private Map<Integer, Room> worldMap;

  // 构造器（无 seed 默认构建）initialize the object 键是房间编号，值是 Room 对象
  public WorldEngine() {
    this.worldMap = new HashMap<>();
  }

  /**
   * 从 JSON 文件生成世界（地图 + 元素）
   * main function
   * 读取文件 → 解析房间 → 解析 items / fixtures / puzzles → 塞回房间
   */
  public void generateWorld(String jsonFilePath) throws IOException {
    // 1. 读取 JSON 内容
    // read the file
    Reader reader = new FileReader(jsonFilePath);
    // Gson 解析 JSON 文件
    // root 是你整个 JSON 的最外层对象（也就是含有 "rooms", "items" 的那一层）
    // 从你用 FileReader 打开的 JSON 文件里，把内容读取出来，
    // 然后转成一个 JsonObject，你就可以像访问字典一样操作这个 JSON 结构了！
    //JsonParser parser = new JsonParser();
    //JsonElement element = parser.parseReader(reader);
    //JsonObject root = element.getAsJsonObject();
    // TODO ht
    //  你必须确保 JSON 文件的根是一个 {} 对象，而不是数组 []，否则 getAsJsonObject() 会报错！
    //  reader 的内容必须是标准 JSON 格式，否则也会 JsonSyntaxException 崩掉！
    JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();

    // 2. 先解析房间 most important
    // put rooms into worldMap
    /**
     * "rooms":[
     *     { "room_name":"Courtyard", "room_number": "1",
     *       "description":"A beautiful courtyard with flowers on both sides of the stone walkway. \nThe walkway leads north. A billboard is in the distance.",
     *       "N": "2", "S": "0", "E": "0", "W": "0","puzzle": null, "monster": null, "items": "Hair Clippers", "fixtures": "Billboard","picture": "courtyard.png" },
     *     { "room_name":"Mansion Entrance", "room_number": "2",
     *       "description":"Entrance to an old, musty-smelling mansion. Some people have entered, to never return. \nThe door to the north is open. The courtyard is to your south and a foyer to your north. A chandelier hangs from the ceiling.",
     *       "N": "3", "S": "1", "E": "0", "W": "0","puzzle": null, "monster": null, "items": "Thumb Drive, Modulo 2", "fixtures": "Chandelier", "picture": "entrance.png" },
     *       ]
     */
    // TODO ht
    //  ❌ NullPointerException	JSON 文件有没有 "rooms" 字段？roomObj 中字段名是否拼错？
    // ❌ IllegalStateException	调用了 getAsJsonObject() 但其实是数组或 null
    // ❌ JsonSyntaxException	JSON 格式不标准，缺逗号、引号不闭合等问题
    // ❌ Map.put() 报错	是否有重复房间号？或者 room 为 null？

    // 👉 从 JSON 根对象中获取 "rooms" 字段，它必须是一个数组！
    JsonArray roomsArray = root.getAsJsonArray("rooms");
    // 👉 遍历这个数组，每一个元素是 JsonElement，代表一个房间对象！
    for (JsonElement element : roomsArray) {
      // 👉 把这个元素（其实是一个 JSON 对象）转换成 JsonObject 类型！
      JsonObject roomObj = element.getAsJsonObject();
      // 调用你自己写的 parseRoom 方法，把 JSON 转成 Java 对象 Room！
      // TODO parseRoom
      Room room = parseRoom(roomObj);
      //  room 放进 Map<Integer, Room> 类型的 worldMap 中，key 是房间号！
      worldMap.put(room.getRoomNumber(), room);
    }
    // worldMap done

    //TODO 1
    // 3.解析 items（用于与房间匹配）
    Map<String, Item> globalItems = new HashMap<>();
    if (root.has("items")) {
      JsonArray itemsArray = root.getAsJsonArray("items");
      for (JsonElement element : itemsArray) {
        JsonObject i = element.getAsJsonObject();
        String name = i.get("name").getAsString();
        double weight = i.get("weight").getAsDouble();
        int maxUses = i.get("max_uses").getAsInt();
        int usesRemaining = i.get("uses_remaining").getAsInt();
        int value = i.get("value").getAsInt();
        String whenUsed = i.get("when_used").getAsString();
        globalItems.put(name, new Item(name, weight, maxUses, usesRemaining, value, whenUsed));
      }
    }
    // todo 1
    // 4.解析 fixtures（用于装入房间）
    Map<String, Fixture> globalFixtures = new HashMap<>();
    if (root.has("fixtures")) {
      JsonArray fixturesArray = root.getAsJsonArray("fixtures");
      for (JsonElement element : fixturesArray) {
        JsonObject f = element.getAsJsonObject();
        String name = f.get("name").getAsString();
        String desc = f.get("description").getAsString();
        int weight = f.get("weight").getAsInt();
        globalFixtures.put(name, new Fixture(name, desc, weight));
      }
    }
    // todo 2
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

    // TODO: 2 解析怪物 monster（方式类似 Puzzle）

    // todo ht
    // 第二轮：给房间塞入 items 和 fixtures
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

    reader.close();
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

  // todo 4
  /**
   * Parse a single Room object from a JSON object.
   * { "room_name":"Courtyard", "room_number": "1",
   *  "description":"A beautiful courtyard with flowers on both sides of the stone walkway. \nThe walkway leads north. A billboard is in the distance.",
   * "N": "2", "S": "0", "E": "0", "W": "0","puzzle": null, "monster": null, "items": "Hair Clippers", "fixtures": "Billboard","picture": "courtyard.png" },
   * }
   */
  // 👇 解析单个 Room 对象 提取基本字段（名字/编号/出口）
  private Room parseRoom(JsonObject obj) {
    // Get the room number from JSON file.
    int num = obj.get("room_number").getAsInt();
    // Get the room name from JSON file.
    String name = obj.get("room_name").getAsString();
    // Create a Room object using room number and name.
    Room r = new Room(num, name);

    // Set the room description if it exists
    if (obj.has("description") && !obj.get("description").isJsonNull()) {
      r.setDescription(obj.get("description").getAsString());
    }

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
}
