package Ejercicio1;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Clase del programa que realiza operaciones sobre un array.
 */
public class Ejercicio1 {

	public static final int N_CONSUMIDORES = 10;
	public static final int LONGITUD_ARRAY = 110;
	public static Random random = new Random();
	public static int[] array = new int[LONGITUD_ARRAY];
	public static ReentrantLock lock = new ReentrantLock();

	/**
	 * Programa principal.
	 * @param args Argumentos del programa.
	 */
	public static void main(String[] args) {

		/**
		 * Inicializamos el array con 110 elementos. Cada 11 elementos, 6 de ellos
		 * tendrán un valor aleatorio entre 1 y 20 y los 5 restantes tendrán un valor
		 * aleatorio entre 1 y 3.
		 * 
		 */
		for (int i = 0; i < LONGITUD_ARRAY; i += 11) {
			for (int j = 0; j < 6; j++) {
				int indice = i + j * 2;
				array[indice] = random.nextInt(20) + 1;
				if (indice + 1 != LONGITUD_ARRAY) {
					array[indice + 1] = random.nextInt(3) + 1;
				}
			}
		}

		/**
		 * Mostramos el array por pantalla.
		 */
		lock.lock();
		try {
			System.out.println("Array a operar : " + Arrays.toString(array));
		} finally {
			lock.unlock();
		}

		/**
		 * Creamos los hilos consumidores y los iniciamos.
		 */
		int[] resultados = new int[N_CONSUMIDORES];

		Thread[] hConsumidores = new Thread[N_CONSUMIDORES];

		for (int i = 0; i < N_CONSUMIDORES; i++) {
			hConsumidores[i] = new Thread(new ProcesoConsumidor(i, array, resultados, lock));
			hConsumidores[i].start();
		}

		/**
		 * Esperamos a que terminen los hilos consumidores.
		 */
		for (int i = 0; i < N_CONSUMIDORES; i++) {
			try {
				hConsumidores[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		/**
		 * Creamos el hilo sumador y lo iniciamos.
		 */
		ProcesoSumador pSumador = new ProcesoSumador(resultados);
		Thread hSumador = new Thread(pSumador);
		hSumador.start();

		/**
		 * Esperamos a que termine el hilo sumador.
		 */
		try {
			hSumador.join();
		} catch (InterruptedException e) {
		}

		System.out.println("Fin del hilo principal.");

	}

}
