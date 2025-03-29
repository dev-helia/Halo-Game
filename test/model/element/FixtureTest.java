package model.element;

import model.elements.Fixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * The type Fixture test.
 */
public class FixtureTest {

  private Fixture fixture;

  /**
   * Sets up.
   */
  @BeforeEach
  public void setUp() {
    // Initialize Fixture object with example data
    fixture = new Fixture("Table", "A sturdy wooden table", 25);
  }

  /**
   * Test fixture constructor.
   */
  @Test
  public void testFixtureConstructor() {
    // Test constructor and getter methods
    assertEquals("Table", fixture.getName());
    assertEquals("A sturdy wooden table", fixture.getDescription());
    assertEquals(25, fixture.getWeight());
  }

  /**
   * Test display details.
   */
  @Test
  public void testDisplayDetails() {
    // Test the 'displayDetails' method
    // Since displayDetails outputs to System.out, we can manually inspect or use a mock for System.out
    fixture.displayDetails();
    // For testing purposes, you can verify output using System.setOut to capture System.out
  }
}
