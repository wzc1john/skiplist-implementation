import java.util.Random;

public class CurrentSkipList {
    private static final int MAX_LEVEL = 32;
    private static final double P = 0.25;
    private final Node head;
    private int currentLevel;
    private final Random random;

    private static class Node {
        int val;
        Node[] next;

        public Node(int val, int level) {
            this.val = val;
            this.next = new Node[level];
        }
    }

    public CurrentSkipList() {
        this.head = new Node(-1, MAX_LEVEL);
        this.currentLevel = 1;
        this.random = new Random();
    }

    private int randomLevel() {
        int level = 1;
        while (random.nextDouble() < P && level < MAX_LEVEL) {
            level++;
        }
        return level;
    }

    public boolean contains(int num) {
        Node current = head;
        // 从最高层开始查找
        for (int i = currentLevel - 1; i >= 0; i--) {
            // 找到第i层小于目标值的最大节点
            while (current.next[i] != null && current.next[i].val < num) {
                current = current.next[i];
            }
        }
        // 检查最底层的下一个节点是否为目标值
        current = current.next[0];
        return current != null && current.val == num;
    }

    public void add(int num) {
        Node[] update = new Node[MAX_LEVEL];
        Node current = head;

        // 从最高层开始查找插入位置
        for (int i = currentLevel - 1; i >= 0; i--) {
            while (current.next[i] != null && current.next[i].val < num) {
                current = current.next[i];
            }
            update[i] = current;
        }

        // 随机生成新节点的层数
        int newLevel = randomLevel();
        if (newLevel > currentLevel) {
            for (int i = currentLevel; i < newLevel; i++) {
                update[i] = head;
            }
            currentLevel = newLevel;
        }

        // 创建新节点
        Node newNode = new Node(num, newLevel);

        // 更新每一层的指针
        for (int i = 0; i < newLevel; i++) {
            newNode.next[i] = update[i].next[i];
            update[i].next[i] = newNode;
        }
    }

    public void remove(int num) {
        Node[] update = new Node[MAX_LEVEL];
        Node current = head;

        // 从最高层开始查找要删除的节点
        for (int i = currentLevel - 1; i >= 0; i--) {
            while (current.next[i] != null && current.next[i].val < num) {
                current = current.next[i];
            }
            update[i] = current;
        }

        current = current.next[0];

        // 如果找到了要删除的节点
        if (current != null && current.val == num) {
            // 从最底层开始，逐层删除节点
            for (int i = 0; i < currentLevel; i++) {
                if (update[i].next[i] != current) {
                    break;
                }
                update[i].next[i] = current.next[i];
            }

            // 更新当前最大层数
            while (currentLevel > 1 && head.next[currentLevel - 1] == null) {
                currentLevel--;
            }
        }
    }

    // 用于调试的打印方法
    public void printSkipList() {
        for (int i = currentLevel - 1; i >= 0; i--) {
            Node current = head.next[i];
            System.out.print("Level " + i + ": ");
            while (current != null) {
                System.out.print(current.val + " -> ");
                current = current.next[i];
            }
            System.out.println("null");
        }
    }
} 