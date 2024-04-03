package ejercicio3;

import java.util.Random;

public class Cliente implements Runnable {

	private Monitor monitor;
	private int id;
	private int tElegirServicio;
	private int tSerAtendido;
	private int maquina;
	private int mesa;
	Random random = new Random();

	public Cliente(Monitor monitor, int id) {
		this.monitor = monitor;
		this.id = id;
		this.tElegirServicio = random.nextInt(3000);
		this.tSerAtendido = random.nextInt(3000);	
	}

	@Override
	public void run() {
		try {
			monitor.imprimir("\n+++++++++++++++++++++++++++++++\nEl Cliente " + id + " ha entrado al banco\n+++++++++++++++++++++++++++++++\n",0);
			try {
				 maquina = monitor.asignarMaquina();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			monitor.imprimir("** Cliente " + id + " ha sido asignado la máquina: " + maquina + "\n",1);
			Thread.sleep(tElegirServicio);
			monitor.imprimir("El Cliente " + id + " ha dejado la maquina " + maquina + "\n",0);
			monitor.dejarMaquina(maquina);
			
			mesa = monitor.asignarMesa(tSerAtendido);
			monitor.imprimir("** Cliente " + id + " ha sido asignado la mesa: " + mesa + "\n",1);
			monitor.imprimir("--------------------------------------------------------------\n"
					+ "Cliente " + id + " ha solicitado su servicio en la máquina: " + maquina + 
					"\nTiempo en solicitar el servicio: " + tElegirServicio + 
					"\nSerá atendido en la mesa: " + mesa + 
					"\nTiempo en la mesa: " + tSerAtendido +
					"\nTiempo de espera en la mesa0: " + monitor.getTiemposEsperaMesas()[0] + ", mesa1: " + monitor.getTiemposEsperaMesas()[1] + ", mesa2: " + monitor.getTiemposEsperaMesas()[2] 
							+ ", mesa3: " + monitor.getTiemposEsperaMesas()[3]+
					"\n--------------------------------------------------------------\n",0);
			
			monitor.sentarMesa(mesa);
			monitor.imprimir("** Cliente " + id + " se ha sentado en la mesa " + mesa + "\n",1);
			Thread.sleep(tSerAtendido);
			monitor.imprimir("El Cliente " + id + " ha dejado la mesa " + mesa,0);
			monitor.dejarMesa(mesa,tSerAtendido);

			monitor.imprimir("\n--------------------------------\nEl Cliente " + id + " ha salido del banco\n--------------------------------\n", 1);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}
}
