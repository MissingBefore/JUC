---
本文当用于帮助读者从功能性上理解JUC的功能场景 * 阅读之前请先确定了解集合相关的基础知识 *

---

# 一、Collections工具类

* 该类应用工厂及静态内部类的形式为已有数据结构进行加工，赋予dynamically(checked) 、empty 、immutable(singleton) 、synchronized.所有读写都加对象锁、unmodifiable view(unmodifiable).final修饰对象，写操作throw new UnsupportedOperationException()等特性.*注意*：这里final修饰对象标识的是对象引用，故还需对写操作throw new UnsupportedOperationException().

* Collections对Collection的如下描述进行支持

  * **View Collections**

  * *view collections* themselves do not store elements，Examples of view collections include the wrapper collections returned by methods such as Collections.checkedCollection, Collections.synchronizedCollection, and Collections.unmodifiableCollection.这里的视图概念可以比作引用委派加以理解

  * **Unmodifiable Collections**

  * Certain methods of this interface are considered "destructive" and are called "mutator" methods in that they modify the group of objects contained within the collection on which they operate. 

  * **Unmodifiable View Collections**

  * An unmodifiable view collection is a collection that is unmodifiable and that is also a view onto a backing collection.The effect is to provide read-only access to the backing collection.This is useful for a component to provide users with read access to an internal collection, while preventing them from modifying such collections unexpectedly. Examples of unmodifiable view collections are those returned by the Collections.unmodifiableCollection, Collections.unmodifiableList, and related methods.只读视图，其他操作会抛UnsupportedOperationException异常

  * **Serializability of Collections**

  * A collection implementation that implements the `Serializable` interface cannot be guaranteed to be serializable. The reason is that in general, collections contain elements of other types, and it is not possible to determine statically whether instances of some element type are actually serializable. 

**JUC是对现有数据结构体扩展并发相关的特性，在对JUC学习之前最好已经对集合数组有一个完整的理解。在下面的文字中也将会时刻拿Collections工具类做功能对比，用以帮助同学更加贴切需求的理解到JUC的用处**

# 二、JUC综述

**JUC是对现有数据结构体扩展并发相关的特性，通过精细化锁控制，和对基本数据类型的特性加工用以支持Collections无法满足的应用场景需求，并提供了并发情况下的池化解决方案**

JUC并发编程工具包包括如下三个部分：

| java.util.concurrent        | Utility classes commonly useful in concurrent programming.   |
| :-------------------------- | ------------------------------------------------------------ |
| java.util.concurrent.atomic | A small toolkit of classes that support lock-free thread-safe programming on single variables. |
| java.util.concurrent.locks  | Interfaces and classes providing a framework for locking and waiting for conditions that is distinct from built-in synchronization and monitors. |

**注意**：concurrent下ConcurrentHashMap并不是对HashMap的封装，它们没有父子关系

~~~java
public interface ConcurrentMap<K, V> extends Map<K, V>
public class ConcurrentHashMap<K,V> extends AbstractMap<K,V> implements ConcurrentMap<K,V>, Serializable
public class HashMap<K,V> extends AbstractMap<K,V> implements Map<K,V>, Cloneable, Serializable 
~~~

**我们通过ConcurrentHashMap中putVal()方法的源码讲看看并发的具体实现**

~~~java
 /** Implementation for put and putIfAbsent */
	/**
	1.onlyIfAbsent if true, don't change existing value
	*/
    final V putVal(K key, V value, boolean onlyIfAbsent) {
        if (key == null || value == null) throw new NullPointerException();
         //static final int spread(int h) { return (h ^ (h >>> 16)) & HASH_BITS; }//>>>表示无符号右移,<<<无符号左，^按位异或
        int hash = spread(key.hashCode());
       
        int binCount = 0;
        for (Node<K,V>[] tab = table;;) {//遍历table
            Node<K,V> f; int n, i, fh;
            if (tab == null || (n = tab.length) == 0)
                tab = initTable();
            else if ((f = tabAt(tab, i = (n - 1) & hash)) == null) {
                if (casTabAt(tab, i, null,
                             new Node<K,V>(hash, key, value, null)))
                    break;                   // no lock when adding to empty bin
            }
            else if ((fh = f.hash) == MOVED)
                tab = helpTransfer(tab, f);
            else {
                V oldVal = null;
                synchronized (f) {//是对Node级别的加锁，而非对整个table加锁
                    ......
                }
                ......
            }
            ......
        }
        ......
    }
我们重点看12、15、24行可以知道是先进行节点判别，然后对节点上锁，把锁的粒度缩小到了节点级别而非Collections工具类调用方法时对整个对象上锁
~~~

**通过上边这个例子我们可以知道JUC的功用在于尽量的减小锁的粒度，从而使并发线程可以低耦合的情况下进行业务处理，在这个putVal方法中还有用到较为重要的方法如：spread(key.hashCode())、initTable()、tabAt(tab, i = (n - 1) & hash)、casTabAt(tab, i, null,new Node<K,V>(hash, key, value, null))，我们在后面的模块中会慢慢讲到**

# 三、JUC的组成官文速览

### 1.Executors

* **Interfaces**

| Callable<V>             | A task that returns a result and may throw an exception.     |
| :---------------------- | ------------------------------------------------------------ |
| Executor                | An object that executes submitted Runnable [^tasks]          |
| ExecutorService         | An Executor that provides methods to manage termination and methods that can produce a Future for tracking progress of one or more asynchronous tasks. |
| Future<V>               | A Future represents the result of an asynchronous computation. |
| RunnableFuture<V>       | A Future that is Runnable                                    |
| RunnableScheduledFuture | A ScheduledFuture that is Runnable.                          |
| ScheduledExcutorService | An ExecutorService that can schedule commands to run after a given delay, or to execute [^periodically] |
| ThreadFactory           | An object that creates new threads on demand.                |

