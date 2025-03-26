package model.obstacle;

/**
 * Abstract base class representing obstacles in the game
 * that block the player's progress, such as puzzles or monsters.
 */
public abstract class GameObstacle {

  /**
   * The name of the obstacle, e.g., "Teddy Bear" or "Turnstile".
   */
  protected String name;

  /**
   * A description of the obstacle, used for hints or UI display.
   */
  protected String description;

  /**
   * Indicates whether the obstacle is currently active.
   * true = unsolved/undefeated; false = deactivated/solved.
   */
  protected boolean active;

  /**
   * The score value awarded upon solving or defeating the obstacle.
   */
  protected int value;

  /**
   * Constructor for subclasses to initialize a game obstacle.
   *
   * @param name        the name of the obstacle
   * @param description a description of the obstacle
   * @param active      whether the obstacle is active
   * @param value       score value associated with the obstacle
   */
  public GameObstacle(String name, String description, boolean active, int value) {
    this.name = name;
    this.description = description;
    this.active = active;
    this.value = value;
  }

  /**
   * Checks whether the obstacle is still active.
   *
   * @return true if the obstacle is active; false otherwise
   */
  public boolean isActive() {
    return active;
  }

  /**
   * Deactivates the obstacle (e.g., after solving a puzzle or defeating a monster).
   */
  public void deactivate() {
    active = false;
  }

  /**
   * Returns the score value associated with the obstacle.
   *
   * @return the score value
   */
  public int getValue() {
    return value;
  }

  /**
   * Returns the name of the obstacle.
   *
   * @return the obstacle's name
   */
  public String getName() {
    return name;
  }

  /**
   * Returns the description of the obstacle.
   *
   * @return the obstacle's description
   */
  public String getDescription() {
    return description;
  }
}
