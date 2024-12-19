import java.util.Map;
import java.util.Objects;

public class CustomHashMap<K, V> {
    // 默认初始容量 - 必须是 2 的幂
    private static final int DEFAULT_INITIAL_CAPACITY = 16;
    
    // 最大容量
    private static final int MAXIMUM_CAPACITY = 1 << 30;
    
    // 默认负载因子
    private static final float DEFAULT_LOAD_FACTOR = 0.75f;
    
    // 链表转红黑树的阈值
    private static final int TREEIFY_THRESHOLD = 8;
    
    // 红黑树转链表的阈值
    private static final int UNTREEIFY_THRESHOLD = 6;
    
    // 树化时的最小容量
    private static final int MIN_TREEIFY_CAPACITY = 64;
    
    // 实际存储的键值对数量
    private int size;
    
    // 扩容阈值
    private int threshold;
    
    // 负载因子
    private final float loadFactor;
    
    // 存储数组
    private Node<K,V>[] table;
    
    // 修改次数，用于快速失败
    private int modCount;
    
    // 基本节点结构
    static class Node<K,V> {
        final int hash;
        final K key;
        V value;
        Node<K,V> next;
        
        Node(int hash, K key, V value, Node<K,V> next) {
            this.hash = hash;
            this.key = key;
            this.value = value;
            this.next = next;
        }
        
        public final K getKey() { return key; }
        public final V getValue() { return value; }
        public final String toString() { return key + "=" + value; }
        
        public final int hashCode() {
            return Objects.hashCode(key) ^ Objects.hashCode(value);
        }
        
        public final V setValue(V newValue) {
            V oldValue = value;
            value = newValue;
            return oldValue;
        }
        
        public final boolean equals(Object o) {
            if (o == this) return true;
            if (o instanceof Map.Entry) {
                Map.Entry<?,?> e = (Map.Entry<?,?>)o;
                return Objects.equals(key, e.getKey()) &&
                       Objects.equals(value, e.getValue());
            }
            return false;
        }
    }
    
    public CustomHashMap() {
        this.loadFactor = DEFAULT_LOAD_FACTOR;
    }
    
    public CustomHashMap(int initialCapacity) {
        this(initialCapacity, DEFAULT_LOAD_FACTOR);
    }
    
    public CustomHashMap(int initialCapacity, float loadFactor) {
        if (initialCapacity < 0)
            throw new IllegalArgumentException("Illegal initial capacity: " + initialCapacity);
        if (initialCapacity > MAXIMUM_CAPACITY)
            initialCapacity = MAXIMUM_CAPACITY;
        if (loadFactor <= 0 || Float.isNaN(loadFactor))
            throw new IllegalArgumentException("Illegal load factor: " + loadFactor);
            
        this.loadFactor = loadFactor;
        this.threshold = tableSizeFor(initialCapacity);
    }
    
    // 计算大于等于 cap 的最小 2 的幂
    static final int tableSizeFor(int cap) {
        int n = -1 >>> Integer.numberOfLeadingZeros(cap - 1);
        return (n < 0) ? 1 : (n >= MAXIMUM_CAPACITY) ? MAXIMUM_CAPACITY : n + 1;
    }
    
    // 计算 key 的哈希值
    static final int hash(Object key) {
        if (key == null) return 0;
        int h = key.hashCode();
        return h ^ (h >>> 16);  // 高位参与运算，减少碰撞
    }
    
    public V put(K key, V value) {
        return putVal(hash(key), key, value, false);
    }
    
    private V putVal(int hash, K key, V value, boolean onlyIfAbsent) {
        Node<K,V>[] tab; Node<K,V> p; int n, i;
        
        // 如果表为空或长度为0，进行初始化
        if ((tab = table) == null || (n = tab.length) == 0) {
            n = (tab = resize()).length;
        }
        
        // 计算索引并获取该位置的节点
        i = (n - 1) & hash;
        p = tab[i];
        
        // 如果该位置为空，直接放入
        if (p == null) {
            tab[i] = new Node<>(hash, key, value, null);
        } else {
            Node<K,V> e; K k;
            // 如果key相同，准备覆盖
            if (p.hash == hash && ((k = p.key) == key || (key != null && key.equals(k))))
                e = p;
            else {
                // 遍历链表
                for (int binCount = 0; ; ++binCount) {
                    if ((e = p.next) == null) {
                        p.next = new Node<>(hash, key, value, null);
                        // 这里可以扩展红黑树转换逻辑
                        break;
                    }
                    if (e.hash == hash && ((k = e.key) == key || (key != null && key.equals(k))))
                        break;
                    p = e;
                }
            }
            
            // 存在相同的key，更新值
            if (e != null) {
                V oldValue = e.value;
                if (!onlyIfAbsent || oldValue == null)
                    e.value = value;
                return oldValue;
            }
        }
        
        // 修改次数增加
        ++modCount;
        // 如果大小超过阈值，扩容
        if (++size > threshold)
            resize();
        return null;
    }
    
