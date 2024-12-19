public class CurrentSkipListTest {
    public static void main(String[] args) {
        CurrentSkipList skipList = new CurrentSkipList();
        
        // 测试添加功能
        System.out.println("添加元素测试：");
        skipList.add(1);
        skipList.add(3);
        skipList.add(5);
        skipList.add(7);
        skipList.add(9);
        skipList.printSkipList();
        
        // 测试查找功能
        System.out.println("\n查找元素测试：");
        System.out.println("查找 3: " + skipList.contains(3));  // 应该返回 true
        System.out.println("查找 4: " + skipList.contains(4));  // 应该返回 false
        
        // 测试删除功能
        System.out.println("\n删除元素测试：");
        skipList.remove(3);
        System.out.println("删除 3 后的跳表：");
        skipList.printSkipList();
        System.out.println("查找 3: " + skipList.contains(3));  // 应该返回 false
    }
} 