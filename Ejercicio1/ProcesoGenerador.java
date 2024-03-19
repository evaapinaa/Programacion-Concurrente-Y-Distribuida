package Ejercicio1;

import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;

public class ProcesoGenerador implements Runnable{
	
	public static final int LONGITUD_ARRAY = 110;
	
	private Random random = new Random();
	private int[] array = new int[LONGITUD_ARRAY];
	private ReentrantLock l = new ReentrantLock();
	
	public int[] getArray() {
		return array;
	}
	
	
	@Override
	public void run() {
		try {
			l.lock();
	        for (int i = 0; i < LONGITUD_ARRAY; i+=11) {
	        	for(int j = 0; j < 6; j++) {
	        		int indice = i + j * 2;
	        		array[indice] = random.nextInt(50) + 1;
	        		if(indice+1 != LONGITUD_ARRAY) {
	        			array[indice + 1] = random.nextInt(3) + 1;
	        		}
	        	}
	        }
			
		} finally {
				l.unlock();
			}
		}
		
		
	
}
