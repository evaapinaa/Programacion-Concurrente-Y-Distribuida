package Ejercicio1;

/**
 * Hilo que se encarga de realizar las operaciones de un bloque de 11 elementos
 * de un array.
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
	 * Método run del hilo. Realiza la suma de los resultados de las operaciones.
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
