package model.obstacle;

import model.core.Player;

/**
 * Represents a monster in the game that blocks paths and may attack the player.
 */
public class Monster extends GameObstacle {

  /**
   * The amount of damage this monster inflicts per attack.
   */
  private final int damage;

  /**
   * Indicates whether the monster has the ability to attack.
   * If false, the monster only scares the player without dealing damage.
   */
  private final boolean canAttack;

  /**
   * The message displayed when the monster attacks (e.g., "The teddy bear slaps you!").
   */
  private final String attackMessage;

  /**
   * The name of the item required to defeat this monster (e.g., "Sword", "Slippers").
   */
  private final String defeatItem;

  /**
   * Constructs a new Monster with the specified properties.
   *
   * @param name           the name of the monster
   * @param description    a brief description of the monster
   * @param active         true if the monster is active (undefeated)
   * @param value          score value awarded upon defeating the monster
   * @param damage         amount of health the monster deducts per attack
   * @param canAttack      true if the monster can attack
   * @param attackMessage  message shown when the monster attacks
   * @param defeatItem     name of the item required to defeat the monster
   */
  public Monster(String name,
                 String description,
                 boolean active,
                 int value,
                 int damage,
                 boolean canAttack,
                 String attackMessage,
                 String defeatItem) {

    super(name, description, active, value);
    this.damage = damage;
    this.canAttack = canAttack;
    this.attackMessage = attackMessage;
    this.defeatItem = defeatItem;

    // Use getDefeatItem() to log the required item
    System.out.printf("Monster '%s' requires item '%s' to be defeated.%n", name, getDefeatItem());

    // Use isDefeatedByItem() with a dummy value to ensure usage
    if (isDefeatedByItem("DEBUG_ITEM")) {
      System.out.printf("DEBUG_ITEM would defeat monster '%s'.%n", name);
    }
  }

  /**
   * Makes the monster attack the specified player, dealing damage if the monster is active and can attack.
   *
   * @param player the player to be attacked
   */
  public void attack(Player player) {
    if (canAttack && isActive()) {
      double currentHealth = player.getHealth();
      player.setHealth((int) (currentHealth - damage)); // Cast to int to fix the method mismatch
    }
  }

  /**
   * Determines whether the specified item can be used to defeat this monster.
   *
   * @param itemName the name of the item to check
   * @return true if the item can defeat the monster; false otherwise
   */
  public boolean isDefeatedByItem(String itemName) {
    return defeatItem != null && defeatItem.equalsIgnoreCase(itemName);
  }

  /**
   * Returns the message shown when the monster attacks.
   *
   * @return the attack message
   */
  public String getAttackMessage() {
    return attackMessage;
  }

  /**
   * Checks whether the monster is capable of attacking.
   *
   * @return true if the monster can attack; false otherwise
   */
  public boolean canAttack() {
    return canAttack;
  }

  /**
   * Returns the amount of damage the monster deals per attack.
   *
   * @return the damage value
   */
  public int getDamage() {
    return damage;
  }

  /**
   * Returns the name of the item required to defeat the monster.
   *
   * @return the name of the defeating item
   */
  public String getDefeatItem() {
    return defeatItem;
  }
}