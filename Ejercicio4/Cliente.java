package Ejercicio4;

import messagepassing.*;

/**
 * Clase que simula un cliente.
 *
 */
public class Cliente implements Runnable {

	private int id;
	private MailBox buzonEnvio;
	private MailBox buzonRespuesta;
	private MailBox buzonCajaA;
	private MailBox buzonCajaB;
	private MailBox buzonLiberar;
	private MailBox buzonImprimir;
	private String cajaAsignada;
	private int tiempoCompra;
	private int tiempoPago;

	/**
	 * Getter de la caja asignada.
	 * 
	 * @return Identificador de la caja.
	 */
	public String getCajaAsignada() {
		return cajaAsignada;
	}

	/**
	 * Setter de la caja asignada.
	 * 
	 * @param cajaAsignada Identificador de la caja.
	 */
	public void setCajaAsignada(String cajaAsignada) {
		this.cajaAsignada = cajaAsignada;
	}

	/**
	 * Getter del tiempo de pago.
	 * 
	 * @return Tiempo de pago.
	 */
	public int getTiempoPago() {
		return tiempoPago;
	}

	/**
	 * Setter del tiempo de pago.
	 * 
	 * @param tiempoPago Tiempo de pago.
	 */
	public void setTiempoPago(int tiempoPago) {
		this.tiempoPago = tiempoPago;
	}

	/**
	 * Constructor de la clase.
	 * 
	 * @param id             Identificador del cliente.
	 * @param buzon          Buzón de envío.
	 * @param buzonRespuesta Buzón de respuesta.
	 * @param buzonCajaA     Buzón de la caja A.
	 * @param buzonCajaB     Buzón de la caja B.
	 * @param liberar        Buzón para liberar la caja.
	 * @param buzonImprimir  Buzón para imprimir.
	 */
	public Cliente(int id, MailBox buzonEnvio, MailBox buzonRespuesta, MailBox buzonCajaA, MailBox buzonCajaB,
			MailBox liberar, MailBox buzonImprimir) {
		this.id = id;
		this.buzonEnvio = buzonEnvio;
		this.buzonRespuesta = buzonRespuesta;
		this.buzonCajaA = buzonCajaA;
		this.buzonCajaB = buzonCajaB;
		this.buzonLiberar = liberar;
		this.buzonImprimir = buzonImprimir;
	}

	/**
	 * Método run del hilo. Realiza las operaciones de un cliente.
	 */
	@Override
	public void run() {
		for (int i = 0; i < 5; i++) {

			tiempoCompra = (int) (Math.random() * 10) + 1;

			try {
				Thread.sleep(tiempoCompra * 100); // Simula la compra
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			buzonEnvio.send(id);

			Object respuesta = buzonRespuesta.receive();
			String[] partes = respuesta.toString().split(",");
			this.setTiempoPago(Integer.parseInt(partes[0]));
			this.setCajaAsignada(partes[1]);

			if (cajaAsignada.equals("A"))
				buzonCajaA.send(id);
			else
				buzonCajaB.send(id);

			buzonRespuesta.receive();

			Object mensaje = "** Cliente " + id + " usando la caja " + this.getCajaAsignada();
			buzonImprimir.send(mensaje);

			try {
				Thread.sleep(tiempoPago * 100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			buzonLiberar.send(cajaAsignada);

			Object info = "\n--------------------------\n" + "Persona " + id + " ha usado la caja " + cajaAsignada
					+ "\nTiempo de pago = " + tiempoPago * 100 + "\nPersona " + id + " liberando la caja "
					+ cajaAsignada + "\n--------------------------\n";

			buzonImprimir.send(info);
			buzonRespuesta.receive();
		}
	}
}