* **Implementations**

| ScheduledThreadPoolExecutor | A ThreadPoolExecutor that can additionally schedule commands to run after a given delay, or to execute periodically.为ThreadPoolExecutor添加自定义上下文时间调度 |
| :-------------------------- | ------------------------------------------------------------ |
| ThreadPoolExecutor          | An ExecutorService that executes each submitted task using one of possibly several pooled threads, normally configured using Executors factory methods. |
| Executors                   | Factory and utility methods for Executor, ExecutorService, ScheduledExecutorService, ThreadFactory, and Callable classes defined in this package. |
| ForkJoinPool                | An ExecutorService for running ForkJoinTasks.分治思想，把任务分化到足够小再执行，即较小时间粒度使cpu得到更好的调度.关键词：fork/join框架 |

### 2.Queues

* **Interface**

| BlockingDeque<E> | A Deque that additionally supports blocking operations that wait for the deque to become non-empty when retrieving an element, and wait for space to become available in the deque when storing an element. |
| :--------------- | ------------------------------------------------------------ |
| BlockingQueue<E> | A Queue that additionally supports operations that wait for the queue to become non-empty when retrieving an element, and wait for space to become available in the queue when storing an element. |

* **Implementations**

| Class                         | Description                                                  |
| :---------------------------- | ------------------------------------------------------------ |
| SynchronousQueue<E>           | A blocking queue in which each insert operation must wait for a corresponding remove operation by another thread, and vice versa.put和take是一对一关系，即生产者消费者1：1) |
| ArrayBlockingQueue<E>         | A bounded blocking queue backed by an array.                 |
| LinkedBlockingDeque<E>        | An optionally-bounded blocking deque based on linked nodes.  |
| PriorityBlockingQueue<E>      | An unbounded blocking queue that uses the same ordering rules as class PriorityQueue and supplies blocking retrieval operations. |
| DelayQueue<E extends Delayed> | An unbounded blocking queue of `Delayed` elements, in which an element can only be taken when its delay has expired. |
| LinkedBlockingQueue<E>        | An optionally-bounded blocking queue based on linked nodes.  |
| LinkedTransferQueue<E>        | An unbounded TransferQueue based on linked nodes.            |

### 3.Timing

* The TimeUnit class provides multiple granularities (including nanoseconds) for specifying and controlling time-out based operations. Most classes in the package contain operations based on time-outs in addition to indefinite waits. In all cases that time-outs are used, the time-out specifies the minimum time that the method should wait before indicating that it timed-out. Implementations make a "best effort" to detect time-outs as soon as possible after they occur. However, an indefinite amount of time may elapse between a time-out being detected and a thread actually executing again after that time-out. All methods that accept timeout parameters treat values less than or equal to zero to mean not to wait at all. To wait "forever", you can use a value of `Long.MAX_VALUE`.

| Enum     | Description                                                  |
| :------- | :----------------------------------------------------------- |
| TimeUnit | A `TimeUnit` represents time durations at a given unit of granularity and provides utility methods to convert across units, and to perform timing and delay operations in these units. |

### 4.Synchronizers

Five classes aid common special-purpose synchronization idioms.

| CountDownLatch | A synchronization aid that allows one or more threads to wait until a set of operations being performed in other threads completes. |
| :------------- | ------------------------------------------------------------ |
| CyclicBarrier  | A synchronization aid that allows a set of threads to all wait for each other to reach a common barrier point. |
| Exchanger      | A synchronization point at which threads can pair and swap elements within pairs. |
| Semaphore      | A counting semaphore.                                        |
| Phaser         | A reusable synchronization barrier, similar in functionality to CyclicBarrier and CountDownLatch but supporting more flexible usage. |

### 5.Concurrent Collections.

注意这里和Collections构造的集合类的比较

* **Interface**


| ConcurrentMap<K,V>          | A Map providing thread safety and atomicity guarantees.      |
| :-------------------------- | ------------------------------------------------------------ |
| ConcurrentNavigableMap<K,V> | A ConcurrentMap supporting NavigableMap operations, and recursively so for its navigable sub-maps. |

* **Implementations**


| Class                      | Description                                                  |
| :------------------------- | ------------------------------------------------------------ |
| ConcurrentHashMap<K,V>     | A hash table supporting full concurrency of retrievals and high expected concurrency for updates.a `ConcurrentHashMap` is normally preferable to a synchronized HashMap |
| ConcurrentLinkedDeque<E>   | An unbounded concurrent deque based on linked nodes.         |
| ConcurrentLinkedQueue<E>   | An unbounded thread-safe queue based on linked nodes.        |
| ConcurrentSkipListMap<K,V> | A [^scalable ]concurrent ConcurrentNavigableMap implementation.A ConcurrentSkipListMap` is normally preferable to a synchronized `TreeMap |
| ConcurrentSkipListSet<E>   | A scalable concurrent NavigableSe implementation based on a ConcurrentSkipListMap. |
| CopyOnWriteArrayList<E>    | A thread-safe variant of ArrayList n which all mutative operations (`add`, `set`, and so on) are implemented by making a fresh copy of the underlying array.写时复制数组列表 |
| CopyOnWriteArraySet<E>     | A Set that uses an internal CopyOnWriteArraySet for all of its operations. |

### 6.Memory Consistency Properties.

**内存一致性，即JUC保障JMM内存调度的规则，以下是细则**

* __In particular:__

