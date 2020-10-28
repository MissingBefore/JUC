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
