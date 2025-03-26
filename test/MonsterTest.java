package model.obstacle;

import model.core.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Monster class, a subclass of GameObstacle.
 */
public class MonsterTest {

  private Monster monster;

  private static class TestPlayer extends Player {
    private double health = 100;

    @Override
    public double getHealth() {
      return health;
    }

    @Override
    public void setHealth(int health) {
      this.health = health;
    }
  }

  private TestPlayer player;

  /**
   * Sets up a test monster and a test player before each test.
   */
  @BeforeEach
  public void setUp() {
    monster = new Monster(
            "Teddy Bear",
            "A creepy teddy bear with glowing eyes.",
            true,
            200,
            25,
            true,
            "The teddy bear slaps you!",
            "Slippers"
    );
    player = new TestPlayer();
  }

  /**
   * Tests that the monster returns the correct name and description.
   */
  @Test
  public void testBasicProperties() {
    assertEquals("Teddy Bear", monster.getName());
    assertEquals("A creepy teddy bear with glowing eyes.", monster.getDescription());
    assertTrue(monster.isActive());
    assertEquals(200, monster.getValue());
  }

  /**
   * Tests that the monster attack message is returned correctly.
   */
  @Test
  public void testGetAttackMessage() {
    assertEquals("The teddy bear slaps you!", monster.getAttackMessage());
  }

  /**
   * Tests that the monster's ability to attack is correctly returned.
   */
  @Test
  public void testCanAttack() {
    assertTrue(monster.canAttack());
  }

  /**
   * Tests that the monster's damage value is correctly returned.
   */
  @Test
  public void testGetDamage() {
    assertEquals(25, monster.getDamage());
  }

  /**
   * Tests that the correct defeat item is returned.
   */
  @Test
  public void testGetDefeatItem() {
    assertEquals("Slippers", monster.getDefeatItem());
  }

  /**
   * Tests whether the correct item can defeat the monster.
   */
  @Test
  public void testIsDefeatedByCorrectItem() {
    assertTrue(monster.isDefeatedByItem("Slippers"));
  }

  /**
   * Tests that the wrong item does not defeat the monster.
   */
  @Test
  public void testIsDefeatedByWrongItem() {
    assertFalse(monster.isDefeatedByItem("Sword"));
  }

  /**
   * Tests the attack method to ensure the player takes damage correctly.
   */
  @Test
  public void testAttackWhenActiveAndCanAttack() {
    monster.attack(player);
    assertEquals(75, player.getHealth());
  }

  /**
   * Tests that a monster does not attack if it's inactive.
   */
  @Test
  public void testAttackWhenInactive() {
    monster.deactivate();
    monster.attack(player);
    assertEquals(100, player.getHealth());
  }

  /**
   * Tests that a monster that cannot attack causes no damage.
   */
  @Test
  public void testAttackWhenCannotAttack() {
    Monster passiveMonster = new Monster(
            "Ghost",
            "A spooky but harmless ghost.",
            true,
            150,
            50,
            false,
            "Boo!",
            "Candle"
    );
    passiveMonster.attack(player);
    assertEquals(100, player.getHealth());
  }
}
