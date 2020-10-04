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
