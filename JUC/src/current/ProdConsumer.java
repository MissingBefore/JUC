package current;


import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
/**
 * 内聚方法，资源低耦合<br>
 * 
 * @author 仙缘一梦
 *
 */
class ShareData {// 资源类
	private static volatile int number = 0;
	private Lock lock = new ReentrantLock();
	// Causes the current thread to wait until it is signalled
	private Condition condition = lock.newCondition();

	public void increment() throws Exception {
		lock.lock();
		try {
			// 1 判断
			while (number != 0) {//为什么不用if？防止
				// CAS等待，不能生产
				condition.await();
				//每次都询问，用了么？用了么？用了么？用了么？
			}
			// 2.
			number++;
			System.out.println(Thread.currentThread().getName() + "\t" + number);
			condition.signalAll();//Wakes up all waiting threads.
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			lock.unlock();
		}

	}

	public void decrement() throws Exception {
		int i=0;
		lock.lock();
		try {
			// 1 判断
			while (number == 0) {
				// CAS等待，不能消费
				condition.await();
				System.out.println("swap"+(++i));
				//每次都询问，还有么？还有么？还有么？还有么？
			}
			// 2.
			number--;
			System.out.println(Thread.currentThread().getName() + "\t" + number);
			condition.signalAll();//Wakes up all waiting threads.
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			lock.unlock();
		}
	}
}

/**
 * 传统生产者消费者模式
 * 
 * @author 仙缘一梦 
 * sync==》wait==》notify <br>
 * lock==》await==》signal<br>
 */
public class ProdConsumer {

	public static void main(String[] args) {
		ShareData shareData = new ShareData();
		new Thread(new Runnable() {//消费

			@Override
			public void run() {
				for (int i = 0; i < 5; i++) {
					try {
						shareData.decrement();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}, "AA").start();
		new Thread(new Runnable() {//生产
			@Override
			public void run() {
				for (int i = 0; i < 5; i++) {
					try {
						shareData.increment();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}, "BB").start();

	}

}