    public V get(Object key) {
        Node<K,V> e = getNode(hash(key), key);
        return e == null ? null : e.value;
    }
    
    private Node<K,V> getNode(int hash, Object key) {
        Node<K,V>[] tab; Node<K,V> first, e; int n; K k;
        
        if ((tab = table) != null && (n = tab.length) > 0 &&
            (first = tab[(n - 1) & hash]) != null) {
            
            // 检查第一个节点
            if (first.hash == hash && ((k = first.key) == key || (key != null && key.equals(k))))
                return first;
                
            if ((e = first.next) != null) {
                // 遍历链表
                do {
                    if (e.hash == hash && ((k = e.key) == key || (key != null && key.equals(k))))
                        return e;
                } while ((e = e.next) != null);
            }
        }
        return null;
    }
    
    @SuppressWarnings({"unchecked"})
    private Node<K,V>[] resize() {
        Node<K,V>[] oldTab = table;
        int oldCap = (oldTab == null) ? 0 : oldTab.length;
        int oldThr = threshold;
        int newCap, newThr = 0;
        
        if (oldCap > 0) {
            // 超过最大容量就不再扩容
            if (oldCap >= MAXIMUM_CAPACITY) {
                threshold = Integer.MAX_VALUE;
                return oldTab;
            }
            // 容量翻倍
            else if ((newCap = oldCap << 1) < MAXIMUM_CAPACITY && oldCap >= DEFAULT_INITIAL_CAPACITY)
                newThr = oldThr << 1;
        }
        else if (oldThr > 0)
            newCap = oldThr;
        else {
            // 使用默认值
            newCap = DEFAULT_INITIAL_CAPACITY;
            newThr = (int)(DEFAULT_LOAD_FACTOR * DEFAULT_INITIAL_CAPACITY);
        }
        
        if (newThr == 0) {
            float ft = (float)newCap * loadFactor;
            newThr = (newCap < MAXIMUM_CAPACITY && ft < (float)MAXIMUM_CAPACITY ?
                     (int)ft : Integer.MAX_VALUE);
        }
        
        threshold = newThr;
        @SuppressWarnings({"rawtypes","unchecked"})
        Node<K,V>[] newTab = (Node<K,V>[])new Node[newCap];
        table = newTab;
        
        // 移动现有元素到新表
        if (oldTab != null) {
            for (int j = 0; j < oldCap; ++j) {
                Node<K,V> e;
                if ((e = oldTab[j]) != null) {
                    oldTab[j] = null;
                    if (e.next == null)
                        newTab[e.hash & (newCap - 1)] = e;
                    else {
                        // 链表重新分布
                        Node<K,V> loHead = null, loTail = null;
                        Node<K,V> hiHead = null, hiTail = null;
                        Node<K,V> next;
                        do {
                            next = e.next;
                            if ((e.hash & oldCap) == 0) {
                                if (loTail == null)
                                    loHead = e;
                                else
                                    loTail.next = e;
                                loTail = e;
                            }
                            else {
                                if (hiTail == null)
                                    hiHead = e;
                                else
                                    hiTail.next = e;
                                hiTail = e;
                            }
                        } while ((e = next) != null);
                        
                        if (loTail != null) {
                            loTail.next = null;
                            newTab[j] = loHead;
                        }
                        if (hiTail != null) {
                            hiTail.next = null;
                            newTab[j + oldCap] = hiHead;
                        }
                    }
                }
            }
        }
        return newTab;
    }
    
    public int size() {
        return size;
    }
    
    public boolean isEmpty() {
        return size == 0;
    }
    
    public void clear() {
        Node<K,V>[] tab;
        modCount++;
        if ((tab = table) != null && size > 0) {
            size = 0;
            for (int i = 0; i < tab.length; ++i)
                tab[i] = null;
        }
    }
    
    public boolean containsKey(Object key) {
        return getNode(hash(key), key) != null;
    }
} 