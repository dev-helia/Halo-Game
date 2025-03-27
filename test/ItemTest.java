import model.elements.Item;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ItemTest {

  private Item item;

  @BeforeEach
  public void setUp() {
    // Initialize Item object with example data
    item = new Item("Sword", "A sharp steel sword", 2.5, 10, 5, 100, "When wielded in battle");
  }

  @Test
  public void testItemConstructor() {
    // Test constructor and getter methods
    assertEquals("Sword", item.getName());
    assertEquals("A sharp steel sword", item.getDescription());
    assertEquals(2.5, item.getWeight());
    assertEquals(10, item.getMaxUses());
    assertEquals(6, item.getUsesRemaining());
    assertEquals(100, item.getValue());
    assertEquals("When wielded in battle", item.getWhenUsed());
  }

  @Test
  public void testUseMethod() {
    // Test the 'use' method
    String result = item.use();
    assertEquals("When wielded in battle", result);
    assertEquals(4, item.getUsesRemaining());  // After using, remaining uses should decrease
  }

  @Test
  public void testIsUsable() {
    // Test if item is usable
    assertTrue(item.isUsable());  // Since usesRemaining is 5, item should be usable
  }
}