- Each action in a thread *happens-before* every action in that thread that comes later in the program's order.
- An unlock (`synchronized` block or method exit) of a monitor *happens-before* every subsequent lock (`synchronized` block or method entry) of that same monitor. And because the *happens-before* relation is transitive, all actions of a thread prior to unlocking *happen-before* all actions subsequent to any thread locking that monitor.
- A write to a `volatile` field *happens-before* every subsequent read of that same field. Writes and reads of `volatile` fields have similar memory consistency effects as entering and exiting monitors, but do *not* entail mutual exclusion locking.
- A call to `start` on a thread *happens-before* any action in the started thread.
- All actions in a thread *happen-before* any other thread successfully returns from a `join` on that thread.

**JUC拓展synchronized以实现更高级的应用**

* **In particular:**

- Actions in a thread [^prior ]to placing an object into any concurrent collection *happen-before* actions [^subsequent ]to the access or removal of that element from the collection in another thread.
- Actions in a thread prior to the submission of a `Runnable` to an `Executor` *happen-before* its execution begins. Similarly for `Callables` submitted to an `ExecutorService`.
- Actions taken by the asynchronous computation represented by a `Future` *happen-before* actions subsequent to the retrieval of the result via `Future.get()` in another thread.
- Actions prior to "releasing" synchronizer methods such as `Lock.unlock`, `Semaphore.release`, and `CountDownLatch.countDown` *happen-before* actions subsequent to a successful "acquiring" method such as `Lock.lock`, `Semaphore.acquire`, `Condition.await`, and `CountDownLatch.await` on the same synchronizer object in another thread.
- For each pair of threads that successfully exchange objects via an `Exchanger`, actions prior to the `exchange()` in each thread *happen-before* those subsequent to the corresponding `exchange()` in another thread.
- Actions prior to calling `CyclicBarrier.await` and `Phaser.awaitAdvance` (as well as its variants) *happen-before* actions performed by the barrier action, and actions performed by the barrier action *happen-before* actions subsequent to a successful return from the corresponding `await` in other threads.

# 四、Atomic原子包

atomic包含原子整型和原子引用类型，主要用于对数据的操作进行原子包装

### 1.volatile的局限

**在讲Atomic之前我们需要对volatile的特性做以下探究，这里给出两个用例：**

* **Demo-1**

~~~java
package atomic;

import java.util.concurrent.atomic.AtomicInteger;

class ShareData {
	private /* volatile */ int num = 0;
	public void increment() {
		num++;
	}
	public int getNum() {
		return num;
	}
}

public class Demo1 {
	public static void main(String[] args) {
		ShareData shareData = new ShareData();
		for (int i = 0; i < 20; i++) {
			new Thread(() -> {
				for (int j = 0; j < 100; j++) {
					shareData.increment();
				}
			},"Thread-"+i).start();
		}
		while(Thread.activeCount()>=2) {
			Thread.yield();
		}
		System.out.println("ShareData finally:"+shareData.getNum());
	}
}


~~~

* **Demo-2**

~~~java
package atomic;

import java.util.concurrent.atomic.AtomicInteger;

class ShareData {
	private AtomicInteger ai=new AtomicInteger(0);
	public void incrementAi() {
		ai.getAndIncrement();
	}
	public AtomicInteger getAi() {
		return ai;
	}
}

public class  Demo2 {
	public static void main(String[] args) {
		ShareData shareData = new ShareData();
		for (int i = 0; i < 20; i++) {
			new Thread(() -> {
				for (int j = 0; j < 100; j++) {
					shareData.incrementAi();
					System.out.println(Thread.currentThread().getName()+"\t"+shareData.getAi().get());
				}
			},"Thread-"+i).start();
		}
		while(Thread.activeCount()>=2) {
			Thread.yield();
		}
		System.out.println("ShareData finally:"+shareData.getAi().get());

	}

}
~~~

* **比较两个demo的结果会是demo-1总是小于预期值2000，而demo-2却能达到预期值2000。**

* 我们复习以下volatile的两个，即在并发的情况下保障所修饰数据的可见性和有序性

**可见性**：线程遵守JMM模型，总是在公共空间取volatile修饰的数据，而不用线程本身的栈空间存储操作数

**有序性**：在多数情况下线程总是按照代码的先后顺序执行，但在编译成字节指令和cpu指令时，编译器和指令编辑器都会对指令进行优化从而提高效率，我们通过volatile修饰数据而使相应的指令顺序不得被修改。**这里需要注意的是，volatile并不能保障指令序列不被打断，即原子性不能被确保**

* 那么demo-2中AtomicInteger是如何保障运行达到预期值的呢？

### 2.Atomic原理分析

* **查看AtomicInteger源码**

~~~java
public class AtomicInteger extends Number implements java.io.Serializable {
    private static final long serialVersionUID = 6214790243416807050L;

    // setup to use Unsafe.compareAndSwapInt for updates
    private static final Unsafe unsafe = Unsafe.getUnsafe();
    private static final long valueOffset;

    static {
        try {
            valueOffset = unsafe.objectFieldOffset
                (AtomicInteger.class.getDeclaredField("value"));
        } catch (Exception ex) { throw new Error(ex); }
    }

    private volatile int value;
    ......
   	public final int getAndIncrement() {
        return unsafe.getAndAddInt(this, valueOffset, 1);
    }
    ......
}
~~~

在理论上Java虚拟机规范是规定除虚拟机以外是不提供对象确切的内存空间地址的查询支持的，这里的unsafe类通过objectFieldOffset变相的获得了对象的准确地址(但同时也增添了风险)，unsafe类的实现机制是自旋锁。以下是从open jdk中截取的部分源码

