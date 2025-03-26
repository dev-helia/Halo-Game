package model.obstacle;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Puzzle class, a subclass of GameObstacle.
 */
public class PuzzleTest {

  private Puzzle puzzle;

  /**
   * Sets up a sample puzzle before each test.
   */
  @BeforeEach
  public void setUp() {
    puzzle = new Puzzle(
            "Ancient Riddle",
            "The stones no longer glow.",
            true,
            150,
            "'light'",
            true,
            false,
            "Glowing stones block the path.",
            7,
            "What shines but burns not?"
    );
  }

  /**
   * Tests basic inherited GameObstacle properties.
   */
  @Test
  public void testBasicProperties() {
    assertEquals("Ancient Riddle", puzzle.getName());
    assertEquals("The stones no longer glow.", puzzle.getDescription());
    assertTrue(puzzle.isActive());
    assertEquals(150, puzzle.getValue());
  }

  /**
   * Tests whether the puzzle is solved by a correct text-based answer.
   */
  @Test
  public void testIsSolvedCorrectAnswerWithQuotes() {
    assertTrue(puzzle.isSolved("light"));
  }

  /**
   * Tests whether the puzzle fails with an incorrect answer.
   */
  @Test
  public void testIsSolvedIncorrectAnswer() {
    assertFalse(puzzle.isSolved("dark"));
  }

  /**
   * Tests that solving is case-insensitive and whitespace-trimmed.
   */
  @Test
  public void testIsSolvedTrimsAndIgnoresCase() {
    assertTrue(puzzle.isSolved(" LIGHT "));
  }

  /**
   * Tests puzzle with a plain string solution (no quotes).
   */
  @Test
  public void testIsSolvedPlainString() {
    Puzzle p = new Puzzle(
            "Lever Puzzle",
            "The levers are jammed.",
            true,
            100,
            "pull",
            false,
            true,
            "You can't proceed without solving this.",
            3,
            "Sometimes pulling is better than pushing."
    );
    assertTrue(p.isSolved("pull"));
  }

  /**
   * Tests the behavior when the solution is null.
   */
  @Test
  public void testIsSolvedNullSolution() {
    Puzzle p = new Puzzle(
            "Empty Puzzle",
            "An empty void.",
            true,
            0,
            null,
            false,
            false,
            "Nothing blocks the way.",
            0,
            "There's nothing here."
    );
    assertFalse(p.isSolved("anything"));
  }

  /**
   * Tests the getEffects method.
   */
  @Test
  public void testGetEffects() {
    assertEquals("Glowing stones block the path.", puzzle.getEffects());
  }

  /**
   * Tests affectsTarget() returns the correct value.
   */
  @Test
  public void testAffectsTarget() {
    assertTrue(puzzle.affectsTarget());
  }

  /**
   * Tests affectsPlayer() returns the correct value.
   */
  @Test
  public void testAffectsPlayer() {
    assertFalse(puzzle.affectsPlayer());
  }

  /**
   * Tests the target room number is returned correctly.
   */
  @Test
  public void testGetTargetRoomNumber() {
    assertEquals(7, puzzle.getTargetRoomNumber());
  }

  /**
   * Tests the hint message is returned correctly.
   */
  @Test
  public void testGetHintMessage() {
    assertEquals("What shines but burns not?", puzzle.getHintMessage());
  }

  /**
   * Tests that getCurrentDescription returns the effect when active.
   */
  @Test
  public void testGetCurrentDescriptionWhenActive() {
    assertEquals("Glowing stones block the path.", puzzle.getCurrentDescription());
  }

  /**
   * Tests that getCurrentDescription returns the base description when inactive.
   */
  @Test
  public void testGetCurrentDescriptionWhenInactive() {
    puzzle.deactivate();
    assertEquals("The stones no longer glow.", puzzle.getCurrentDescription());
  }
}
