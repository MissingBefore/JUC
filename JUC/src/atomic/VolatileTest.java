package atomic;

import java.util.concurrent.atomic.AtomicInteger;

class ShareData {
	private /* volatile */ int num = 0;
	private AtomicInteger ai=new AtomicInteger(0);
	public void incrementAi() {
		ai.getAndIncrement();
	}
	public AtomicInteger getAi() {
		return ai;
	}

	public void setAi(AtomicInteger ai) {
		this.ai = ai;
	}

	public void increment() {
		num++;
	}

	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
	}

}

public class VolatileTest {

	public static void main(String[] args) {
		ShareData shareData = new ShareData();
		for (int i = 0; i < 20; i++) {
			new Thread(() -> {
				for (int j = 0; j < 100; j++) {
					shareData.increment();
				}
			},"Thread-"+i).start();
		}
		while(Thread.activeCount()>=2) {
			Thread.yield();
		}

		System.out.println("ShareData finally:"+shareData.getNum());
		
		for (int i = 0; i < 20; i++) {
			new Thread(() -> {
				for (int j = 0; j < 100; j++) {
					shareData.incrementAi();
					System.out.println(Thread.currentThread().getName()+"\t"+shareData.getAi().get());
				}
			},"Thread-"+i).start();
		}
		while(Thread.activeCount()>=2) {
			Thread.yield();
		}

		System.out.println("ShareData finally:"+shareData.getAi().get());

	}

}
