package Ejercicio4;

import messagepassing.*;

/**
 * Simula el comportamiento de un cliente en un supermercado con dos cajas. Cada
 * cliente realiza 5 rondas, en las que realiza compras, solicita asignación de
 * caja, realiza pagos y luego libera la caja asignada. La asignación de caja
 * depende del tiempo estimado de pago calculado aleatoriamente: los tiempos más
 * largos son asignados a la caja más rápida (A). La comunicación con el
 * controlador de cajas se realiza mediante buzones de mensajes, haciendo así
 * una sincronización basada en el paso de mensajes asíncrono.
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
	 * Retorna la caja asignada al cliente.
	 * 
	 * @return cadena con el identificador de la caja.
	 */
	public String getCajaAsignada() {
		return cajaAsignada;
	}

	/**
	 * Establece la caja asignada al cliente.
	 * 
	 * @param cajaAsignada cadena con el identificador de la caja.
	 */
	public void setCajaAsignada(String cajaAsignada) {
		this.cajaAsignada = cajaAsignada;
	}

	/**
	 * Retorna el tiempo de pago asignado al cliente.
	 * 
	 * @return Tiempo de pago.
	 */
	public int getTiempoPago() {
		return tiempoPago;
	}

	/**
	 * Establece el tiempo de pago del cliente.
	 * 
	 * @param tiempoPago Tiempo de pago.
	 */
	public void setTiempoPago(int tiempoPago) {
		this.tiempoPago = tiempoPago;
	}

	/**
	 * Constructor de la clase Cliente. Inicializa un nuevo cliente con un
	 * identificador único y diversos buzones. Estos últimos los utilizará para
	 * enviar y recibir mensajes con el coordinador de cajas. Serán relacionados con
	 * la asignación de cajas, la liberación de cajas, y la impresión de mensajes
	 * informativos.
	 * 
	 * @param id             Identificador del cliente.
	 * @param buzonEnvio     Buzón de envío.
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
	 * Realiza las acciones del cliente durante su visita al supermercado. En cada
	 * una de las cinco iteraciones, el cliente: realiza una compra, solicita y
	 * espera la asignación de una caja, paga en la caja asignada, y finalmente
	 * libera la caja. Las acciones de solicitud, pago y liberación son coordinadas
	 * a través del intercambio de mensajes con el controlador utilizando buzones
	 * específicos. Además, se imprimime por pantalla la evolución del cliente.
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