~~~java
    /**
     * Atomically update Java variable to <tt>x</tt> if it is currently
     * holding <tt>expected</tt>.
     * @return <tt>true</tt> if successful
     */
    public final native boolean compareAndSwapInt(Object o, long offset,
                                                  int expected,
                                                  int x);
    /** Volatile version of {@link #getInt(Object, long)}  */
    public native int     getIntVolatile(Object o, long offset);
    /**
     * Atomically adds the given value to the current value of a field
     * or array element within the given object <code>o</code>
     * at the given <code>offset</code>.
     *
     * @param o object/array to update the field/element in
     * @param offset field/element offset
     * @param delta the value to add
     * @return the previous value
     * @since 1.8
     */
    public final int getAndAddInt(Object o, long offset, int delta) {
        int v;
        do {
            v = getIntVolatile(o, offset);
        } while (!compareAndSwapInt(o, offset, v, v + delta));
        return v;
    }
~~~

**注意**：精彩的部分在这里，在getAndAddInt方法中是先取得volatile修饰的公共空间的变量值，然后调用compareAndSwapInt方法进行比较并原子更新，这里为什么要取两次值呢？即getIntVolatile、compareAndSwapInt各取一次值相比较。由于这两个方法都为native修饰的本地方法，我们无从得知，但我们却可以配合JMM模型得出答案。

**每个线程都有自己的工作空间，其中包括程序计数器、虚拟机栈和本地方法栈。线程只会在自己的工作空间进行运算处理，那么我们就可以理解到，线程处理由volatile修饰的变量虽然每次都是从公共空间拿取数据，但其实起到的作用只是每一次都从公共空间copy一份数据到自己的工作空间进行操作而已，所以我们在前面的Demo-1中用volatile修饰num最终的结果还是不能达到预期值，因为多线程会对彼此的运算结果进行覆盖。那么我们怎么防止线程彼此之间的数据覆盖呢？首先我们就可以猜测到synchronized的底层就是JVM直接对指针的加锁操作，没办法做到读写分离但是能保证线程之间的操作不会彼此覆盖。那么Atomic是如何做到线程之间的操作结果不会彼此覆盖呢？答案是：CAS自旋锁，即每一此更新操作都对比拿到的操作数是否与公共内存的操作数相同，不同则自旋再拿一次操作数进行运算，再比较操作数是否与公共内存的操作数相同，相同则执行更新**

自旋锁总结：cpu虽然空转但保障了线程之间的畅通，死锁的概率大大减低

### 3.Atomic成员总结

| Class                            | Description                                                  |
| :------------------------------- | :----------------------------------------------------------- |
| AtomicBoolean                    | A `boolean` value that may be updated atomically.            |
| AtomicInteger                    | An `int` value that may be updated atomically.               |
| AtomicIntegerArray               | An `int` array in which elements may be updated atomically.  |
| AtomicIntegerFieldUpdater<T>     | A reflection-based utility that enables atomic updates to designated `volatile int` fields of designated classes.基于反射的对任意结构体内整型进行原子更新 |
| AtomicLong                       | A `long` value that may be updated atomically.               |
| AtomicLongArray                  | A `long` array in which elements may be updated atomically.  |
| AtomicLongFieldUpdater<T>        | A reflection-based utility that enables atomic updates to designated `volatile long` fields of designated classes. |
| AtomicMarkableReference<V>       | An `AtomicMarkableReference` maintains an object reference along with a mark bit, that can be updated atomically.带有标记的原子引用封装 |
| AtomicReference<V>               | An object reference that may be updated atomically.          |
| AtomicReferenceArray<E>          | An array of object references in which elements may be updated atomically. |
| AtomicReferenceFieldUpdater<T,V> | A reflection-based utility that enables atomic updates to designated `volatile` reference fields of designated classes. |
| AtomicStampedReference<V>        | An `AtomicStampedReference` maintains an object reference along with an integer "stamp", that can be updated atomically. |
| DoubleAccumulator                | One or more variables that together maintain a running `double` value updated using a supplied function. |
| DoubleAdder                      | One or more variables that together maintain an initially zero `double` sum. |
| LongAccumulator                  | One or more variables that together maintain a running `long` value updated using a supplied function. |
| LongAdder                        | One or more variables that together maintain an initially zero `long` sum. |

* 反射原子更新示例

* Demo-3

~~~java
package atomic;

import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

class ShareData01{
	/* private */ volatile  /*Integer*/int num;

	public Integer getNum() {
		return num;
	}

	public void setNum(Integer num) {
		this.num = num;
	}
	
	public ShareData01(Integer num) {
		super();
		this.num = num;
	}	
}
public class AtomicIntegerFieldUpdaterTest {

	public static void main(String[] args) {
		AtomicIntegerFieldUpdater<ShareData01> atomicIntegerFieldUpdater=AtomicIntegerFieldUpdater.newUpdater(ShareData01.class, "num");
		
		ShareData01 shareData01 = new ShareData01(1);
		
		System.out.println(atomicIntegerFieldUpdater.addAndGet(shareData01, 2));
	}

}

~~~

* 带有标记的原子引用
* Demo-4

~~~java
package atomic;

import java.util.concurrent.atomic.AtomicMarkableReference;
class ShareData02{
	volatile int num;

	public ShareData02(int num) {
		super();
		this.num = num;
	}

	@Override
	public String toString() {
		return "ShareData02 [num=" + num + "]";
	}
	
}
public class AtomicMarkableReferenceTest {

