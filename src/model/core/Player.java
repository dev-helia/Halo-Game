package model.core;

import model.elements.Item;

import java.util.*;

/**
 * 表示玩家角色，包含名字、健康值、分数、物品、当前位置等
 */
public class Player {
  // 玩家名字
  private String name;

  // 当前生命值（范围 0 - 100）
  private double health;

  // 当前积分
  private double score;

  // 背包：拥有的物品
  private List<Item> inventory;

  // 玩家当前所在的房间
  private Room currentRoom;

  // 背包最大负重
  private final double MAX_WEIGHT = 13.0;

  /**
   * 构造函数
   */
  public Player(String name, Room startingRoom) {
    this.name = name;
    this.health = 100.0;
    this.score = 0.0;
    this.inventory = new ArrayList<>();
    this.currentRoom = startingRoom;
  }

  /**
   * 玩家移动到指定方向，如果该方向有出口且没有障碍，就更新房间
   */
  public boolean move(String direction, Map<Integer, Room> roomMap) {
    int nextRoomNumber = currentRoom.getExit(direction);
    if (nextRoomNumber <= 0) return false;

    Room targetRoom = roomMap.get(nextRoomNumber);
    if (targetRoom == null) return false;

    if (targetRoom.hasObstacle()) return false;

    currentRoom = targetRoom;
    return true;
  }

  /**
   * 玩家拾取物品
   */
  public boolean pickItem(String itemName) {
    Item item = currentRoom.removeItem(itemName);
    if (item == null) return false;

    if (getTotalWeight() + item.getWeight() > MAX_WEIGHT) {
      // 放回去
      currentRoom.addItem(item);
      return false;
    }

    inventory.add(item);
    return true;
  }

  /**
   * 玩家丢弃物品
   */
  public boolean dropItem(String itemName) {
    Iterator<Item> it = inventory.iterator();
    while (it.hasNext()) {
      Item i = it.next();
      if (i.getName().equalsIgnoreCase(itemName)) {
        it.remove();
        currentRoom.addItem(i);
        return true;
      }
    }
    return false;
  }

  /**
   * 使用某个物品
   */
  public String useItem(String itemName) {
    for (Item i : inventory) {
      if (i.getName().equalsIgnoreCase(itemName)) {
        if (i.isUsable()) {
          return i.use();
        } else {
          return "You can't use that item anymore.";
        }
      }
    }
    return "Item not found in inventory.";
  }

  /**
   * 用文字解答谜题（由 Puzzle 检查答案）
   */
  public boolean answerCorrect(String answer, Room room) {
    if (room.hasObstacle() && room.getObstacle() instanceof model.obstacle.Puzzle puzzle) {
      if (puzzle.isSolved(answer)) {
        puzzle.deactivate();
        updateScore(puzzle.getValue());
        return true;
      }
    }
    return false;
  }

  /**
   * 计算当前背包总重量
   */
  private double getTotalWeight() {
    return inventory.stream().mapToDouble(Item::getWeight).sum();
  }

  /**
   * 获取背包清单
   */
  public List<Item> getInventory() {
    return inventory;
  }

  /**
   * 更新分数
   */
  public void updateScore(int points) {
    this.score += points;
  }

  // 🧠 Getter & Setter

  public Room getCurrentRoom() {
    return currentRoom;
  }

  public String getName() {
    return name;
  }

  public double getHealth() {
    return health;
  }

  public void setHealth(double health) {
    this.health = Math.max(0, Math.min(health, 100));
  }

  public double getScore() {
    return score;
  }
}
