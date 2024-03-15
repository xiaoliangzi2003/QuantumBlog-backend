### 1. ArrayList

ArrayList自动扩容机制是什么样的

#### 1.1 ArrayList整体框架

![[Pasted image 20240310122546.png]]
ArrayList继承于AbstractList，实现了RandomAccess、Serializable、Cloneable、List四个接口

1. ArrayList的数据类型必须是引用数据类型

- 添加元素：add(e)
- 修改元素: set(index,e)
- 删除元素：remove(index)
- 求元素数量：size()

	引用数据类型：Boolean、Byte、Short、Integer、Long、Float、Double、Character

因为ArrayList的底层是数组，所以可以通过索引直接访问元素，并且ArrayList中存储了一个变量用来记录当前元素的数量，所以size(), isEmpty(), get(), set()方法均能在常数时间内完成，add()方法的时间开销跟插入位置有关，addAll()方法的时间开销跟添加元素的个数成正比。其余方法大都是线性时间。


**2. 什么是序列化和反序列化**

序列化是将对象的状态转化为字节流的过程，以便其存储在内存、在网络中传输、以及持久化存储在文件中。

在序列化的过程中，对象的状态会被转化为字节序列，以便其在需要时重新创建相同的对象

反序列化是相反的过程，将先前序列化的字节流转化回对象。如果某个字段不希望被序列化，可以使用`transient`关键字进行标记

#### 1.2 为了追求效率，ArrayList没有实现同步(synchronized)

**1. 什么是同步？**

同步是一种用于控制对共享资源访问的机制，当多个线程同时访问共享资源时，可能会引发数据不一致等问题，而同步机制通过确保任意时刻只有一个线程访问资源，从而避免了这个问题

关键字`synchronized`实现了同步，它可应用于方块或代码块，以确保在同一时刻只有一个线程可以访问被同步的代码区域

**2.为什么ArrayList没有实现同步？**

大多数情况下，ArrayList是在单线程环境下使用的，如果每个操作都被同步，就会增加时间和资源的消耗，对于单线程环境来说，这种开销是不必要的

**3.如何实现多线程并发访问一个ArrayList**

- 使用Collections工具类的synchronizedList方法
```java
List<String> synchronizedList = Collections.synchronizedList(new ArrayList<>());

```

- 使用并发集合类，如`CopyOnWriteArrayList`,`CopyOnWriteArrayList`允许在迭代期间修改集合，因为它在对集合进行修改时会创建一个新的副本，而不会影响正在进行的迭代。

- 多线程访问时，可以用`synchronized`关键字或者显式地使用`ReentrantLock`等锁机制来保证对ArrayList的访问是线程安全的。在这种情况下，需要在访问ArrayList的每个关键区域加上同步控制，以确保同一时刻只有一个线程在修改ArrayList。

#### 1.3 ArrayList的三种构造方法

**1.具有初始容量的ArrayList**

```java
  
/**  
 * Constructs an empty list with the specified initial capacity. * * @param  initialCapacity  the initial capacity of the list  
 * @throws IllegalArgumentException if the specified initial capacity  
 *         is negative */
 public ArrayList(int initialCapacity) {  
    if (initialCapacity > 0) {  
        this.elementData = new Object[initialCapacity];  
    } else if (initialCapacity == 0) {  
        this.elementData = EMPTY_ELEMENTDATA;  
    } else {  
        throw new IllegalArgumentException("Illegal Capacity: "+  
                                           initialCapacity);  
    }  
}
```

如果指定的初始容量大于0，则创建一个具有该容量的Object数组作为ArrayList的内部存储空间。如果初始容量为0，则使用预定义的空数组。如果初始容量小于0，则抛出IllegalArgumentException异常。

**2.创建默认容量的ArrayList**

```java
/**  
 * Constructs an empty list with an initial capacity of ten. */
 public ArrayList() {  
    this.elementData = DEFAULTCAPACITY_EMPTY_ELEMENTDATA;  
}
```

这个构造方法创建一个初始容量为10的空ArrayList。它使用一个预定义的空数组作为内部存储空间。

**3.创建包含指定元素的ArrayList**

```java
/*
Constructs a list containing the elements of the specified collection, in the order they are returned by the collection's iterator.
Params:
c – the collection whose elements are to be placed into this list
Throws:
NullPointerException – if the specified collection is null
*/
public ArrayList(Collection<? extends E> c) {  
    Object[] a = c.toArray();  
    if ((size = a.length) != 0) {  
        if (c.getClass() == ArrayList.class) {  
            elementData = a;  
        } else {  
            elementData = Arrays.copyOf(a, size, Object[].class);  
        }  
    } else {  
        // replace with empty array.  
        elementData = EMPTY_ELEMENTDATA;  
    }  
}
```

