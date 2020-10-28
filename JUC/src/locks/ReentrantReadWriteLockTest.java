package locks;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
/**
 * 读写分离测试
 * @author 仙缘一梦
 */
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
