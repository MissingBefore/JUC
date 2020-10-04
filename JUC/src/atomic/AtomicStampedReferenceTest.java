package atomic;

import java.util.concurrent.atomic.AtomicStampedReference;

class ShareData03 {
	volatile String info = "";

	public String getInfo() {
		return info;
	}

	public ShareData03 addInfoAndget(String info) {
		this.info = this.info + info;
		return this;
	}

	@Override
	public String toString() {
		return "ShareData03 [info=" + info + "]";
	}

}

public class AtomicStampedReferenceTest {

	public static void main(String[] args) {
		ShareData03 shareData03 = new ShareData03();
		AtomicStampedReference<ShareData03> atomicStampedReference = new AtomicStampedReference<ShareData03>(
				shareData03, 1);// 设置初始信号量1，即：总是AA先启动
		new Thread(() -> {
			for (int i = 0; i < 5; i++) {
				while (atomicStampedReference.getStamp() != 1) {

				}
				atomicStampedReference.attemptStamp(shareData03.addInfoAndget(Thread.currentThread().getName() + "-->"),
						2);
			}
		}, new String("AA")).start();
		new Thread(() -> {
			for (int i = 0; i < 5; i++) {
				while (atomicStampedReference.getStamp() != 2) {

				}
				atomicStampedReference.attemptStamp(shareData03.addInfoAndget(Thread.currentThread().getName() + "-->"),
						3);

			}
		}, new String("BB")).start();
		new Thread(() -> {
			for (int i = 0; i < 5; i++) {
				while (atomicStampedReference.getStamp() != 3) {

				}
				atomicStampedReference.attemptStamp(shareData03.addInfoAndget(Thread.currentThread().getName() + "-->"),
						1);

			}

		}, new String("CC")).start();
		while (Thread.activeCount() >= 2) {

		}
		System.out.println(shareData03);
	}

}
