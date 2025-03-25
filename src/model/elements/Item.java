package model.elements;

/**
 * Item 类代表游戏中的物品，玩家可以拾取、使用或丢弃这些物品。
 */
public class Item extends GameElements {
  private double weight; // 重量
  private int maxUses; // 最大使用次数
  private int usesRemaining; // 剩余使用次数
  private int value; // 价值
  private String whenUsed; //使用时的描述

  /**
   * 初始化物品的名称、描述、重量、最大使用次数、剩余使用次数、价值及使用时的描述
   */
  public Item(String name, String description, double weight, int maxUses, int usesRemaining, int value, String whenUsed) {
    super(name, description);  // 调用parents类的构造函数
    this.weight = weight;
    this.maxUses = maxUses;
    this.usesRemaining = usesRemaining;
    this.value = value;
    this.whenUsed = whenUsed;
  }

  /**
   * 使用物品，减少剩余使用次数并显示描述
   */
  public String use() {
    if (usesRemaining > 0) {
      usesRemaining--;
      return whenUsed;  // 返回使用时的描述
    } else {
      return getName() + " has no remaining uses.";  // 没有剩余使用次数时
    }
  }

  /**
   * 判断物品是否仍然可以使用
   */
  public boolean isUsable() {
    return usesRemaining > 0;
  }

  /**
   * 丢弃物品
   */
  public void dropItem() {
    System.out.println("You dropped the " + getName() + ".");
  }

  /**
   * 展示物品的详细信息
   */
  @Override
  public void displayDetails() {
    System.out.println("Item: " + getName());
    System.out.println("Description: " + getDescription());
    System.out.println("Weight: " + weight + "kg");
    System.out.println("Uses remaining: " + usesRemaining);
    System.out.println("Value: " + value);
  }

  public double getWeight() {
    return weight;
  }

  public void setWeight(double weight) {
    this.weight = weight;
  }

  public int getMaxUses() {
    return maxUses;
  }

  public void setMaxUses(int maxUses) {
    this.maxUses = maxUses;
  }

  public int getUsesRemaining() {
    return usesRemaining;
  }

  public void setUsesRemaining(int usesRemaining) {
    this.usesRemaining = usesRemaining;
  }

  public int getValue() {
    return value;
  }

  public void setValue(int value) {
    this.value = value;
  }

  public String getWhenUsed() {
    return whenUsed;
  }

  public void setWhenUsed(String whenUsed) {
    this.whenUsed = whenUsed;
  }
}
