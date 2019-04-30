package mytest;

import java.util.ArrayList;
import java.util.List;

/**
 * 曾经的面试题：（淘宝？）
 * 实现一个容器，提供两个方法，add，size
 * 写两个线程，线程1添加10个元素到容器中，线程2实现监控元素的个数，当个数到5个时，线程2给出提示并结束
 * 
 * 给lists添加volatile之后，t2能够接到通知，但是，t2线程的死循环很浪费cpu，如果不用死循环，该怎么做呢？
 * 
 * 这里使用wait和notify做到，wait会释放锁，而notify不会释放锁
 * 需要注意的是，运用这种方法，必须要保证t2先执行，也就是首先让t2监听才可以
 * 
 * 阅读下面的程序，并分析输出结果
 * 可以读到输出结果并不是size=5时t2退出，而是t1结束时t2才接收到通知而退出
 * 想想这是为什么？
 * 
 * notify之后，t1必须释放锁，t2退出后，也必须notify，通知t1继续执行
 * 整个通信过程比较繁琐
 * @author mashibing
 */
public class MyContainer1 {
	private List<Integer> list = new ArrayList<>();
	
	public void add(int ele) {
		list.add(ele);
	}
	
	public int size() {
		return list.size();
	}
	
	public static void main(String[] args) {
		MyContainer1 c = new MyContainer1();
		
		//新建一个对象充当锁的作用
		Object lock = new Object();
		
		new Thread(()->{
			System.out.println(Thread.currentThread().getName()+" 启动");
			synchronized(lock) {
				if(c.size()!=5) {
					try {
						lock.wait();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
				System.out.println(Thread.currentThread().getName()+" 停止");
				//通知其继续进行
				lock.notifyAll();
				
			}
			
			
		},"线程2" ).start();;
		
		
		new Thread(()->{
			System.out.println(Thread.currentThread().getName()+" 启动");
			synchronized(lock) {
				for(int i=0;i<10;i++) {
				
					c.add(i);
					System.out.println(Thread.currentThread().getName()+"插入数据"+i);
					if(c.size()==5) {
						lock.notifyAll();
						
						try {
							lock.wait();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					//等待100ms再增加
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
			}
			System.out.println(Thread.currentThread().getName()+" 停止");
		},"线程1" ).start();
		
		
		
	}
}
