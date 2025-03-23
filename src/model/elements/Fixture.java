package model.elements;

/**
 * 表示房间中的固定装置，比如电脑、雕像等不能移动的物体
 */
public class Fixture {
  // 装置名称，例如 "Computer"
  private String name;

  // 描述信息，examine 时显示的内容
  private String description;

  // 重量，虽然不会被拿走，但可以用作“是否可搬动”的标识（通常设得很大）
  // 装置都是 immovable 的，weight 设大于 200（约定）
  private int weight;

  /**
   * 构造函数
   */
  public Fixture(String name, String description, int weight) {
    this.name = name;
    this.description = description;
    this.weight = weight;
  }

  /**
   * 获取装置的描述信息
   */
  public String getDescription() {
    return description;
  }

  /**
   * 获取名称
   */
  public String getName() {
    return name;
  }

  /**
   * 获取重量
   */
  public int getWeight() {
    return weight;
  }
}