	public static void main(String[] args) {
		ShareData02 shareData02 = new ShareData02(1);
		ShareData02 shareData022 = new ShareData02(3);
		AtomicMarkableReference<ShareData02> atomicMarkableReference = new AtomicMarkableReference(shareData02,true);
		atomicMarkableReference.compareAndSet(shareData02, shareData022, true, false);
		System.out.println(atomicMarkableReference.getReference());
	}

~~~

* 带信号量的原子引用
* Demo-5

~~~java
package atomic;

import java.util.concurrent.atomic.AtomicStampedReference;

class ShareData03 {
	volatile String info = "";

	public String getInfo() {
		return info;
	}

	public ShareData03 addInfoAndget(String info) {
		this.info = this.info + info;
		return this;
	}

	@Override
	public String toString() {
		return "ShareData03 [info=" + info + "]";
	}

}

public class AtomicStampedReferenceTest {

	public static void main(String[] args) {
		ShareData03 shareData03 = new ShareData03();
		AtomicStampedReference<ShareData03> atomicStampedReference = new AtomicStampedReference<ShareData03>(
				shareData03, 1);// 设置初始信号量1，即：总是AA先启动
		new Thread(() -> {
			for (int i = 0; i < 5; i++) {
				while (atomicStampedReference.getStamp() != 1) {

				}
				atomicStampedReference.attemptStamp(shareData03.addInfoAndget(Thread.currentThread().getName() + "-->"),
						2);
			}
		}, new String("AA")).start();
		new Thread(() -> {
			for (int i = 0; i < 5; i++) {
				while (atomicStampedReference.getStamp() != 2) {

				}
				atomicStampedReference.attemptStamp(shareData03.addInfoAndget(Thread.currentThread().getName() + "-->"),
						3);

			}
		}, new String("BB")).start();
		new Thread(() -> {
			for (int i = 0; i < 5; i++) {
				while (atomicStampedReference.getStamp() != 3) {

				}
				atomicStampedReference.attemptStamp(shareData03.addInfoAndget(Thread.currentThread().getName() + "-->"),
						1);

			}

		}, new String("CC")).start();
		while (Thread.activeCount() >= 2) {

		}
		System.out.println(shareData03);
	}

}

~~~

**总结**：原子类通过自旋锁CAS+volatile的模式实现了线程并发安全，后又添加反射代理类(AtomicIntegerFieldUpdater\AtomicLongFieldUpdater\AtomicReferenceFieldUpdater)来实现对于其他已知结构体内属性的原子更新。通过封装添加标记信号量(AtomicStampedReference\AtomicMarkableReferenceTest)的方式实现线程之间的条件控制.

# 五、Locks锁包

### 1.Summary

* ### Inteface

| Interface     | Description                                                  |
| :------------ | :----------------------------------------------------------- |
| Condition     | `Condition` factors out the `Object` monitor methods wait ,notify() and notifyAll() into distinct objects to give the effect of having multiple wait-sets per object, by combining them with the use of arbitrary Lock implementations. |
| Lock          | `Lock` implementations provide more extensive locking operations than can be obtained using `synchronized` methods and statements. |
| ReadWriteLock | A `ReadWriteLock` maintains a pair of associated locks, one for read-only operations and one for writing. |

* ### Implementation

| Class                            | Description                                                  |
| :------------------------------- | :----------------------------------------------------------- |
| AbstractOwnableSynchronizer      | A synchronizer that may be exclusively owned by a thread.    |
| AbstractQueuedLongSynchronizer   | A version of AbstractQueuedSynchronizer in which synchronization state is maintained as a `long`. |
| AbstractQueuedSynchronizer       | Provides a framework for implementing blocking locks and related synchronizers (semaphores, events, etc) that rely on first-in-first-out (FIFO) wait queues. |
| LockSupport                      | Basic thread blocking primitives for creating locks and other synchronization classes. |
| ReentrantLock                    | A reentrant mutual exclusion Lock with the same basic behavior and semantics as the implicit monitor lock accessed using `synchronized` methods and statements, but with extended capabilities. |
| ReentrantReadWriteLock           | An implementation of ReadWriteLock supporting similar semantics to ReentrantLock. |
| ReentrantReadWriteLock.ReadLock  | The lock returned by method ReentrantReadWriteLock.readLock(). |
| ReentrantReadWriteLock.WriteLock | The lock returned by method ReentrantReadWriteLock.writeLock(). |
| StampedLock                      | A capability-based lock with three modes for controlling read/write access. |

### 2.condition条件调度

**对比Demo-5，虽然自旋锁避免了线程的阻塞，但是cpu却会空转。在locks包中提供了condition类用以在多线程环境中对线程的运行和挂起提供了调度方法**

* Demo-6

~~~java
package atomic;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class ShareData04 {
	
	private int num=0;
	private int single=1;
	private Lock lock = new ReentrantLock();
	private Condition c1 = lock.newCondition();
	private Condition c2 = lock.newCondition();
	private Condition c3 = lock.newCondition();

	public void add5() {
		lock.lock();
		try {
			while (single != 1) {
				c1.await();
			}
			single =2;
			num+=5;
			c2.signal();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			lock.unlock();
		}
	}

	public void add10() {
		lock.lock();
		try {
			while (single != 2) {
				c2.await();
			}
			single =3;
			num+=10;
			c3.signal();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			lock.unlock();
		}
	}

	public void add20() {
		lock.lock();
		try {
			while (single != 3) {
				c3.await();
			}
			single =1;
			num+=20;
			c1.signal();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			lock.unlock();
		}
	}

	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
	}
	
}

public class ConditionTest {

