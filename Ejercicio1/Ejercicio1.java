package Ejercicio1;

import java.util.Arrays;

public class Ejercicio1 {
	static final int N_CONSUMIDORES = 10;
	public static void main(String[] args) {
		
		ProcesoGenerador pGenerador =  new ProcesoGenerador();
		Thread hGenerador= new Thread(pGenerador);
		
		hGenerador.start();
		System.out.println("Array a operar : " + Arrays.toString(pGenerador.getArray()));
		try {
			hGenerador.join();
		} catch(InterruptedException e) {
			e.printStackTrace();
		}
		
		int[] resultados = new int[N_CONSUMIDORES];
		
		Thread[] hConsumidores = new Thread[N_CONSUMIDORES];
		
		for (int i = 0; i < N_CONSUMIDORES; i++) {
            hConsumidores[i] = new Thread(new ProcesoConsumidor(i, pGenerador.getArray(), resultados));
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
		
	}

}
