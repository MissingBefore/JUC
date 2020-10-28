package locks;

public class CASTest {
	public volatile int  i = 1;
	public void increment() {
		while (i < 1) {
			System.out.println("消费：" + i);
			i++;
		}
		//注意：sleep不释放资源
		try {
			Thread.sleep(300);
			//Thread.yield();
			//Thread.currentThread().interrupt();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void decrease() {
		while (i > 0) {
			System.out.println("生产：" + i);
			i--;
		}
		try {
		Thread.sleep(300);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		CASTest cas = new CASTest();
		new Thread(() -> {
			for (int i = 0; i < 5; i++) {
				cas.increment();
			}
		}).start();
		new Thread(() -> { 
			for (int i = 0; i < 5; i++) {
				cas.decrease();
			}
		}).start();
		
		while (Thread.activeCount() >= 2) {
		}
		System.out.println("还剩：" + cas.i);

	}
}