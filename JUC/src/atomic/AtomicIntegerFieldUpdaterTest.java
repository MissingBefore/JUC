package atomic;

import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

class ShareData01{
	/* private */ volatile  /*Integer*/int num;

	public Integer getNum() {
		return num;
	}

	public void setNum(Integer num) {
		this.num = num;
	}
	
	public ShareData01(Integer num) {
		super();
		this.num = num;
	}	
}
public class AtomicIntegerFieldUpdaterTest {

	public static void main(String[] args) {
		AtomicIntegerFieldUpdater<ShareData01> atomicIntegerFieldUpdater=AtomicIntegerFieldUpdater.newUpdater(ShareData01.class, "num");
		
		ShareData01 shareData01 = new ShareData01(1);
		
		System.out.println(atomicIntegerFieldUpdater.addAndGet(shareData01, 2));
	}

}
