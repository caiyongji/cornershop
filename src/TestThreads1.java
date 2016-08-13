import iuv.cns.utils.NetUtil;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class TestThreads1 {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		CountDownLatch latch=new CountDownLatch(4);
		ThreadGroup group =new ThreadGroup("NAME");
		Th t1=new TestThreads1().new Th(1,latch,group);
		Thread thread1=new Thread(group,t1);
		thread1.start();
		Th t2=new TestThreads1().new Th(2,latch,group);
		Thread thread2=new Thread(group,t2);
		thread2.start();
		Th t3=new TestThreads1().new Th(3,latch,group);
		Thread thread3=new Thread(group,t3);
		thread3.start();
		Th t4=new TestThreads1().new Th(4,latch,group);
		Thread thread4=new Thread(group,t4);
		thread4.start();
		try {
			latch.await(5, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			System.err.println("await:"+e);
		}
		System.out.println("下一步");
	}

	private class Th implements Runnable{
		ThreadGroup group;
		int key;
		CountDownLatch latch;
		public Th(int key,CountDownLatch latch,ThreadGroup group){
			this.key=key;
			this.group=group;
			this.latch=latch;
		}

		@Override
		public void run(){
			switch (key) {
				case 1:
					try {
						Thread.sleep(1000);
						System.out.println("t1执行出错，开始抛出异常");
						throw new Exception("异常！！！");
					} catch (Exception e) {
						group.interrupt();
						System.out.println("t1执行了interrupt");
						downToZero(latch,"t1");
					}
					break;
				case 2:
					try {
						Thread.sleep(200);
						System.out.println("t2睡了200毫秒1");
						Thread.sleep(200);
						System.out.println("t2睡了200毫秒2");
						Thread.sleep(200);
						System.out.println("t2睡了200毫秒3");
						Thread.sleep(300);
						System.out.println("t2睡了300毫秒4");
						String result=NetUtil.connectUrlResponse("https://qr.snipp.com/CreateQr.aspx#comefromHome");
						System.out.println(result);
						System.out.println("t2睡了60毫秒4.0");
						Thread.sleep(1);
						System.out.println("t2睡了1毫秒4.1");
						Thread.sleep(300);
						System.out.println("t2睡了300毫秒5");
						System.out.println("t2");
						latch.countDown();
					} catch (Exception e) {
						System.out.println("t2异常！！！"+e.getMessage());
						downToZero(latch,"t2");
					}
					break;
				case 3:
					try {
						System.out.println("t3");
						latch.countDown();
					} catch (Exception e) {
						System.out.println("t3"+e.getMessage());
						downToZero(latch,"t3");
					}
					break;
				case 4:
					try {
						System.out.println("t4");
						latch.countDown();
					} catch (Exception e) {
						System.out.println("t4"+e.getMessage());
						downToZero(latch,"t4");
					}

					break;

				default:
					break;
			}
		}

		private synchronized void downToZero(CountDownLatch latch,String theadName) {
			latch.getCount();
			for (int i = 0; i < latch.getCount(); i++) {
				System.out.println(theadName+"当前count数："+latch.getCount());
				latch.countDown();
			}
		}

	}

}
