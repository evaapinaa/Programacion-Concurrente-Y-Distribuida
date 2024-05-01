package Ejercicio4;

import messagepassing.*;

/**
 * Clase del programa principal.
 *
 */
public class Programa {

	/**
	 * Clase que simula el hilo controlador.
	 *
	 */
	static class Controlador extends Thread {
		private MailBox pEnviar, pCajaA, pCajaB, pLiberar, pImprimir;
		private boolean cajaALibre;
		private boolean cajaBLibre;
		private Selector s;

		/**
		 * Constructor de la clase.
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
		 * Método run del hilo controlador.
		 */
		public void run() {
			while (true) {
				pCajaA.setGuardValue(cajaALibre == true);
				pCajaB.setGuardValue(cajaBLibre == true);
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
	public static final int NUM_CLIENTES = 30;
	public static Thread Clientes[] = new Thread[NUM_CLIENTES];
	public static MailBox buzonesClientes[] = new MailBox[30];

	/**
	 * Programa principal.
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