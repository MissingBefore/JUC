package locks;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
/**
 * 简单缓存实现
 * @author 仙缘一梦
 *
 */
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
