package Ejercicio3;

import java.util.Random;

/**
 * Programa principal que simula el funcionamiento de un banco atendiendo a
 * múltiples clientes. Utiliza hilos para representar los clientes y un monitor
 * para gestionar el acceso concurrente a recursos compartidos, como son las
 * mesas y máquinas del banco. Cada cliente actúa de manera independiente y su
 * comportamiento está sincronizado mediante el uso de un Monitor.
 */

public class Programa {

	// CONSTANTES
	/**
	 * Número de clientes.
	 */
	public final static int NUM_CLIENTES = 50;
	/**
	 * Número de mesas.
	 */
	public final static int NUM_MESAS = 4;
	/**
	 * Número de máquinas.
	 */
	public final static int NUM_MAQUINAS = 3;
	/**
	 * Random para generar números aleatorios.
	 */
	public final static Random random = new Random();

	// VARIABLES
	/**
	 * Array de hilos clientes.
	 */
	public static Thread clientesDentro[] = new Thread[NUM_CLIENTES];

	/**
	 * Método principal de la simulación del banco. Inicializa los clientes y son
	 * gestionados desde que entran al banco, hasta que todos han sido atendidos.
	 * Los clientes son creados y gestionados como hilos independientes, y se les
	 * asigna un identificador único. Se simula una aletoriedad en la llegada de los
	 * clientes mediante un tiempo de espera random, antes de que cada cliente
	 * inicie. Finalmente, espera a que todos los clientes completen su ejecución.
	 * 
	 * @param args Argumentos de la línea de comandos, no utilizados en esta
	 *             simulación.
	 */
	public static void main(String[] args) {

		Monitor monitor = new Monitor(NUM_MESAS, NUM_MAQUINAS);

		// Crear clientes
		for (int i = 0; i < NUM_CLIENTES; i++) {
			clientesDentro[i] = new Thread(new Cliente(monitor, i));

		}

		// Iniciar clientes
		for (int i = 0; i < NUM_CLIENTES; i++) {
			clientesDentro[i].start();
			try {
				Thread.sleep(random.nextInt(1000)); // Simular entradas en tiempos diferentes
			} catch (InterruptedException e) {
			}
		}

		// Esperar a que todos los clientes sean atendidos
		for (Thread cliente : clientesDentro) {
			try {
				cliente.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		System.out.println("Todos los clientes han sido atendidos.");
	}

}
