package Ejercicio2;

/**
 * Hilo encargado de cambiar el turno de los semáforos 
 * en el cruce cada 5 segundos. 
 * turno == 0 -> Vehículos Norte-Sur
 * turno == 1 -> Vehículos Este-Oeste
 * turno == 2 -> Peatones
 * 
 */
public class CambiaTurno extends Thread {

	/**
	 * Bucle que gestiona el cambio de los semáforos cada 5 segundos. Se
	 * encarga de mostrar por pantalla el turno actual de los semáforos.
	 */
	@Override
	public void run() {
		while (true) {
			try {
				if (CruceSemaforos.turno == 0) {
					CruceSemaforos.pantalla.acquire();
					System.err.println("----------Turno de los vehículos Norte-Sur----------");
					CruceSemaforos.pantalla.release();
				} else if (CruceSemaforos.turno == 1) {
					CruceSemaforos.pantalla.acquire();
					System.err.println("----------Turno de los vehículos Este-Oeste----------");
					CruceSemaforos.pantalla.release();
				} else if (CruceSemaforos.turno == 2) {
					CruceSemaforos.pantalla.acquire();
					System.err.println("----------Turno de los peatones----------");
					CruceSemaforos.pantalla.release();
				}

				// Esperar los 5 segundos que tiene que estar encendido el semáforo
				Thread.sleep(CruceSemaforos.TEMPO_ESPERA_SEMAFORO);

				// Cambiar el turno
				CruceSemaforos.turno = (CruceSemaforos.turno + 1) % 3;

			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}
	}

}
