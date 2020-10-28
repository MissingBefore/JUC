package collections;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

class ShareData {
	private Integer id;
	private char name;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	
	public char getName() {
		return name;
	}

	public void setName(char name) {
		this.name = name;
	}

	public ShareData(Integer id, char name) {
		super();
		this.id = id;
		this.name = name;
	}

	@Override
	public String toString() {
		return "ShareData [id=" + id + ", name=" + name + "]";
	}

	public ShareData() {
	}

}

public class UnmodifiedCollection {

	public static void main(String[] args) {
		ArrayList<ShareData> arrayList = new ArrayList<>();
		String ch="abcdefjhijklmnopqrstuvwxyz1234567890";
		Random random = new Random();
		
		/*
		 * 创建arrayList的只读视图
		 */
		List<ShareData> unmodifiableList = Collections.unmodifiableList(arrayList);
		Runnable runnable01 = new Runnable() {
			@Override
			public void run() {
				System.out.println(Thread.currentThread().getName());
				for (int i = 0; i < 5; i++) {
					char charAt = ch.charAt(random.nextInt(ch.length()));
					ShareData shareData2 = new ShareData(i,charAt);
					arrayList.add(shareData2);
				}
			}
		};
		Runnable runnable02 = new Runnable() {
			@Override
			public void run() {
				System.out.println(Thread.currentThread().getName());
				for (Iterator<ShareData> iterator = unmodifiableList.iterator(); iterator.hasNext();) {
					ShareData shareData = (ShareData) iterator.next();
					System.out.println(shareData);
				}
			}
		};
		BlockingQueue<Runnable> blockingQueue = new ArrayBlockingQueue<Runnable>(4);
		ThreadPoolExecutor threadPoolExecutor = 
				new ThreadPoolExecutor(2, 4, 20, TimeUnit.SECONDS, blockingQueue);
		threadPoolExecutor.execute(runnable02);
		threadPoolExecutor.execute(runnable01);
		//16 按位异或 (4 右移 2位)
		//10000 ^
		//100 >>2
		//00001
		//10001
		//17
		System.out.println(16^(30 >> 2));
		//(h = key.hashCode()) ^ (h >>> 16);
		//(h ^ (h >>> 16)) & HASH_BITS;
		
	}

}
