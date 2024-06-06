[:us:](README.md) [:brazil:](README_ptbr.md) [:jp:](README_jp.md) [:india:](README_india.md)

# SnA*ke

欢迎来到 SnA\*ke，这是一个使用 A\* 算法进行智能路径规划的经典贪吃蛇游戏的 Java Swing 实现。

![Snake](https://i.imgur.com/TcbzVZL.png)

## 特点

- **智能路径规划**: 贪吃蛇使用 A* 算法智能地在游戏板上导航，优化其路径以到达苹果，同时避开陷阱和障碍物。

- **陷阱检测**: 还有一个附加的启发式方法，尝试避免潜在的困境，为那些导致网格子集小于当前蛇身大小的移动设置非常高的权重。

- **智能目标评估**: 贪吃蛇评估苹果周围的可用空间，检查是否有足够的空间提供潜在的逃生路径，或者如果有 3 个或更多的相邻方格被阻塞，也会为这些潜在的移动设置高权重。

- **会话统计**: 跟踪当前游戏的进度以及多场游戏中的平均移动次数和贪吃蛇的大小。


## 安装

要在本地运行 SnA*ke，请按照以下步骤操作：

1. 克隆仓库:

   ```bash
   git clone https://github.com/TardivoJP/SnA-ke.git
   ```

2. 进入项目目录:

   ```bash
   cd SnA-ke
   ```

3. 编译并运行游戏:

   ```bash
   javac SnakeGUI.java
   java SnakeGUI
   ```


---

感谢您查看 SnA*ke！如果您有任何问题或反馈，请随时提出 issue 或联系我们。享受游戏吧！🐍✨