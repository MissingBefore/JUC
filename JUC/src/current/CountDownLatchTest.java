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

