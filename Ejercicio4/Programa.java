package Ejercicio4;

import messagepassing.*;

/**
 * Programa principal que simula el proceso de compra y pago en un supermercado
 * con dos cajas (A y B). Inicializa un controlador, que funcionará como un
 * servidor para coordinar, y múltiples clientes. Utiliza buzones de mensajes
 * para la comunicación entre el controlador y los clientes, permitiendo una
 * ejecución sincronizada de los hilos.
 */

public class Programa {

	/**
	 * Gestiona la asignación de cajas a los clientes en un supermercado con dos
	 * cajas de pago. Usa paso de mensajes para recibir solicitudes de los clientes
	 * y asignarles cajas basándose en el tiempo estimado de pago (caja A para pagos
	 * más largos). También maneja la liberación de cajas y la impresión de
	 * mensajes. La sincronización del acceso a las cajas se controla a través de un
	 * selector.
	 */
	static class Controlador extends Thread {
		private MailBox pEnviar, pCajaA, pCajaB, pLiberar, pImprimir;
		private boolean cajaALibre;
		private boolean cajaBLibre;
		private Selector s;

		/**
		 * Constructor de la clase Controlador. Inicializa el controlador con buzones de
		 * mensajes específicos para manejar la asignación de cajas, liberación de cajas
		 * y acciones de impresión. Establece el estado inicial de las cajas como libres
		 * y configura un selector para gestionar los mensajes que provienen de los hilos.
		 * 
		 * @param pEnviar   Buzón de mensajes para enviar.
		 * @param pCajaA    Buzón de mensajes para la caja A.
		 * @param pCajaB    Buzón de mensajes para la caja B.
		 * @param pLiberar  Buzón de mensajes para liberar una caja.
		 * @param pImprimir Buzón de mensajes para imprimir.
		 */

		public Controlador(MailBox pEnviar, MailBox pCajaA, MailBox pCajaB, MailBox pLiberar, MailBox pImprimir) {
			this.s = new Selector();
			this.pEnviar = pEnviar;
			this.pLiberar = pLiberar;
			this.pCajaA = pCajaA;
			this.pCajaB = pCajaB;
			this.cajaALibre = true;
			this.cajaBLibre = true;
			this.pImprimir = pImprimir;
			s.addSelectable(pEnviar, false);
			s.addSelectable(pCajaA, false);
			s.addSelectable(pCajaB, false);
			s.addSelectable(pLiberar, false);
			s.addSelectable(pImprimir, false);

		}

		/**
		 * Procesa las solicitudes de los clientes y gestiona la
		 * asignación y liberación de cajas. Utiliza un selector para esperar y
		 * responder a los mensajes de diferentes buzones: 
		 * 1) Asignación de caja basada en el tiempo de pago
		 * 2) Manejo de clientes en la caja A
		 * 3) Manejo de clientes en la caja B
		 * 4) Liberación de la caja.
		 * 5) Impresión de mensajes. 
		 * 
		 */

		public void run() {
			while (true) {
				pCajaA.setGuardValue(cajaALibre == true); // se puede usar la caja A si está libre
				pCajaB.setGuardValue(cajaBLibre == true); // se puede usar la caja B si está libre
				switch (s.selectOrBlock()) {
				case 1: // peticion caja
					Object id = pEnviar.receive();
					String cajaAsignada;
					int tiempoPago = (int) (Math.random() * 10) + 1;
					if (tiempoPago >= 5) {
						cajaAsignada = "A";
					} else
						cajaAsignada = "B";

					Object mensajeImprimir = "** Cliente " + id + " asignado el tiempo: " + tiempoPago * 100
							+ " y la caja " + cajaAsignada;

					pImprimir.send(mensajeImprimir);

					String respuesta = tiempoPago + "," + cajaAsignada;
					buzonesClientes[(int) id].send(respuesta);

					break;

				case 2: // ponerse en caja A
					id = pCajaA.receive();
					cajaALibre = false;
					buzonesClientes[(int) id].send("ok");
					break;

				case 3: // ponerse en caja B
					id = pCajaB.receive();
					cajaBLibre = false;
					buzonesClientes[(int) id].send("ok");

					break;
				case 4: // liberar
					Object caja = pLiberar.receive();
					if (caja.equals("A")) {
						cajaALibre = true;
					} else
						cajaBLibre = true;

					break;
				case 5: // imprimir
					Object mensaje = pImprimir.receive();
					System.out.println(mensaje);
					break;
				}

			}
		}
	}

	// CONSTANTES, VARIABLES

	/**
	 * Número de clientes.
	 */
	public static final int NUM_CLIENTES = 30;

	/**
	 * Array de hilos clientes.
	 */
	public static Thread Clientes[] = new Thread[NUM_CLIENTES];

	/**
	 * Array de buzones de respuesta de cada cliente.
	 */
	public static MailBox buzonesClientes[] = new MailBox[30];

	/**
	 * Punto de inicio del programa principal.
	 * Crea y arranca un hilo para el controlador y un conjunto de
	 * hilos para los clientes. Configura buzones de mensajes para la 
	 * comunicación entre clientes y el controlador. Espera a que todos 
	 * los clientes completen sus acciones.
	 * 
	 * @param args Argumentos del programa.
	 */
	public static void main(String[] args) {

		MailBox pEnviar = new MailBox();
		MailBox pCajaA = new MailBox();
		MailBox pCajaB = new MailBox();
		MailBox pLiberar = new MailBox();
		MailBox pImprimir = new MailBox();

		Controlador controlador = new Controlador(pEnviar, pCajaA, pCajaB, pLiberar, pImprimir);
		controlador.start();

		for (int i = 0; i < NUM_CLIENTES; i++) {
			buzonesClientes[i] = new MailBox();
			Clientes[i] = new Thread(new Cliente(i, pEnviar, buzonesClientes[i], pCajaA, pCajaB, pLiberar, pImprimir));
		}

		for (int i = 0; i < NUM_CLIENTES; i++) {
			Clientes[i].start();
		}

		for (int i = 0; i < NUM_CLIENTES; i++) {
			try {
				Clientes[i].join();
				controlador.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}
}