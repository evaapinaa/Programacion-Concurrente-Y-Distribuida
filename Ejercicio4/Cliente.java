package Ejercicio4;

import messagepassing.*;

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

	public String getCajaAsignada() {
		return cajaAsignada;
	}

	public void setCajaAsignada(String cajaAsignada) {
		this.cajaAsignada = cajaAsignada;
	}

	public int getTiempoPago() {
		return tiempoPago;
	}

	public void setTiempoPago(int tiempoPago) {
		this.tiempoPago = tiempoPago;
	}

	public Cliente(int id, MailBox buzon, MailBox buzonRespuesta, MailBox buzonCajaA, MailBox buzonCajaB,
			MailBox liberar, MailBox buzonImprimir) {
		this.id = id;
		this.buzonEnvio = buzon;
		this.buzonRespuesta = buzonRespuesta;
		this.buzonCajaA = buzonCajaA;
		this.buzonCajaB = buzonCajaB;
		this.buzonLiberar = liberar;
		this.buzonImprimir = buzonImprimir;
	}

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