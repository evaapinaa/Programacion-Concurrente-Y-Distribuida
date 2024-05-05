package Ejercicio2;

/**
 * Hilo encargado de cambiar el turno de los semáforos en el cruce cada 5
 * segundos. turno == 0 -> Vehículos Norte-Sur turno == 1 -> Vehículos
 * Este-Oeste turno == 2 -> Peatones. Si no hay vehículos o peatones cruzando en
 * el momento del cambio de turno, se cambia el turno. En caso contrario, el
 * encargado de cambiar el turno será el último vehículo o peatón en cruzar.
 * 
 */
public class CambiaTurno extends Thread {

	/**
	 * Bucle que gestiona el cambio de los semáforos cada 5 segundos. Se encarga de
	 * mostrar por pantalla el turno actual de los semáforos. Y de despertar el
	 * semáforo correspondiente.
	 */
	@Override
	public void run() {
		while (true) {
			try {

				CruceSemaforos.mutex.acquire();

				switch (CruceSemaforos.turno) {

				case 0: {
					// Si no hay vehículos Norte-Sur pasando y hay vehículos Norte-Sur esperando y
					// no hay peatones pasando, se cambia el turno
					if (CruceSemaforos.vehiculosNSpasando == 0 && CruceSemaforos.vehiculosNSesperando > 0
							&& CruceSemaforos.peatonesPasando == 0) {
						CruceSemaforos.semaforoNS.release();
						CruceSemaforos.pantalla.acquire();
						System.err.println("----------Turno de los vehículos Norte-Sur----------");
						CruceSemaforos.pantalla.release();
					} else {
						// Si no se cumple la condición anterior se libera el mutex para que los
						// peatones puedan seguir pasando
						CruceSemaforos.pantalla.acquire();
						System.err.println("----------Turno de los vehículos Norte-Sur----------");
						CruceSemaforos.pantalla.release();
						CruceSemaforos.mutex.release();
					}
					break;
				}
				case 1: {
					// Si no hay vehículos Este-Oeste pasando y hay vehículos Este-Oeste esperando y
					// no hay peatones pasando, se cambia el turno
					if (CruceSemaforos.vehiculosEOpasando == 0 && CruceSemaforos.vehiculosEOesperando > 0
							&& CruceSemaforos.vehiculosNSpasando == 0) {
						CruceSemaforos.semaforoEO.release();
						CruceSemaforos.pantalla.acquire();
						System.err.println("----------Turno de los vehículos Este-Oeste----------");
						CruceSemaforos.pantalla.release();
					} else {
						// Si no se cumple la condición anterior se libera el mutex para que los
						// vehículosNS puedan seguir pasando
						CruceSemaforos.pantalla.acquire();
						System.err.println("----------Turno de los vehículos Este-Oeste----------");
						CruceSemaforos.pantalla.release();
						CruceSemaforos.mutex.release();
					}
					break;
				}
				case 2: {
					// Si no hay peatones pasando y hay peatones esperando y no hay vehículos
					// pasando, se cambia el turno
					if (CruceSemaforos.peatonesPasando == 0 && CruceSemaforos.peatonesEsperando > 0
							&& CruceSemaforos.vehiculosEOpasando == 0) {
						CruceSemaforos.semaforoPeatones.release();
						CruceSemaforos.pantalla.acquire();
						System.err.println("----------Turno de los peatones----------");
						CruceSemaforos.pantalla.release();
					} else {
						// Si no se cumple la condición anterior se libera el mutex para que los
						// vehiculosEO puedan seguir pasando
						CruceSemaforos.pantalla.acquire();
						System.err.println("----------Turno de los peatones----------");
						CruceSemaforos.pantalla.release();
						CruceSemaforos.mutex.release();
					}
					break;
				}

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
