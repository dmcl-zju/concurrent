package mytest;

import java.util.LinkedList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 面试题：写一个固定容量同步容器，拥有put和get方法，以及getCount方法，
 * 能够支持2个生产者线程以及10个消费者线程的阻塞调用
 * 
 * 使用lock和condition实现
 * 
 * @author mashibing
 */
public class ProAndCom2<T> {
	final private LinkedList<T> list = new LinkedList<>();
	final private int Max = 10;
	private int count = 0;
	
	Lock lock = new ReentrantLock();
	Condition producer = lock.newCondition();
	Condition comsumer = lock.newCondition();
	
	public void put(T t) {
		//这里一定要用while,因为如果用if,在消费者notifyAll会叫醒所有的线程，被叫醒的线程都不会再进行判断是否已经满了，直接生产，会使得容量溢出
		lock.lock();
		try {
			while(count==Max) {
				try {
					producer.wait();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			list.add(t);
			count++;
			//通知消费者进行消费---必须用notifyAll
			comsumer.notifyAll();
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			lock.unlock();
		}
	}
	
	synchronized public T get() {
		T t = null;
		lock.lock();
		
		try {
			while(count==0) {
				try {
					comsumer.wait();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			count--;
			t = list.removeFirst();
			//通知生产者进行生产
			producer.notifyAll();
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			lock.unlock();
		}
		return t;
	}
	//模拟生产者和消费者线程
	public static void main(String[] args) {
		ProAndCom1<String> c = new ProAndCom1<>();
		
		//先启动消费者等待消费
		for(int i=0;i<10;i++) {
			new Thread(() ->{
				for(int j=0;j<5;j++) {
					//每个人拿走5个产品
					System.out.println(Thread.currentThread().getName()+" 拿走了 "+c.get());
				}
				
			},"消费者"+i).start();
		}
		
		try {
			TimeUnit.SECONDS.sleep(2);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		//先启动消费者等待消费
		for(int i=0;i<2;i++) {
			new Thread(() ->{
				for(int j=0;j<25;j++) {
					//每个工人生产25个商品
					c.put(Thread.currentThread().getName()+" 生产的第"+j+"个商品");
				}
				
			},"生产者"+i).start();
		}

	}
	
}