package model.obstacle;

import model.core.Player;
import model.core.Room;
import model.core.HealthStatus;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link Monster} class, which extends {@link GameObstacle}.
 * <p>
 * These tests validate Monster-specific behavior (damage, attack, defeat item),
 * as well as inherited properties from {@link GameObstacle}, including value,
 * name, description, and active state.
 * </p >
 */
public class MonsterTest {

  private Monster monster;
  private TestPlayer player;

  /**
   * Minimal implementation of a testable Player with overrideable health.
   */
  private static class TestPlayer extends Player {
    /**
     * Instantiates a new Test player.
     *
     * @param name         the name
     * @param startingRoom the starting room
     * @param health       the health
     */
    public TestPlayer(String name, Room startingRoom, int health) {
      super(name, startingRoom);
      super.setHealth(health);
    }

    @Override
    public void setHealth(int health) {
      super.setHealth(health);
    }

    @Override
    public double getHealth() {
      return super.getHealth();
    }
  }

  /**
   * Sets up a test monster and player in a dummy room.
   */
  @BeforeEach
  public void setUp() {
    Room dummyRoom = new Room(1, "Test Room", "A dummy room.");
    monster = new Monster(
            "Teddy Bear",
            "A creepy teddy bear with glowing eyes.",
            true,
            200,
            25,
            true,
            "The teddy bear slaps you!",
            "Slippers", "The bear blocks your way with a menacing grin."
    );
    player = new TestPlayer("TestPlayer", dummyRoom, 100);
  }

  /**
   * Test basic properties.
   */
  @Test
  public void testBasicProperties() {
    assertEquals("Teddy Bear", monster.getName());
    assertEquals("A creepy teddy bear with glowing eyes.", monster.getDescription());
    assertTrue(monster.isActive());
    assertEquals(200, monster.getValue());
  }

  /**
   * Test get attack message.
   */
  @Test
  public void testGetAttackMessage() {
    assertEquals("The teddy bear slaps you!", monster.getAttackMessage());
  }

  /**
   * Test can attack.
   */
  @Test
  public void testCanAttack() {
    assertTrue(monster.canAttack());
  }

  /**
   * Test get damage.
   */
  @Test
  public void testGetDamage() {
    assertEquals(25, monster.getDamage());
  }

  /**
   * Test get defeat item.
   */
  @Test
  public void testGetDefeatItem() {
    assertEquals("Slippers", monster.getDefeatItem());
  }

  /**
   * Test is defeated by correct item.
   */
  @Test
  public void testIsDefeatedByCorrectItem() {
    assertTrue(monster.isDefeatedByItem("Slippers"));
  }

  /**
   * Test is defeated by wrong item.
   */
  @Test
  public void testIsDefeatedByWrongItem() {
    assertFalse(monster.isDefeatedByItem("Sword"));
  }

  /**
   * Test attack when active and can attack.
   */
  @Test
  public void testAttackWhenActiveAndCanAttack() {
    monster.attack(player);
    assertEquals(75, player.getHealth());
  }

  /**
   * Test attack when inactive.
   */
  @Test
  public void testAttackWhenInactive() {
    monster.deactivate();
    monster.attack(player);
    assertEquals(100, player.getHealth());
  }

  /**
   * Test attack when cannot attack.
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
            "Candle", "Just floats in place and stares at you silently."
    );
    passiveMonster.attack(player);
    assertEquals(100, player.getHealth());
  }

  /**
   * Test player health status transitions.
   */
  @Test
  public void testPlayerHealthStatusTransitions() {
    player.setHealth(100);
    assertEquals(HealthStatus.AWAKE, player.getHealthStatus());

    player.setHealth(69);
    assertEquals(HealthStatus.FATIGUED, player.getHealthStatus());

    player.setHealth(39);
    assertEquals(HealthStatus.WOOZY, player.getHealthStatus());

    player.setHealth(0);
    assertEquals(HealthStatus.SLEEP, player.getHealthStatus());
  }

  /**
   * Test attack drops to woozy.
   */
  @Test
  public void testAttackDropsToWoozy() {
    player.setHealth(45);
    monster.attack(player); // -25 → health = 20
    assertEquals(20, player.getHealth());
    assertEquals(HealthStatus.WOOZY, player.getHealthStatus());
  }

  /**
   * Test attack to sleep status.
   */
  @Test
  public void testAttackToSleepStatus() {
    player.setHealth(10);
    monster.attack(player); // -25 → health = 0
    assertEquals(0, player.getHealth());
    assertEquals(HealthStatus.SLEEP, player.getHealthStatus());
    assertFalse(player.isAlive());
  }
}
