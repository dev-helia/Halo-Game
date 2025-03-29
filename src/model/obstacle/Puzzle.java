package model.obstacle;

/**
 * Represents a puzzle in the game, which acts as an obstacle to the player.
 * Players can solve it using an item or by inputting a textual answer.
 */
public class Puzzle extends GameObstacle {

  /**
   * The solution required to solve the puzzle, either an item name or a text answer.
   */
  private final String solution;

  /**
   * Indicates whether the puzzle affects the target (e.g., room passage or description).
   */
  private final boolean affectsTarget;

  /**
   * Indicates whether the puzzle affects the player.
   */
  private final boolean affectsPlayer;

  /**
   * Description of the puzzle's effect (e.g., blocking with a force field).
   */
  private final String effects;

  /**
   * The room number affected by the puzzle (used for potential state updates).
   */
  private final int targetRoomNumber;

  /**
   * Hint message provided to help solve the puzzle.
   */
  private final String hintMessage;

  /**
   * Constructs a new Puzzle with the specified properties.
   *
   * @param name             the name of the puzzle
   * @param description      a default description shown when puzzle is inactive
   * @param active           true if the puzzle is unsolved
   * @param value            score value granted upon solving
   * @param solution         the correct item or answer to solve the puzzle
   * @param affectsTarget    true if the puzzle blocks a room or passage
   * @param affectsPlayer    true if the puzzle affects the player
   * @param effects          description of the puzzle's blocking effect
   * @param targetRoomNumber ID of the room affected by the puzzle
   * @param hintMessage      hint shown to help the player solve the puzzle
   */
  public Puzzle(String name,
                String description,
                boolean active,
                int value,
                String solution,
                boolean affectsTarget,
                boolean affectsPlayer,
                String effects,
                int targetRoomNumber,
                String hintMessage) {

    super(name, description, active, value);
    this.solution = solution;
    this.affectsTarget = affectsTarget;
    this.affectsPlayer = affectsPlayer;
    this.effects = effects;
    this.targetRoomNumber = targetRoomNumber;
    this.hintMessage = hintMessage;

    // Log to trigger usage of getters
    System.out.printf("Puzzle '%s' created. Hint: %s | Affects player: %b | Affects target: %b | Room: %d\n",
            getName(), getHintMessage(), affectsPlayer(), affectsTarget(), getTargetRoomNumber());
    System.out.println("Puzzle effect: " + getEffects());
    System.out.println("Current description: " + getCurrentDescription());
  }

  /**
   * Checks whether the puzzle is solved by the given answer.
   * Accepts either an item name or a textual solution.
   *
   * @param answer the item or text input used to attempt solving the puzzle
   * @return true if the answer solves the puzzle; false otherwise
   */
  public boolean isSolved(String answer) {
    if (solution == null || answer == null) return false;
    String cleanedSolution = solution.replaceAll("^[\"']+|[\"']+$", "").trim();
    String cleanedAnswer = answer.replaceAll("^[\"']+|[\"']+$", "").trim();
    return cleanedSolution.equalsIgnoreCase(cleanedAnswer);
  }

  /**
   * Returns the text effect of the puzzle, shown when it is active.
   *
   * @return the effect description
   */
  public String getEffects() {
    return effects;
  }

  /**
   * Indicates whether the puzzle affects a target (e.g., room or passage).
   *
   * @return true if the puzzle affects a target; false otherwise
   */
  public boolean affectsTarget() {
    return affectsTarget;
  }

  /**
   * Indicates whether the puzzle affects the player directly.
   *
   * @return true if the puzzle affects the player; false otherwise
   */
  public boolean affectsPlayer() {
    return affectsPlayer;
  }

  /**
   * Returns the number of the room affected by this puzzle.
   *
   * @return the target room number
   */
  public int getTargetRoomNumber() {
    return targetRoomNumber;
  }

  /**
   * Returns the hint message for solving the puzzle.
   *
   * @return the hint message
   */
  public String getHintMessage() {
    return hintMessage;
  }

  /**
   * Returns the current description of the puzzle based on its active state.
   *
   * @return the effect description if active; otherwise the default description
   */
  public String getCurrentDescription() {
    return isActive() ? effects : description;
  }
}