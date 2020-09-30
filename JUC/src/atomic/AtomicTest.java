package atomic;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
class srcTo{
	int a;
	double b;
}
public class AtomicTest {

	public static void main(String[] args) {
		AtomicInteger atomicInteger = new AtomicInteger(2);
		AtomicReference<?> atomicReference = new AtomicReference<srcTo>();
		atomicInteger.compareAndSet(2, 3);
		System.out.println(atomicInteger);
	}

}
