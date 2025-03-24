package model.elements;

// 抽象类，所有游戏元素的基类
public abstract class GameElements {
  protected String name;
  protected String description;

  public GameElements(String name, String description) {
    this.name = name;
    this.description = description;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  // 抽象方法，要求子类实现具体的展示方法
  public abstract void displayDetails();
}
