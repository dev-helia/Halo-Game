import model.elements.Fixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class FixtureTest {

  private Fixture fixture;

  @BeforeEach
  public void setUp() {
    // Initialize Fixture object with example data
    fixture = new Fixture("Table", "A sturdy wooden table", 25);
  }

  @Test
  public void testFixtureConstructor() {
    // Test constructor and getter methods
    assertEquals("Table", fixture.getName());
    assertEquals("A sturdy wooden table", fixture.getDescription());
    assertEquals(25, fixture.getWeight());
  }

  @Test
  public void testDisplayDetails() {
    // Test the 'displayDetails' method
    // Since displayDetails outputs to System.out, we can manually inspect or use a mock for System.out
    fixture.displayDetails();
    // For testing purposes, you can verify output using System.setOut to capture System.out
  }
}
