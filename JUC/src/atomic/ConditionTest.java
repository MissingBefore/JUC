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
