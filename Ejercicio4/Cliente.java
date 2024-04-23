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
	
	public Cliente(int id, MailBox buzon, MailBox buzonRespuesta,MailBox buzonCajaA,MailBox buzonCajaB, MailBox liberar, MailBox buzonImprimir) {
		this.id = id;
		this.buzonEnvio = buzon;
		this.buzonRespuesta = buzonRespuesta;
		this.buzonCajaA = buzonCajaA;
		this.buzonCajaB = buzonCajaB;
		this.buzonLiberar = liberar;
		this.buzonImprimir = buzonImprimir;
		this.tiempoCompra = (int) (Math.random() * 10) + 1;
	}
	
	@Override
	public void run() {
		for (int i = 0; i < 5; i++) {
			
            try {
                Thread.sleep(tiempoCompra * 100); // Simula la compra
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            
			buzonEnvio.send(id);
			
			Object respuesta = buzonRespuesta.receive();
			String[] partes = respuesta.toString().split(",");
            int id = Integer.parseInt(partes[0]);
            this.setTiempoPago(Integer.parseInt(partes[1]));
            this.setCajaAsignada(partes[2]);
            
            if(cajaAsignada.equals("A")) buzonCajaA.send(tiempoPago);
            else buzonCajaB.send(tiempoPago);
            
            
            Object mensaje = "** Cliente " + id+ " usando la caja " + this.getCajaAsignada();
			buzonImprimir.send(mensaje);
			
			buzonLiberar.send(cajaAsignada);
			Object info = "\n--------------------------\n" + "Persona " + id + " ha usado la caja " + cajaAsignada +
                    "\nTiempo de pago = " + tiempoPago +
                    "\nThread.sleep(" + tiempoPago + ")" +
                    "\nPersona " + id + " liberando la caja " + cajaAsignada
                    + "\n--------------------------\n" ;
			
			buzonImprimir.send(info);
		}
	}
}