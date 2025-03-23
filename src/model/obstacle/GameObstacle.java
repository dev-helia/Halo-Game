package model.obstacle;

/**
 * 游戏中所有“阻挡玩家前进”的元素的父类，例如谜题 Puzzle、怪物 Monster
 */
public abstract class GameObstacle {
  // 名称，比如 "Teddy Bear" 或 "Turnstile"
  protected String name;

  // 障碍的描述，用于提示或呈现效果信息
  protected String description;

  // 是否处于激活状态（true=未解决/未击败，false=已解除）
  protected boolean active;

  // 解决/击败该障碍后可以获得的分数
  protected int value;

  /**
   * 构造函数（给子类用）
   */
  public GameObstacle(String name, String description, boolean active, int value) {
    this.name = name;
    this.description = description;
    this.active = active;
    this.value = value;
  }

  // ✅ 判断障碍是否还在（active）
  public boolean isActive() {
    return active;
  }

  // ✅ 解除障碍（解谜或击败怪兽时调用）
  public void deactivate() {
    active = false;
  }

  // ✅ 获取障碍值（用于加分）
  public int getValue() {
    return value;
  }

  // ✅ 获取障碍名称（可用于输出）
  public String getName() {
    return name;
  }

  // ✅ 获取描述（用于显示在 UI 上）
  public String getDescription() {
    return description;
  }
}
