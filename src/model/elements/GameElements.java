package model.elements;

import java.io.Serializable;

/**
 * Abstract class that serves as the base for all game elements.
 */
public abstract class GameElements implements Serializable {
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

}
