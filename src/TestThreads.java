import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;


public class TestThreads {
	public static void main(String[] args) {
		long start=System.currentTimeMillis();
		ConcurrentHashMap<String, String> crmMap = new ConcurrentHashMap<String, String>();
		try {
			//通过key 进行HTTP请求获取三种返回值 value1 value2 value3
			//TODO 执行三个线程，HTTP请求，
			CountDownLatch latch=new CountDownLatch(3);
			ThreadGroup group =new ThreadGroup("test");
			Test test1=new TestThreads().new Test("1",latch,crmMap,group);
			Test test2=new TestThreads().new Test("2",latch,crmMap,group);
			Test test3=new TestThreads().new Test("3",latch,crmMap,group);
			Thread thread1 = new Thread(group,test1);
			Thread thread2 = new Thread(group,test2);
			Thread thread3 = new Thread(group,test3);
			thread1.start();
			thread2.start();
			thread3.start();
			try {
				latch.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			System.out.println("捕获");
			e.printStackTrace();
		}
		Set<String> keys=crmMap.keySet();
		for (String key : keys) {
			System.out.println(key+":"+crmMap.get(key));
		}
		long end= System.currentTimeMillis();
		System.out.println(end-start);
		//返回
		
	}
	
	class Test extends Thread{
		CountDownLatch latch;
		String key;
		ConcurrentHashMap<String, String> crmMap;
		ThreadGroup group;
		public Test(String key,CountDownLatch latch,ConcurrentHashMap<String, String> crmMap,ThreadGroup group){
			this.key=key;
			this.latch=latch;
			this.crmMap=crmMap;
			this.group=group;
			
		}
		@Override
		public void run() {
			switch (key) {
			case "1":
				try {
					Thread.sleep(5000);
					crmMap.put(key, "5000");
					System.out.println("1 done");
					latch.countDown();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				break;
			case "2":
				try {
					Thread.sleep(2000);
					crmMap.put(key, "2000");
					System.out.println("2 done");
					latch.countDown();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				break;
			case "3":
				try {
					Thread.sleep(3000);
					crmMap.put(key, "3000");
					System.out.println(latch.getCount());
					System.out.println("3 done");
					latch.countDown();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				break;

			default:
				break;
			}
		}
	}
}
