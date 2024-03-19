package Ejercicio1;

public class ProcesoSumador extends Thread {
	private final int[] resultados;
	
	public ProcesoSumador(int[] resultados) {
		this.resultados = resultados;
	}
	
	@Override
	public void run() {
		int suma = 0;
		for(int resultado : resultados) {
			suma += resultado;
		}
		System.out.println("Resultado: " + suma);
	}
}
