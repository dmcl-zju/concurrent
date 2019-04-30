package mytest;

import java.util.ArrayList;
import java.util.List;

/**
 * �����������⣺���Ա�����
 * ʵ��һ���������ṩ����������add��size
 * д�����̣߳��߳�1���10��Ԫ�ص������У��߳�2ʵ�ּ��Ԫ�صĸ�������������5��ʱ���߳�2������ʾ������
 * 
 * ��lists���volatile֮��t2�ܹ��ӵ�֪ͨ�����ǣ�t2�̵߳���ѭ�����˷�cpu�����������ѭ��������ô���أ�
 * 
 * ����ʹ��wait��notify������wait���ͷ�������notify�����ͷ���
 * ��Ҫע����ǣ��������ַ���������Ҫ��֤t2��ִ�У�Ҳ����������t2�����ſ���
 * 
 * �Ķ�����ĳ��򣬲�����������
 * ���Զ���������������size=5ʱt2�˳�������t1����ʱt2�Ž��յ�֪ͨ���˳�
 * ��������Ϊʲô��
 * 
 * notify֮��t1�����ͷ�����t2�˳���Ҳ����notify��֪ͨt1����ִ��
 * ����ͨ�Ź��̱ȽϷ���
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
		
		//�½�һ������䵱��������
		Object lock = new Object();
		
		new Thread(()->{
			System.out.println(Thread.currentThread().getName()+" ����");
			synchronized(lock) {
				if(c.size()!=5) {
					try {
						lock.wait();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
				System.out.println(Thread.currentThread().getName()+" ֹͣ");
				//֪ͨ���������
				lock.notifyAll();
				
			}
			
			
		},"�߳�2" ).start();;
		
		
		new Thread(()->{
			System.out.println(Thread.currentThread().getName()+" ����");
			synchronized(lock) {
				for(int i=0;i<10;i++) {
				
					c.add(i);
					System.out.println(Thread.currentThread().getName()+"��������"+i);
					if(c.size()==5) {
						lock.notifyAll();
						
						try {
							lock.wait();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					//�ȴ�100ms������
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
			}
			System.out.println(Thread.currentThread().getName()+" ֹͣ");
		},"�߳�1" ).start();
		
		
		
	}
}
