# CurrentSkipList 实现

这是一个基于 Java 实现的跳表 (Skip List) 数据结构。跳表是一个可以用来快速查找的数据结构，可以看作是多层链表，支持快速的插入、删除和查找操作。

## 特点

- 平均时间复杂度：O(log n)
- 空间复杂度：O(n)
- 支持的操作：插入、删除、查找
- 随机化数据结构

## 主要接口

```java
public void add(int num)      // 添加元素
public void remove(int num)   // 删除元素
public boolean contains(int num) // 查找元素
```

## 使用示例

```java
CurrentSkipList skipList = new CurrentSkipList();
skipList.add(1);
skipList.add(2);
skipList.add(3);
System.out.println(skipList.contains(2)); // true
skipList.remove(2);
System.out.println(skipList.contains(2)); // false
```

## 性能分析

- 查找：平均 O(log n)，最坏 O(n)
- 插入：平均 O(log n)，最坏 O(n)
- 删除：平均 O(log n)，最坏 O(n)

## 实现细节

- 使用多层链表实现
- 每一层都是一个有序链表
- 通过随机函数决定节点的层数
- 使用哨兵节点简化边界情况的处理 