package mytest;

import java.util.LinkedList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * �����⣺дһ���̶�����ͬ��������ӵ��put��get�������Լ�getCount������
 * �ܹ�֧��2���������߳��Լ�10���������̵߳���������
 * 
 * ʹ��lock��conditionʵ��
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
		//����һ��Ҫ��while,��Ϊ�����if,��������notifyAll��������е��̣߳������ѵ��̶߳������ٽ����ж��Ƿ��Ѿ����ˣ�ֱ����������ʹ���������
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
			//֪ͨ�����߽�������---������notifyAll
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
			//֪ͨ�����߽�������
			producer.notifyAll();
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			lock.unlock();
		}
		return t;
	}
	//ģ�������ߺ��������߳�
	public static void main(String[] args) {
		ProAndCom1<String> c = new ProAndCom1<>();
		
		//�����������ߵȴ�����
		for(int i=0;i<10;i++) {
			new Thread(() ->{
				for(int j=0;j<5;j++) {
					//ÿ��������5����Ʒ
					System.out.println(Thread.currentThread().getName()+" ������ "+c.get());
				}
				
			},"������"+i).start();
		}
		
		try {
			TimeUnit.SECONDS.sleep(2);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		//�����������ߵȴ�����
		for(int i=0;i<2;i++) {
			new Thread(() ->{
				for(int j=0;j<25;j++) {
					//ÿ����������25����Ʒ
					c.put(Thread.currentThread().getName()+" �����ĵ�"+j+"����Ʒ");
				}
				
			},"������"+i).start();
		}

	}
	
}