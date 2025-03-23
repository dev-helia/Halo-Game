package model.elements;

/**
 * 表示游戏中的一个可拾取物品
 * 不知道怎么存储, 用HashMap可以标注物品数量, 如果这样的话
 * 需要写hash 和 equalTo
 */
public class Item {
  // 物品名称
  private String name;

  // 物品重量（用于背包负重判断）
  private double weight;

  // 物品最多可以使用的次数（上限）
  private int maxUses;

  // 当前剩余可使用次数
  private int usesRemaining;

  // 物品的价值，用于积分加分
  private int value;

  // 使用物品时输出的文本（反馈）
  private String whenUsed;

  /**
   * 构造函数
   */
  public Item(String name, double weight, int maxUses, int usesRemaining, int value, String whenUsed) {
    this.name = name;
    this.weight = weight;
    this.maxUses = maxUses;
    this.usesRemaining = usesRemaining;
    this.value = value;
    this.whenUsed = whenUsed;
  }

  // 🧠 判断这个物品还能不能用
  public boolean isUsable() {
    return usesRemaining > 0;
  }

  // ✨ 使用物品时调用，减少剩余次数，返回效果描述
  public String use() {
    if (isUsable()) {
      usesRemaining--;
      return whenUsed;
    } else {
      return "The item is empty or cannot be used anymore.";
    }
  }

  // 👜 获取物品重量
  public double getWeight() {
    return weight;
  }

  // 💰 获取物品价值
  public int getValue() {
    return value;
  }

  // 🧾 丢弃物品时显示信息
  public String dropItem() {
    return "You dropped " + name + ".";
  }

  // 📎 getter（你们可能后面需要）
  public String getName() {
    return name;
  }

  public int getUsesRemaining() {
    return usesRemaining;
  }

  public String getWhenUsed() {
    return whenUsed;
  }

  public String getDescription() {
    return String.format("Item: %s, Uses left: %d, Weight: %.2f", name, usesRemaining, weight);
  }

}
