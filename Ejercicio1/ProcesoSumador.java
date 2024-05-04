package Ejercicio1;

/**
 * Hilo dedicado a sumar todos los resultados generados por los hilos
 * consumidores. Accede al array de resultados, suma todos sus elementos y
 * muestra el resultado total. Este se inicia después de que todos los hilos
 * consumidores hayan terminado.
 */

public class ProcesoSumador extends Thread {

	private final int[] resultados;

	/**
	 * Constructor de la clase.
	 * 
	 * @param resultados Array de resultados de las operaciones.
	 */
	public ProcesoSumador(int[] resultados) {
		this.resultados = resultados;
	}

	/**
	 * Realiza la suma de los resultados de las operaciones.
	 */
	@Override
	public void run() {
		int suma = 0;
		for (int resultado : resultados) {
			suma += resultado;
		}
		System.out.println("Resultado: " + suma);
	}
}
