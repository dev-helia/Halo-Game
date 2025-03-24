package model.obstacle;

import model.core.Player;

/**
 * 表示游戏中的一个怪物，会阻挡路径并攻击玩家
 */

// TODO 名字唯一
public class Monster extends GameObstacle {
  // ✅ 每次攻击造成的伤害
  private int damage;

  // ✅ 是否具有攻击能力（可选项） false 就只吓人不打人
  private boolean canAttack;

  // ✅ 攻击时的文字描述（例如：“泰迪熊甩你一巴掌！”）
  private String attackMessage;

  // ✅ 打败它需要的物品名称（比如 "Sword", "Slippers"）
  private String defeatItem;

  /**
   * 构造函数
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
  }

  /**
   * 怪物攻击玩家
   */
  public void attack(Player player) {
    if (canAttack && isActive()) {
      // 扣血！
      double currentHealth = player.getHealth();
      player.setHealth(currentHealth - damage);
    }
  }

  /**
   * 玩家是否能用某个物品击败该怪物
   */
  public boolean isDefeatedByItem(String itemName) {
    return defeatItem != null && defeatItem.equalsIgnoreCase(itemName);
  }

  public String getAttackMessage() {
    return attackMessage;
  }

  public boolean canAttack() {
    return canAttack;
  }

  public int getDamage() {
    return damage;
  }

  public String getDefeatItem() {
    return defeatItem;
  }
}
