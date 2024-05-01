package Ejercicio1;

import java.util.concurrent.locks.ReentrantLock;

/**
 * Hilo que se encarga de realizar las operaciones de un bloque de 11 elementos
 * de un array.
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
     * @return Identificador del hilo.
	 */
	public int getId() {
		return id;
	}
	
	/**
	 * Getter del array de elementos a procesar.
	 * @return Array de elementos a procesar.
	 */
	public int[] getArray() {
		return array;
	}

	/**
	 * Getter del array de resultados de las operaciones.
	 * @return Array de resultados de las operaciones.
	 */
	public int[] getResultados() {
		return resultados;
	}

	/**
	 * Constructor de la clase.
	 * 
	 * @param id         Identificador del hilo.
	 * @param array      Array de elementos a procesar.
	 * @param resultados Array de resultados de las operaciones.
	 * @param lock       Cerrojo para controlar la exclusión mutua.
	 */
	public ProcesoConsumidor(int id, int[] array, int[] resultados, ReentrantLock lock) {
		super();
		this.id = id;
		this.array = array;
		this.resultados = resultados;
		this.lock = lock;
	}

	/**
	 * Método run del hilo. Realiza las operaciones de un bloque de 11 elementos del
	 * array.
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
