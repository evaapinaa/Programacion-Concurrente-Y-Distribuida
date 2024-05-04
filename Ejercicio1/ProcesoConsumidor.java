package Ejercicio1;

import java.util.concurrent.locks.ReentrantLock;

/**
 * Clase que representa un hilo consumidor encargado de procesar una sección del
 * array. Cada consumidor trabaja sobre un bloque fijo de 11 elementos,
 * realizando diversas operaciones y almacenando el resultado en un array
 * compartido de resultados. Utiliza un ReentrantLock para garantizar la
 * exclusión mutua al imprimir por pantalla.
 */

public class ProcesoConsumidor implements Runnable {

	/**
	 * Tamaño de bloque de elementos a procesar.
	 */
	public static final int TAM_BLOQUE = 11;

	private int id;
	private int[] array;
	private int[] resultados;
	private ReentrantLock lock;

	/**
	 * Getter del identificador del hilo.
	 * 
	 * @return Identificador del hilo.
	 */
	public int getId() {
		return id;
	}

	/**
	 * Getter del array de elementos a procesar.
	 * 
	 * @return Array de elementos a procesar.
	 */
	public int[] getArray() {
		return array;
	}

	/**
	 * Getter del array de resultados de las operaciones.
	 * 
	 * @return Array de resultados de las operaciones.
	 */
	public int[] getResultados() {
		return resultados;
	}

	/**
	 * Construye una instancia de ProcesoConsumidor asignando los recursos
	 * compartidos y el identificador único.
	 * 
	 * @param id         Identificador único del consumidor.
	 * @param array      Array compartido de números y operaciones.
	 * @param resultados Array compartido para almacenar los resultados de las
	 *                   operaciones de cada consumidor.
	 * @param lock       Cerrojo de ReentrantLock para controlar lla impresión por
	 *                   pantalla.
	 * 
	 */

	public ProcesoConsumidor(int id, int[] array, int[] resultados, ReentrantLock lock) {
		super();
		this.id = id;
		this.array = array;
		this.resultados = resultados;
		this.lock = lock;
	}

	/**
	 * Ejecuta la tarea del consumidor que consiste en procesar un bloque específico
	 * del array. Lee y realiza las operaciones matemáticas de su bloque asignado y
	 * almacena el resultado. Finalmente, imprime el resultado junto con su
	 * identificador de manera segura utilizando el cerrojo.
	 */

	@Override
	public void run() {
		int inicio = id * TAM_BLOQUE;
		int resultado = array[inicio];

		for (int i = inicio + 1; i < inicio + 10; i += 2) {
			int operacion = array[i];
			int numero = array[i + 1];

			switch (operacion) {
			case 1:
				resultado += numero;
				break;
			case 2:
				resultado -= numero;
				break;
			case 3:
				resultado *= numero;
				break;
			}
		}
		resultados[id] = resultado;
		try {
			lock.lock();
			System.out.println("ID: " + getId() + ", resultado: " + resultados[id]);
		} finally {
			lock.unlock();
		}
	}

}
