package model.elements;

/**
 * Abstract class that serves as the base for all game elements.
 */
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

  /**
   * Abstract method that forces subclasses to implement a detailed display method.
   */
  public abstract void displayDetails();
}