	public static void main(String[] args) {
		ShareData04 shareData04 = new ShareData04();
		new Thread(() -> {
			for (int i = 0; i < 5; i++) {
				shareData04.add5();
			}
		},"AA").start();
		new Thread(() -> {
			for (int i = 0; i < 5; i++) {
				shareData04.add10();
			}
		},"BB").start();
		new Thread(() -> {
			for (int i = 0; i < 5; i++) {
				shareData04.add20();
			}
		},"CC").start();
		while(Thread.activeCount()>=2) {
			
		}
		System.out.println(shareData04.getNum());
	}

}

~~~

* **通过Condition类对锁进行条件化控制，从而使cpu不用空转**

| Modifier and Type | Method                            | Description                                                  |
| :---------------- | :-------------------------------- | :----------------------------------------------------------- |
| `void`            | `await()`                         | Causes the current thread to wait until it is signalled or interrupted. |
| `boolean`         | `await(long time, TimeUnit unit)` | Causes the current thread to wait until it is signalled or interrupted, or the specified waiting time elapses. |
| `long`            | `awaitNanos(long nanosTimeout)`   | Causes the current thread to wait until it is signalled or interrupted, or the specified waiting time elapses. |
| `void`            | `awaitUninterruptibly()`          | Causes the current thread to wait until it is signalled.     |
| `boolean`         | `awaitUntil(Date deadline)`       | Causes the current thread to wait until it is signalled or interrupted, or the specified deadline elapses. |
| `void`            | `signal()`                        | Wakes up one waiting thread.                                 |
| `void`            | `signalAll()`                     | Wakes up all waiting threads.                                |

### 3.ReadWriteLock读写分离

**实现在读多写少的情况下提高效率**

* Demo-7

~~~java
package atomic;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

class ReadWriteLockDemo {
	private int number;
	private ReadWriteLock lock = new ReentrantReadWriteLock();

	// 读操作
	public void get() {
		lock.readLock().lock();
		try {
			System.out.println(Thread.currentThread().getName() + ":" + number);
		} finally {
			lock.readLock().unlock();
		}
	}

	// 写操作
	public void set(int number) {
		lock.writeLock().lock();
		try {
			this.number = number;
			System.out.println(Thread.currentThread().getName()+number);
		} finally {
			lock.writeLock().unlock();
		}
	}

}

public class ReentrantReadWriteLockTest {

	public static void main(String[] args) {
		ReadWriteLockDemo rw = new ReadWriteLockDemo();
		new Thread(() -> {
			rw.set((int)(Math.random()*101));
		},"Write:").start();
		
		for (int i = 0; i < 100; i++) {
			new Thread(() -> {
				rw.get();
			},"Read-"+i).start();
		}
	}

}

~~~

* Demo-8模拟缓存读写分离

~~~java
package locks;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

class MyCahe {
	private volatile Map<String, Object> map = new HashMap<>();
	/* private Lock lock=new ReentrantLock(); */
	ReadWriteLock rwLock = new ReentrantReadWriteLock();

	public void put(String key, Object value) {
		rwLock.writeLock().lock();
		try {
			System.out.println(Thread.currentThread().getName() + "\t 正在写入：" + key);
			// 模拟网络延迟
			try {
				TimeUnit.MILLISECONDS.sleep(300);
			} catch (Exception e) {
				e.printStackTrace();
			}
			map.put(key, value);
			System.out.println(Thread.currentThread().getName() + "\t 写入完成：");
		} finally {
			rwLock.writeLock().unlock();
		}
	}

	public void get(String key) {
		rwLock.readLock().lock();
		try {
			System.out.println(Thread.currentThread().getName() + "\t 正在读取：");
			// 模拟网络延迟
			try {
				TimeUnit.MILLISECONDS.sleep(300);
			} catch (Exception e) {
				e.printStackTrace();
			}
			Object result = map.get(key);
			System.out.println(Thread.currentThread().getName() + "\t 读取完成："+result);
		} finally {
			rwLock.readLock().unlock();
		}
	}

}

public class ReentrantReadWriteLockTest2 {

	public static void main(String[] args) {
		MyCahe myCahe = new MyCahe();
		for (int i = 0; i < 5; i++) {
			final int tempInt = i;
			new Thread(() -> {
				myCahe.put(tempInt + "", tempInt + "");
			}, String.valueOf(i)).start();
		}
		for (int i = 0; i < 5; i++) {
			final int tempInt = i;
			new Thread(() -> {
				myCahe.get(tempInt + "");
			}, String.valueOf(i)).start();
		}
	}

}

~~~

# 六、Semaphore并发工具类

| CountDownLatch | A synchronization aid that allows one or more threads to wait until a set of operations being performed in other threads completes. |
| :------------- | ------------------------------------------------------------ |
| CyclicBarrier  | A synchronization aid that allows a set of threads to all wait for each other to reach a common barrier point. |
| Exchanger      | A synchronization point at which threads can pair and swap elements within pairs. |
| Semaphore      | A counting semaphore.                                        |
| Phaser         | A reusable synchronization barrier, similar in functionality to CyclicBarrier and CountDownLatch but supporting more flexible usage. |

### 1.CountDownLatch

* 我们在Deo-6中用内置的single来做为线程之间的调度信号，CountDownLatch就是内置的信号类，用以计数线程的启动前置条件
* demo-9

~~~java
package current;

import java.util.concurrent.CountDownLatch;
/**
 * 
 * @author 仙缘一梦
 *
 */
public class CountDownLatchTest {

