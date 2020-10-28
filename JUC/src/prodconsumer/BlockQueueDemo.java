package prodconsumer;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

class MyResource{
	private volatile boolean FLAG=true;//默认开启，进行生产+消费
	private AtomicInteger atomicInteger=new AtomicInteger();
	
	BlockingQueue<String> blockingQueue=null;//构造注入
	
	public MyResource(BlockingQueue<String> blockingQueue) {
		this.blockingQueue=blockingQueue;
		System.out.println(blockingQueue.getClass().getName());//测试从那儿来的实例
	}
	public void myProd() throws Exception {
		String data =null;
		boolean offer;
		while(FLAG) {
			data = atomicInteger.incrementAndGet()+"";
			offer = blockingQueue.offer(data,2L,TimeUnit.SECONDS);
			if(offer) {
				System.out.println(Thread.currentThread().getName()+"\t\t 插入队列"+data+"成功");
			}else {
				System.out.println(Thread.currentThread().getName()+"\t\t 插入队列"+data+"失败");
			}
			TimeUnit.SECONDS.sleep(1);
		}
		System.out.println(Thread.currentThread().getName()+"\t\t FLAG=true,生产结束");
	}
	
	public void myConsumer() throws Exception{
		String poll =null;
		while(FLAG) {
			poll = blockingQueue.poll(2L,TimeUnit.SECONDS);
			if(null==poll||poll.equalsIgnoreCase("")) {
				FLAG=false;
				System.out.println(Thread.currentThread().getName()+"\t 超时未取得信息 退出");
				System.out.println();
				System.out.println();
				System.out.println();
				return;
			}
			System.out.println(Thread.currentThread().getName()+"\t 取出队列"+poll+"成功");
			
		}
	}
	public void stop() throws Exception{
		this.FLAG=false;//主动叫停
	}
	
}
/**
 * 
 * @author 仙缘一梦
 * volatile/CAS/Atomicnteger/BlockQueue/线程交互/原子引用
 */
public class BlockQueueDemo {

	public static void main(String[] args) throws Exception {
		
		MyResource myResource = new MyResource(new ArrayBlockingQueue<>(10));
		new Thread(()->{
			System.out.println(Thread.currentThread().getName()+"\t\t 生产线程启动");
			try {
				myResource.myProd();
			} catch (Exception e) {
				e.printStackTrace();
			}
		},"Prod").start();
		new Thread(()->{
			System.out.println(Thread.currentThread().getName()+"\t 消费线程启动");
			try {
				myResource.myConsumer();
			} catch (Exception e) {
				e.printStackTrace();
			}
		},"Consumer").start();
		
		TimeUnit.SECONDS.sleep(5);//main线程停滞5s
		myResource.stop();//主动叫停活动
	}

}
