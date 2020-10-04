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
