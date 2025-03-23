package model.obstacle;

/**
 * 表示一个谜题类，是游戏中阻挡玩家的障碍之一
 * 玩家可以通过“物品”或“输入文字答案”来解决它
 */
public class Puzzle extends GameObstacle {
  // ✅ 解谜的方式：可能是道具名称，也可能是文字答案
  private String solution;

  // ✅ 是否影响目标（一般是房间通路）
  private boolean affectsTarget;

  // ✅ 是否影响玩家（当前版本中不需要实现）
  private boolean affectsPlayer;

  // ✅ 当前谜题的“效果”描述，会覆盖房间原本的文本
  private String effects;

  // ✅ 被这个谜题影响的房间编号（用于后续可能的状态更新）
  private int targetRoomNumber;

  /**
   * 构造函数
   */
  public Puzzle(String name,
                String description,
                boolean active,
                int value,
                String solution,
                boolean affectsTarget,
                boolean affectsPlayer,
                String effects,
                int targetRoomNumber) {

    super(name, description, active, value); // 调用 GameObstacle 的构造器

    this.solution = solution;
    this.affectsTarget = affectsTarget;
    this.affectsPlayer = affectsPlayer;
    this.effects = effects;
    this.targetRoomNumber = targetRoomNumber;
  }

  /**
   * 判断谜题是否被正确解答（答案可以是物品名或文字）
   */
  public boolean isSolved(String answer) {
    if (solution == null) return false;

    // 如果解谜方式是文字（以 ' 引号包围）
    if (solution.startsWith("'") && solution.endsWith("'")) {
      String trimmed = solution.substring(1, solution.length() - 1);
      return trimmed.equalsIgnoreCase(answer.trim());
    } else {
      // 否则视为物品名匹配
      return solution.equalsIgnoreCase(answer.trim());
    }
  }

  /**
   * 返回谜题的效果文本（比如力场挡住你）
   */
  public String getEffects() {
    return effects;
  }

  public boolean affectsTarget() {
    return affectsTarget;
  }

  public boolean affectsPlayer() {
    return affectsPlayer;
  }

  public int getTargetRoomNumber() {
    return targetRoomNumber;
  }
}