	public static void main(String[] args) throws InterruptedException{
		CountDownLatch countDownLatch = new CountDownLatch(6);
		
		for(int i=1;i<=6;i++) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					System.out.println(Thread.currentThread().getName()+"\t 运行");
					countDownLatch.countDown();//减少锁存器的计数，如果计数达到零，则释放所有等待线程。
				}
			},String.valueOf(i)).start();
		}
		
		countDownLatch.await();//导致当前线程等待，直到锁存器递减计数到零为止，除非该线程被中断。
		System.out.println("所有线程开启"+Thread.currentThread().getName()+"方法运行");
	}

}
~~~

**对比一下我们可以知道**：CountDownLatch等同于加了条件的condition

那么又出现了一个问题：我们不可能给每一个线程都手动的添加反馈，我们怎么动态的加载返回的message呢？

我们这里用枚举来解决问题

demo-10

~~~java
package current;

import java.util.concurrent.CountDownLatch;
/**
 * 
 * @author 仙缘一梦
 *
 */
public class CountDownLatchTest2 {

	public static void main(String[] args) throws InterruptedException{
		CountDownLatch countDownLatch = new CountDownLatch(6);
		
		for(int i=1;i<=6;i++) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					System.out.println(Thread.currentThread().getName()+"\t 及格");
					countDownLatch.countDown();//减少锁存器的计数，如果计数达到零，则释放所有等待线程。
				}
			},ClassEnum.forEach(i).getRetMessage()).start();
		}
		
		countDownLatch.await();//导致当前线程等待，直到锁存器递减计数到零为止，除非该线程被中断。
		System.out.println("所有课程及格"+Thread.currentThread().getName()+"来年好运");
	}

}

-------------
package current;
/**
 * 枚举本质上是单例
 * @author 仙缘一梦
 *
 */
public enum ClassEnum {
	ONE(1, "英语"), TWO(2, "数学"), THREE(3, "语文"), 
	FOUR(4, "体育"), FIVE(5, "美术"), SIX(6, "编程");
	private Integer retCode;
	private String retMessage;
	private ClassEnum(Integer retCode, String retMessage) {
		this.retCode = retCode;
		this.retMessage = retMessage;
	}
	public static ClassEnum forEach(int index) {
		ClassEnum[] values = ClassEnum.values();
		for (ClassEnum element : values) {
			if(index==element.retCode) {
				return element;
			}
		}
		return null;
	}
	public Integer getRetCode() {
		return retCode;
	}
	public String getRetMessage() {
		return retMessage;
	}
}
~~~

### 2.CyclicBarrier

demo-11

~~~java
package current;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class CyclicBarrierTest {

	public static void main(String[] args) {
		CyclicBarrier cyclicBarrier = new CyclicBarrier(7,()->{
			System.out.println("果元天尊");
		});
		for (int i = 1; i <=7; i++) {
			final int tempInt=i;
			new Thread(()->{
				System.out.println(Thread.currentThread().getName()+"\t 收集水果"+tempInt);
				try {
					cyclicBarrier.await();
				} catch (InterruptedException | BrokenBarrierException e) {
					e.printStackTrace();
				}
			},String.valueOf(i)).start();//这里也可以练习一下枚举
		}

	}

}

~~~

### 3.Semaphore

**semaphore信号量用于多个共享资源的互斥使用，和对并发线程数的控制**

demo-12

~~~java
package current;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class SemaphoreTest {

	public static void main(String[] args) {
		Semaphore semaphore = new Semaphore(3);//模拟多资源
		for (int i = 1; i <= 6; i++) {
			new Thread(() -> {
				try {
					semaphore.acquire();
					System.out.println(Thread.currentThread().getName() + "\t 拿到资源");
					TimeUnit.SECONDS.sleep(3);
					System.out.println(Thread.currentThread().getName() + "\t 占有3s释放资源");
				} catch (InterruptedException e) {
					e.printStackTrace();
				}finally {
					semaphore.release();
				}

			}, String.valueOf(i)).start();
		}
	}

}

~~~

# 七、BlockingQueue的补充

| Class                         | Description                                                  |
| :---------------------------- | ------------------------------------------------------------ |
| SynchronousQueue<E>           | A blocking queue in which each insert operation must wait for a corresponding remove operation by another thread, and vice versa.**put和take是一对一关系，即生产者消费者1：1** |
| ArrayBlockingQueue<E>         | A bounded blocking queue backed by an array.                 |
| LinkedBlockingDeque<E>        | An optionally-bounded blocking deque based on linked nodes.  |
| PriorityBlockingQueue<E>      | An unbounded blocking queue that uses the same ordering rules as class PriorityQueue and supplies blocking retrieval operations. |
| DelayQueue<E extends Delayed> | An unbounded blocking queue of `Delayed` elements, in which an element can only be taken when its delay has expired. |
| LinkedBlockingQueue<E>        | An optionally-bounded blocking queue based on linked nodes.  |
| LinkedTransferQueue<E>        | An unbounded TransferQueue based on linked nodes.            |

**想要了解线程池就必须JUC提供的阻塞队列，阻塞队列是典型的生产者消费者模式，生产者消费者又分一对一，和多对多**

* demo-13.利用SynchronousQueue队列实现生产者消费者的 一对一

~~~java
package current.blocking;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;

public class SynchronousQueueTest {

