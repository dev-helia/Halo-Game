package model.core;

import java.util.*;

import model.elements.Fixture;
import model.elements.Item;
import model.obstacle.GameObstacle;

/**
 * 表示游戏地图中的一个房间，包含名称、编号、描述、出口、物品、装置、障碍等。
 */
public class Room {
  // 房间编号（唯一标识）
  private int roomNumber;

  // 房间名称，比如 "Hallway 1"
  private String name;

  // 出口信息：方向 -> 房间编号
  private Map<String, Integer> exits;

  // 房间中的物品列表
  private List<Item> items;

  // 房间中的固定装置（Fixture）
  private List<Fixture> fixtures;

  // 该房间的障碍（可能是 Puzzle 或 Monster），可为 null
  private GameObstacle obstacle;

  /**
   * 构造函数，初始化一个房间
   */
  public Room(int roomNumber, String name) {
    this.roomNumber = roomNumber;
    this.name = name;
    this.exits = new HashMap<>();
    this.items = new ArrayList<>();
    this.fixtures = new ArrayList<>();
    this.obstacle = null; // 默认为没有障碍
  }

  // ✅ 添加一个出口方向（方向字符串如 "N", "S", "E", "W"）
  public void setExit(String direction, int targetRoomNumber) {
    exits.put(direction.toUpperCase(), targetRoomNumber);
  }

  // ✅ 获取指定方向的出口房间编号（若无则返回 0）
  public int getExit(String direction) {
    return exits.getOrDefault(direction.toUpperCase(), 0);
  }

  // ✅ 添加物品
  public void addItem(Item item) {
    items.add(item);
  }

  // ✅ 移除指定名称的物品（用于拾取）
  public Item removeItem(String itemName) {
    Iterator<Item> it = items.iterator();
    while (it.hasNext()) {
      Item i = it.next();
      if (i.getName().equalsIgnoreCase(itemName)) {
        it.remove();
        return i;
      }
    }
    return null;
  }

  // ✅ 获取指定名称的物品（但不移除）
  public Item getItem(String itemName) {
    for (Item i : items) {
      if (i.getName().equalsIgnoreCase(itemName)) {
        return i;
      }
    }
    return null;
  }

  // ✅ 添加装置
  public void addFixture(Fixture f) {
    fixtures.add(f);
  }

  // ✅ 获取指定名称的装置
  public Fixture getFixture(String name) {
    for (Fixture f : fixtures) {
      if (f.getName().equalsIgnoreCase(name)) {
        return f;
      }
    }
    return null;
  }

  // ✅ 设置障碍物（Puzzle 或 Monster）
  public void setObstacle(GameObstacle obs) {
    this.obstacle = obs;
  }

  // ✅ 判断是否有障碍
  public boolean hasObstacle() {
    return obstacle != null && obstacle.isActive();
  }

  // ✅ 解除障碍（用于谜题成功解锁或怪兽被击败）
  public void deactivateObstacle() {
    if (obstacle != null) {
      obstacle.deactivate();
    }
  }

  // 💕 一些 getter
  public int getRoomNumber() {
    return roomNumber;
  }

  public String getName() {
    return name;
  }

  public List<Item> getItems() {
    return items;
  }

  public List<Fixture> getFixtures() {
    return fixtures;
  }

  public GameObstacle getObstacle() {
    return obstacle;
  }

  public Map<String, Integer> getExits() {
    return exits;
  }
}
