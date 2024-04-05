package ejercicio3;

import java.util.Random;

public class Programa {
	
	// CONSTANTES
	public final static int NUM_CLIENTES = 50;
	public final static int NUM_MESAS = 4;
	public final static int NUM_MAQUINAS = 3;
	public final static Random random = new Random();
	// VARIABLES
	public static Thread clientesDentro[] = new Thread[NUM_CLIENTES];
	
	public static void main(String[] args) {
	
		Monitor monitor = new Monitor(NUM_MESAS, NUM_MAQUINAS);
		
		for(int i = 0; i < NUM_CLIENTES; i++) {
			clientesDentro[i] = new Thread(new Cliente(monitor,i));
		}
		
		for(int i = 0; i < NUM_CLIENTES; i++) {
			clientesDentro[i].start();
			try {
				Thread.sleep(random.nextInt(1000)); // entradas en tiempos diferentes
			} catch (InterruptedException e) {}
		}
		
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
