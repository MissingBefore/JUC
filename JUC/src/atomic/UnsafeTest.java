package atomic;

public class UnsafeTest {
	public volatile int value;
	
	public int getValue() {
		return value;
	}
	public void setValue(int value) {
		this.value = value;
	}
	public void getAndInt(UnsafeTest unsafe,UnsafeTest anotherUnsafe) {
		while(this.getValue()!=anotherUnsafe.getValue()) {
			
		}
		unsafe.setValue(unsafe.getValue()+1);
	}
	public static void main(String[] args) {

	}
}