	public static void main(String[] args) throws InterruptedException {
		BlockingQueue<String> synchronousQueue = new SynchronousQueue<>();
		new Thread(() -> {

			try {
				System.out.println(Thread.currentThread().getName() + "\t put 1");
				synchronousQueue.put("1");
				System.out.println(Thread.currentThread().getName() + "\t put 2");
				synchronousQueue.put("2");
				System.out.println(Thread.currentThread().getName() + "\t put 3");
				synchronousQueue.put("3");
				System.out.println(Thread.currentThread().getName() + "\t put 4");
				synchronousQueue.put("4");
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}, "AAA").start();

		new Thread(() -> {

			try {
				TimeUnit.SECONDS.sleep(5);
				System.out.println(Thread.currentThread().getName() + "\t "+synchronousQueue.take());
				TimeUnit.SECONDS.sleep(5);
				System.out.println(Thread.currentThread().getName() + "\t "+synchronousQueue.take());
				TimeUnit.SECONDS.sleep(5);
				System.out.println(Thread.currentThread().getName() + "\t "+synchronousQueue.take());
				TimeUnit.SECONDS.sleep(5);
				System.out.println(Thread.currentThread().getName() + "\t "+synchronousQueue.take());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}, "BBB").start();
	}

}

~~~

* SynchronousQueue同步队列，没生产一个就消费一个，有点单例的思想

# 八、Thread线程通信

* demo-14.线程交互

~~~java
package prodconsumer;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class ShareData{
	private int number=0;
	private Lock lock=new ReentrantLock();
	private Condition condition = lock.newCondition();
	public void increment() throws Exception{
		lock.lock();
		
		//1.判断
		while(number!=0) {
			//等待
			condition.await();
			
		}
		//2.操作资源
		number++;
		System.out.println(Thread.currentThread().getName()+"\t"+number);
		//3.通知唤醒
		condition.signalAll();
		lock.unlock();
	}
	public void decrease() throws Exception{
		lock.lock();
		
		//1.判断
		while(number==0) {
			//等待
			condition.await();
			
		}
		//2.操作资源
		number--;
		System.out.println(Thread.currentThread().getName()+"\t"+number);
		//3.通知唤醒
		condition.signalAll();
		lock.unlock();
	}
}
/**
 * 
 * @author 仙缘一梦
 * 多线程情形下的调度要素
 * 1.	线程		方法		资源
 * 2.	判断		操作		通知
 * 3.	防止虚假唤醒
 * 0.	CAS模式循环取值比较，防止虚假唤醒，注意if和while在判断中的区别
 */
public class TraditionDemo {

	public static void main(String[] args) {
		
		ShareData shareData = new ShareData();
		new Thread(()->{
			for (int i = 0; i < 5; i++) {
				try {
					shareData.increment();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
		},"AA").start();
		
		new Thread(()->{
			for (int i = 0; i < 5; i++) {
				try {
					shareData.decrease();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
		},"BB").start();
		
		new Thread(()->{
			for (int i = 0; i < 5; i++) {
				try {
					shareData.decrease();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
		},"CC").start();
		
	}

}

~~~

**在这个示例中同学们可以把ShareData中的while判断改为if判断，思考其中的不同**

* demo-15.volatile/CAS/Atomicnteger/BlockQueue/线程交互/原子引用整合

~~~java
package prodconsumer;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

class MyResource{
	private volatile boolean FLAG=true;//默认开启，进行生产+消费
	private AtomicInteger atomicInteger=new AtomicInteger();
	
	BlockingQueue<String> blockingQueue=null;//构造注入
	
	public MyResource(BlockingQueue<String> blockingQueue) {
		this.blockingQueue=blockingQueue;
		System.out.println(blockingQueue.getClass().getName());//测试从那儿来的实例
	}
	public void myProd() throws Exception {
		String data =null;
		boolean offer;
		while(FLAG) {
			data = atomicInteger.incrementAndGet()+"";
			offer = blockingQueue.offer(data,2L,TimeUnit.SECONDS);
			if(offer) {
				System.out.println(Thread.currentThread().getName()+"\t\t 插入队列"+data+"成功");
			}else {
				System.out.println(Thread.currentThread().getName()+"\t\t 插入队列"+data+"失败");
			}
			TimeUnit.SECONDS.sleep(1);
		}
		System.out.println(Thread.currentThread().getName()+"\t\t FLAG=true,生产结束");
	}
	
	public void myConsumer() throws Exception{
		String poll =null;
		while(FLAG) {
			poll = blockingQueue.poll(2L,TimeUnit.SECONDS);
			if(null==poll||poll.equalsIgnoreCase("")) {
				FLAG=false;
				System.out.println(Thread.currentThread().getName()+"\t 超时未取得信息 退出");
				System.out.println();
				System.out.println();
				System.out.println();
				return;
			}
			System.out.println(Thread.currentThread().getName()+"\t 取出队列"+poll+"成功");
			
		}
	}
	public void stop() throws Exception{
		this.FLAG=false;//主动叫停
	}
	
}
/**
 * 
 * @author 仙缘一梦
 * volatile/CAS/Atomicnteger/BlockQueue/线程交互/原子引用
 */
public class BlockQueueDemo {

	public static void main(String[] args) throws Exception {
		
		MyResource myResource = new MyResource(new ArrayBlockingQueue<>(10));
		new Thread(()->{
			System.out.println(Thread.currentThread().getName()+"\t\t 生产线程启动");
			try {
				myResource.myProd();
			} catch (Exception e) {
				e.printStackTrace();
			}
		},"Prod").start();
		new Thread(()->{
			System.out.println(Thread.currentThread().getName()+"\t 消费线程启动");
			try {
				myResource.myConsumer();
			} catch (Exception e) {
				e.printStackTrace();
			}
		},"Consumer").start();
		
		TimeUnit.SECONDS.sleep(5);//main线程停滞5s
		myResource.stop();//主动叫停活动
	}

}

~~~



---------------------------------

[^subsequent ]: 随后的
[^prior ]: 首先的
[^scalable ]: 可扩展的
[^periodically]: 定期地.
[^tasks]: 任务
