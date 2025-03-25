package model.elements;

/**
 * Fixture 类代表游戏中不能移动的物体。
 */
public class Fixture extends GameElements {
  private int weight;

  /**
   * 构造函数：初始化设施的名称、描述和重量
   */
  public Fixture(String name, String description, int weight) {
    super(name, description);  // 调用父类的构造函数
    this.weight = weight;
  }

  /**
   * 获取设施的重量
   */
  public int getWeight() {
    return weight;
  }

  /**
   * 展示设施的详细信息
   */
  @Override
  public void displayDetails() {
    System.out.println("Fixture: " + getName());
    System.out.println("Description: " + getDescription());
    System.out.println("Weight: " + getWeight() + "kg");
  }
}
