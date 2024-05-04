package Ejercicio3;

import java.util.Random;

/**
 * Esta clase representa un cliente que realiza diversas operaciones en un
 * banco. Cada cliente es un hilo que simula la elección de un servicio en una
 * máquina y luego espera ser atendido en una de las mesas disponibles. El
 * cliente selecciona una máquina, espera un tiempo aleatorio que simula la
 * elección de un servicio, y luego procede a colocarse en la cola de la mesa
 * con el menor tiempo de espera estimado.
 */

public class Cliente implements Runnable {

	private Monitor monitor;
	private int id;
	private int tElegirServicio;
	private int tSerAtendido;
	private int maquina;
	private int mesa;
	Random random = new Random();

	/**
	 * Constructor de la clase Cliente.
	 * 
	 * @param monitor Monitor compartido por los clientes.
	 * @param id      Identificador del cliente.
	 */
	public Cliente(Monitor monitor, int id) {
		this.monitor = monitor;
		this.id = id;
		this.tElegirServicio = random.nextInt(3000);
		this.tSerAtendido = random.nextInt(3000);
	}

	/**
	 * Define el comportamiento del cliente en el banco.  
	 * 1. Coge la primera máquina disponible. 
	 * 2. Simula el tiempo que tarda en seleccionar el servicio. 
	 * 3. Se libera la máquina y se dirige a una mesa. 
	 * 4. Se asigna a la mesa con el menor tiempo de espera,
	 * espera  a ser atendido, y luego se libera la mesa para
	 * que otros clientes puedan usarla. También se imprime por consola 
	 * el flujo de su servicio.
	 */

	@Override
	public void run() {
		try {
			monitor.imprimir("\n+++++++++++++++++++++++++++++++\nEl Cliente " + id
					+ " ha entrado al banco\n+++++++++++++++++++++++++++++++\n", 0);
			try {
				maquina = monitor.asignarMaquina();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			monitor.imprimir("** Cliente " + id + " ha sido asignado la máquina: " + maquina + "\n", 1);
			Thread.sleep(tElegirServicio);
			monitor.imprimir("El Cliente " + id + " ha dejado la maquina " + maquina + "\n", 0);
			monitor.dejarMaquina(maquina);

			mesa = monitor.asignarMesa(tSerAtendido);
			monitor.imprimir("** Cliente " + id + " ha sido asignado la mesa: " + mesa + "\n", 1);
			monitor.imprimir("--------------------------------------------------------------\n" + "Cliente " + id
					+ " ha solicitado su servicio en la máquina: " + maquina + "\nTiempo en solicitar el servicio: "
					+ tElegirServicio + "\nSerá atendido en la mesa: " + mesa + "\nTiempo en la mesa: " + tSerAtendido
					+ "\nTiempo de espera en la mesa0: " + monitor.getTiemposEsperaMesas()[0] + ", mesa1: "
					+ monitor.getTiemposEsperaMesas()[1] + ", mesa2: " + monitor.getTiemposEsperaMesas()[2]
					+ ", mesa3: " + monitor.getTiemposEsperaMesas()[3]
					+ "\n--------------------------------------------------------------\n", 0);

			monitor.sentarMesa(mesa);
			monitor.imprimir("** Cliente " + id + " se ha sentado en la mesa " + mesa + "\n", 1);
			Thread.sleep(tSerAtendido);
			monitor.imprimir("El Cliente " + id + " ha dejado la mesa " + mesa, 0);
			monitor.dejarMesa(mesa, tSerAtendido);

			monitor.imprimir("\n--------------------------------\nEl Cliente " + id
					+ " ha salido del banco\n--------------------------------\n", 1);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}
}
