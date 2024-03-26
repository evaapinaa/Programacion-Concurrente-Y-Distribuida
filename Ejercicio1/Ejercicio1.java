package Ejercicio1;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;

public class Ejercicio1 {
	
	public static final int N_CONSUMIDORES = 10;
	public static final int LONGITUD_ARRAY = 110;
	public static Random random = new Random();
	public static int[] array = new int[LONGITUD_ARRAY];
	public static ReentrantLock lock = new ReentrantLock();
	
	public static void main(String[] args) {
		
		// GENERADOR
		for (int i = 0; i < LONGITUD_ARRAY; i+=11) {
        	for(int j = 0; j < 6; j++) {
        		int indice = i + j * 2;
        		array[indice] = random.nextInt(20) + 1;
        		if(indice+1 != LONGITUD_ARRAY) {
        			array[indice + 1] = random.nextInt(3) + 1;
        		}
        	}
        }
		
		lock.lock();
		try {
		    System.out.println("Array a operar : " + Arrays.toString(array));
		} finally {
		    lock.unlock();
		}
		
		int[] resultados = new int[N_CONSUMIDORES];
		
		Thread[] hConsumidores = new Thread[N_CONSUMIDORES];
		
		for (int i = 0; i < N_CONSUMIDORES; i++) {
            hConsumidores[i] = new Thread(new ProcesoConsumidor(i, array, resultados, lock));
            hConsumidores[i].start();
        }
		
		for (int i = 0; i < N_CONSUMIDORES; i++) {
            try {
                hConsumidores[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
		
		ProcesoSumador pSumador = new ProcesoSumador(resultados);
		Thread hSumador = new Thread(pSumador);
		hSumador.start();
		
		try {
			hSumador.join();
		} catch (InterruptedException e) {}
		
		System.out.println("Fin del hilo principal.");
		
	}

}
