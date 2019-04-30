package mytest;

import java.util.LinkedList;
import java.util.concurrent.TimeUnit;

/**
 * �����⣺дһ���̶�����ͬ��������ӵ��put��get�������Լ�getCount������
 * �ܹ�֧��2���������߳��Լ�10���������̵߳���������
 * 
 * ʹ��wait��notify/notifyAll��ʵ��
 * 
 * @author mashibing
 */
public class ProAndCom1<T> {
	final private LinkedList<T> list = new LinkedList<>();
	final private int Max = 10;
	private int count = 0;
	
	synchronized public void put(T t) {
		//����һ��Ҫ��while,��Ϊ�����if,��������notifyAll��������е��̣߳������ѵ��̶߳������ٽ����ж��Ƿ��Ѿ����ˣ�ֱ����������ʹ���������
		while(count==Max) {
			try {
				this.wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		list.add(t);
		count++;
		//֪ͨ�����߽�������---������notifyAll
		this.notifyAll();
	}
	
	synchronized public T get() {
		while(count==0) {
			try {
				this.wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		count--;
		//֪ͨ�����߽�������
		this.notifyAll();
		return 	list.removeFirst();
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
