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