创建一个包含指定元素的集合，元素按照集合的迭代器顺序进行排列，在这种构造方法中，集合被转化为了一个Object数组，然后按照数组的长度设置ArrayList的大小，如果集合不为空且类型是ArrayList，则直接用该数组作为内部存储空间，否则使用Arrays.copyOf方法将集合转换为一个新的Object数组作为内部存储空间。

#### 1.4 ArrayList自动扩容

每一次向数组中添加元素时，都要去检查添加之后元素的个数是否会超过当前数组的大小，如果超出，则数组会进行自动扩容。

数组通过一个公开的方法`ensureCapacity(int minCapacity)`来实现

数组进行扩容时，会将老数组中的元素重新拷贝一份到新的数组中，每次数组容量的增长大约是其原容量的**1.5**倍。

ArrayList通过ensureCapacity方法检查当前容量是否足够，如果不够则触发自动扩容机制，调用grow方法进行实际的扩容操作


**1.ensureCapacity(int minCapacity)方法**

```java
/**  
 * Increases the capacity of this {@code ArrayList} instance, if  
 * necessary, to ensure that it can hold at least the number of elements * specified by the minimum capacity argument. * * @param minCapacity the desired minimum capacity  
 */
 public void ensureCapacity(int minCapacity) {  
    if (minCapacity > elementData.length  
        && !(elementData == DEFAULTCAPACITY_EMPTY_ELEMENTDATA  
             && minCapacity <= DEFAULT_CAPACITY)) {  
        modCount++;  
        grow(minCapacity);  
    }  
}
```

`minCapacity`是需要对数组进行扩容后的最小容量，如果数组原来的长度小于了对数组进行扩容后的最小容量并且ArrayList不是使用默认容量的空数组，或者说指定的最小容量大于默认容量，则会触发扩容操作

**2.grow(int minCapacity)方法**
```java
/**  
 * Increases the capacity to ensure that it can hold at least the * number of elements specified by the minimum capacity argument. * * @param minCapacity the desired minimum capacity  
 * @throws OutOfMemoryError if minCapacity is less than zero  
 */
 private Object[] grow(int minCapacity) {  
    int oldCapacity = elementData.length;  
    if (oldCapacity > 0 || elementData != DEFAULTCAPACITY_EMPTY_ELEMENTDATA) {  
        int newCapacity = ArraysSupport.newLength(oldCapacity,  
                minCapacity - oldCapacity, /* minimum growth */  
                oldCapacity >> 1           /* preferred growth */);  
        return elementData = Arrays.copyOf(elementData, newCapacity);  
    } else {  
        return elementData = new Object[Math.max(DEFAULT_CAPACITY, minCapacity)];  
    }
```

这个方法用于实际的扩容操作，首先获取当前数组的容量，然后计算新的容量，这个容量通常是就旧容量的1.5倍，但是会根据minCapacity的值进行调整。

如果原来的数组不是空数组，并且长度大于0，根据oldCapacity、minimum growth、preferred growth，计算出扩容后的新容量

之后使用Arrays.copyOf方法创建新数组，将原数组的元素复制到新数组中，最后将这个数组赋值给ArrayList的elementData字段

**3.grow()**
```java
private Object[] grow() {  
    return grow(size + 1);  
}
```
这个方法是一个简化的扩容方法，它会将容量增加到当前元素数量的下一个位置。这个方法通常在添加新元素时被调用，以确保有足够的空间来存储新元素。

#### 1.5 add(), addAll()

**1.boolean add(E e)**
```java
/**  
 * Appends the specified element to the end of this list. * * @param e element to be appended to this list  
 * @return {@code true} (as specified by {@link Collection#add})  
 */
 public boolean add(E e) {  
    modCount++;  
    add(e, elementData, size);  
    return true;  
}

```

这是一个向ArrayList的末尾添加元素的公共接口，modCount记录了结构性修改的次数，然后调用私有的add(e, elementData, size)方法，在末尾添加元素，如果返回true则说明添加成功

**2. private void add(E e, Object [ ] elementData, int s)**

```java
/**  
 * This helper method split out from add(E) to keep method * bytecode size under 35 (the -XX:MaxInlineSize default value), * which helps when add(E) is called in a C1-compiled loop. */
private void add(E e, Object[] elementData, int s) {  
    if (s == elementData.length)  
        elementData = grow();  
    elementData[s] = e;  
    size = s + 1;  
}
```

如果数组已经满了，则调用grow进行扩容，之后让下标为s的元素=e并更新size变量

**3. public void add(int index, E element)**
```java
/**  
 * Inserts the specified element at the specified position in this * list. Shifts the element currently at that position (if any) and * any subsequent elements to the right (adds one to their indices). * * @param index index at which the specified element is to be inserted  
 * @param element element to be inserted  
 * @throws IndexOutOfBoundsException {@inheritDoc}  
 */
public void add(int index, E element) {  
    rangeCheckForAdd(index);  
    modCount++;  
    final int s;  
    Object[] elementData;  
    if ((s = size) == (elementData = this.elementData).length)  
        elementData = grow();  
    System.arraycopy(elementData, index,  
                     elementData, index + 1,  
                     s - index);  
    elementData[index] = element;  
    size = s + 1;  
}
```

