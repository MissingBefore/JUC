package atomic;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

class SrcTo {
	int a;
	double b;

	public int getA() {
		return a;
	}

	public void setA(int a) {
		this.a = a;
	}

	public double getB() {
		return b;
	}

	public void setB(double b) {
		this.b = b;
	}

	public SrcTo(int a, double b) {
		super();
		this.a = a;
		this.b = b;
	}
}

public class AtomicTest {

	public static void main(String[] args) {
		SrcTo srcTo = new SrcTo(1, 2.0);
		SrcTo srcTo2 = new SrcTo(2, 3.1);
		AtomicInteger atomicInteger = new AtomicInteger(2);
		AtomicReference<SrcTo> atomicReference = new AtomicReference<SrcTo>(srcTo);
		atomicInteger.compareAndSet(2, 3);
		System.out.println(atomicInteger);
		atomicReference.compareAndSet(srcTo, srcTo2);
	}

}
