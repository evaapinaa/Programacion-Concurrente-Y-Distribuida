package Ejercicio2;

import java.util.concurrent.Semaphore;

/**
 * Clase principal que simula el cruce de semáforos para peatones y vehículos.
 * Incluye la inicialización de los semáforos y de las variables necesarias, así
 * como la creación de los hilos de los vehículos y peatones.
 */
public class CruceSemaforos {

	// Definir constantes, semáforos y variables globales
	
	/**
	 * Constantes tiempo que tarda un vehículo en cruzar el cruce
	 */
	public static int TIEMPO_CRUCE_VEHICULO = 500;
	/**
	 * Constantes tiempo que tarda un peatón en cruzar el cruce
	 */
	public static int TIEMPO_CRUCE_PEATON = 3000;
	/**
	 * Constantes tiempo que espera el semáforo en cambiar de turno
	 */
	public static int TEMPO_ESPERA_SEMAFORO = 5000;
	/**
	 * Constantes tiempo que espera un vehículo antes de intentar cruzar de nuevo
	 */
	public static int TIEMPO_ESPERA_VEHICULO = 7000;
	/**
	 * Constantes tiempo que espera un peatón antes de intentar cruzar de nuevo
	 */
	public static int TIEMPO_ESPERA_PEATON = 8000;
	
	/**
	 * Semáforo que controla el paso de los vehículos de Norte a Sur
	 */
	public static Semaphore semaforoNS = new Semaphore(0);
	/**
	 * Semáforo que controla el paso de los vehículos de Este a Oeste
	 */
	public static Semaphore semaforoEO = new Semaphore(0);
	/**
	 * Semáforo que controla el paso de los peatones
	 */
	public static Semaphore semaforoPeatones = new Semaphore(0);
	/**
	 * Semáforo que controla el acceso a las variables compartidas
	 */
	public static Semaphore mutex = new Semaphore(1);
	/**
	 * Semáforo que controla la impresión por pantalla
	 */
	public static Semaphore pantalla = new Semaphore(1);

	/**
	 * Variable que controla el número de vehículos Norte-Sur que están pasando
	 */
	public static int vehiculosNSpasando = 0;
	/**
	 * Variable que controla el número de vehículos Este-Oeste que están pasando
	 */
	public static int vehiculosEOpasando = 0;
	/**
	 * Variable que controla el número de peatones que están pasando
	 */
	public static int peatonesPasando = 0;
	/**
	 * Variable que controla el número de vehículos Norte-Sur que están esperando
	 */
	public static int vehiculosNSesperando = 0;
	/**
	 * Variable que controla el número de vehículos Este-Oeste que están esperando
	 */
	public static int vehiculosEOesperando = 0;
	/**
	 * Variable que controla el número de peatones que están esperando
	 */
	public static int peatonesEsperando = 0;

	/**
	 * Variable que controla el turno de los semáforos
	 */
	public static int turno = 0; // 0 = NS, 1 = EO, 2 = Peatones

	/**
	 * Programa principal que inicia los hilos de control de cambio de turno y los
	 * hilos de peatones y vehículos.
	 * 
	 * @param args Argumentos de la línea de comandos.
	 */
	public static void main(String[] args) {

		CambiaTurno cambiaTurno = new CambiaTurno();
		cambiaTurno.start();
		
		// iniciamos con vehiculosNS (dirección = 0)
		for (int i = 0; i < 50; i++) {
			new Thread(new Vehiculo(i, 0)).start();
		}

		for (int i = 0; i < 100; i++) {
			new Thread(new Peaton(i)).start();
		}
		

	}

}