向ArrayList指定位置插入元素的公共接口，首先检查索引是否越界，如果越界则抛出异常
接着增加 `modCount` 变量，用于记录结构性修改的次数。
然后判断数组的长度是否已经满了，如果是则调用grow方法进行扩容
接着使用arraycopy方法将index之后的元素向右移动一位，为新元素腾出空间，最后将新元素插入到指定位置，更新size变量

**4.public boolean addAll(Collection < ? extends E> c) **

```java
/**  
 * Appends all of the elements in the specified collection to the end of * this list, in the order that they are returned by the * specified collection's Iterator.  The behavior of this operation is * undefined if the specified collection is modified while the operation * is in progress.  (This implies that the behavior of this call is * undefined if the specified collection is this list, and this * list is nonempty.) * * @param c collection containing elements to be added to this list  
 * @return {@code true} if this list changed as a result of the call  
 * @throws NullPointerException if the specified collection is null  
 */
 public boolean addAll(Collection<? extends E> c) {  
    Object[] a = c.toArray();  
    modCount++;  
    int numNew = a.length;  
    if (numNew == 0)  
        return false;  
    Object[] elementData;  
    final int s;  
    if (numNew > (elementData = this.elementData).length - (s = size))  
        elementData = grow(s + numNew);  
    System.arraycopy(a, 0, elementData, s, numNew);  
    size = s + numNew;  
    return true;  
}
```

接收一个集合的参数，将集合转化为一个object数组，结构性修改次数+1，新增的长度就是object数组的长度。如果长度为0，则返回false，添加失败。接着创建一个新的elementData数组，用来存放新的元素，如果需要新增的数组长度比数组的容量与数组元素的长度还要大了（数组剩余的空间不够了），那么就调用grow方法，将容量新增到s+numnew，然后调用arraycopy（）方法将集合c中的元素复制到elementData的末尾，从s开始，最后更新ArrayList的大小

**5.public boolean addAll(int index, Collection< ? extends E> c)**
```java
/**  
 * Inserts all of the elements in the specified collection into this * list, starting at the specified position.  Shifts the element * currently at that position (if any) and any subsequent elements to * the right (increases their indices).  The new elements will appear * in the list in the order that they are returned by the * specified collection's iterator. * * @param index index at which to insert the first element from the  
 *              specified collection * @param c collection containing elements to be added to this list  
 * @return {@code true} if this list changed as a result of the call  
 * @throws IndexOutOfBoundsException {@inheritDoc}  
 * @throws NullPointerException if the specified collection is null  
 */
 public boolean addAll(int index, Collection<? extends E> c) {  
    rangeCheckForAdd(index);  
  
    Object[] a = c.toArray();  
    modCount++;  
    int numNew = a.length;  
    if (numNew == 0)  
        return false;  
    Object[] elementData;  
    final int s;  
    if (numNew > (elementData = this.elementData).length - (s = size))  
        elementData = grow(s + numNew);  
  
    int numMoved = s - index;  
    if (numMoved > 0)  
        System.arraycopy(elementData, index,  
                         elementData, index + numNew,  
                         numMoved);  
    System.arraycopy(a, 0, elementData, index, numNew);  
    size = s + numNew;  
    return true;  
}

```

这个方法是在指定的下标插入集合，首先要判断下标的值，如果超出数组的容量，则抛出异常，接着将集合转为一个Object数组a。接着让需要增加的长度等于数组a的长度。再接着还要判断新增后所需的容量是否超过了数组本身的容量，如果超过了则要进行扩容。再接下来计算需要移动的元素数量，如果需要移动的元素数量大于0，则将index后面所有的元素往后移numMoved个元素，否则就是在数组末尾进行插入，不需要对元素进行移动。接着更新数组的长度大小，并根据numNew！=0的值返回false还是true

#### 1.6 get()和set（）

方法很简单，因为底层是个数组，所以直接按照索引访问即可，注意get方法得到元素后要进行类型转换

## 1.7 remove()

1. **remove(int index)删除指定位置的元素**

```java
public E remove(int index) {  
    Objects.checkIndex(index, size);  
    final Object[] es = elementData;  
  
    @SuppressWarnings("unchecked") E oldValue = (E) es[index];  
    fastRemove(es, index);  
  
    return oldValue;  
}
```

返回值是删除元素的值

而remove(object o)删除第一个满足值为o的元素，删除点之后的元素要向前移动一个位置

#### 1.8 其他方法
1. trimToSize（） 将底层数组的容量调整为当前列表保存的实际元素大小
2. indexOf(Object o) 获取元素第一次出现的位置
3. lastIndexOf（Object o） 获取元素最后一次出现的index
4. 
