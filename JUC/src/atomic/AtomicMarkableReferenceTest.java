package atomic;

import java.util.concurrent.atomic.AtomicMarkableReference;
class ShareData02{
	volatile int num;

	public ShareData02(int num) {
		super();
		this.num = num;
	}

	@Override
	public String toString() {
		return "ShareData02 [num=" + num + "]";
	}
	
}
public class AtomicMarkableReferenceTest {

	public static void main(String[] args) {
		ShareData02 shareData02 = new ShareData02(1);
		ShareData02 shareData022 = new ShareData02(3);
		AtomicMarkableReference<ShareData02> atomicMarkableReference = new AtomicMarkableReference<ShareData02>(shareData02,true);
		atomicMarkableReference.compareAndSet(shareData02, shareData022, true, false);
		System.out.println(atomicMarkableReference.getReference());
	}

}
