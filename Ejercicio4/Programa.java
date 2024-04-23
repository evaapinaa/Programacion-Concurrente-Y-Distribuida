package Ejercicio4;

import messagepassing.*;

public class Programa {

	static class Controlador extends Thread {
		private MailBox pEnviar, pRespuesta, pCajaA, pCajaB, pLiberar, pImprimir;
		private boolean cajaALibre;
		private boolean cajaBLibre;
		private Selector s;
		private volatile boolean fin = false;

		public void setFin(boolean fin) {
			this.fin = fin;
		}

		public Controlador(MailBox pEnviar, MailBox pRespuesta, MailBox pCajaA, MailBox pCajaB, MailBox pLiberar,
				MailBox pImprimir) {
			this.s = new Selector();
			this.pEnviar = pEnviar;
			this.pRespuesta = pRespuesta;
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
			s.addSelectable(pRespuesta, false);

		}

		public void run() {

			while (!fin) {
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

					String respuesta = id + "," + tiempoPago * 100 + "," + cajaAsignada;
					pRespuesta.send(respuesta);

					break;

				case 2: // ponerse en caja A
					Object tiempoA = pCajaA.receive();
					cajaALibre = false;
					try {
						Thread.sleep((int) tiempoA); // Simula la compra
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

					break;

				case 3: // ponerse en caja B
					Object tiempoB = pCajaB.receive();
					cajaBLibre = false;
					try {
						Thread.sleep((int) tiempoB); // Simula la compra
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

					break;
				case 4: // liberar
					Object caja = pLiberar.receive();
					if (caja.equals("A")) {
						cajaALibre = true;
					} else
						cajaBLibre = true;

					break;
				case 5: // imprimir
					System.out.println(pImprimir.receive());
					break;
					
				}
			}

		}
	}

	// CONSTANTES, VARIABLES
	public static final int NUM_CLIENTES = 30;
	public static Thread Clientes[] = new Thread[NUM_CLIENTES];

	public static void main(String[] args) {

		MailBox pEnviar = new MailBox();
		MailBox pRespuesta = new MailBox();
		MailBox pCajaA = new MailBox();
		MailBox pCajaB = new MailBox();
		MailBox pLiberar = new MailBox();
		MailBox pImprimir = new MailBox();
		Controlador controlador = new Controlador(pEnviar, pRespuesta, pCajaA, pCajaB, pLiberar, pImprimir);
		Thread controladorH = new Thread(controlador);
		controladorH.start();

		for (int i = 0; i < NUM_CLIENTES; i++) {
			Clientes[i] = new Thread(new Cliente(i, pEnviar, pRespuesta, pCajaA, pCajaB, pLiberar, pImprimir));
			Clientes[i].start();
		}

		for (int i = 0; i < NUM_CLIENTES; i++) {
			try {
				Clientes[i].join();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		controlador.setFin(true);
		
		
		try {
			controladorH.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		
		controladorH.interrupt();

		System.out.println("Fin del programa principal");
		
		
	}

}