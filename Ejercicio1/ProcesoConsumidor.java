package Ejercicio1;

import java.util.concurrent.locks.ReentrantLock;

public class ProcesoConsumidor implements Runnable {

	public static final int TAM_BLOQUE = 11;

	private int id;
	private int[] array;
	private int[] resultados;
	private ReentrantLock lock;

	public int getId() {
		return id;
	}

	public int[] getArray() {
		return array;
	}

	public int[] getResultados() {
		return resultados;
	}

	public ProcesoConsumidor(int id, int[] array, int[] resultados, ReentrantLock lock) {
		super();
		this.id = id;
		this.array = array;
		this.resultados = resultados;
		this.lock = lock;
	}

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
