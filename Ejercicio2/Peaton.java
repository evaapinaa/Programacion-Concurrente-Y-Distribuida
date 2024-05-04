package Ejercicio2;

/**
 * Hilo que representa a un peatón que cruza el paso de peatones regulado por
 * semáforos. El peatón intenta cruzar siguiendo las señales del semáforo
 * correspondiente, y utiliza mecanismos de sincronización para asegurarse de
 * que el cruce es seguro y que respeta el turno actual del semáforo.
 */
public class Peaton implements Runnable {
	private final int id;

	/**
	 * Inicializa un peatón con un identificador único.
	 * 
	 * @param id Identificador del peatón.
	 */
	public Peaton(int id) {
		this.id = id;
	}

	/**
	 * Simula el comportamiento de cruzar cuando es el turno de los peatones.
	 * Utiliza semáforos para coordinar el cruce y para asegurar que no hay
	 * vehículos cruzando simultáneamente. Los peatones cruzan en grupos de 10
	 * peatones, y esperan su turno si hay menos de 10 peatones cruzando o si no es
	 * su turno. Una vez que cruzan, esperan un tiempo antes de intentar cruzar de
	 * nuevo.
	 */
	@Override
	public void run() {
		try {
			while (true) {

				CruceSemaforos.mutex.acquire();
				// No se puede cruzar, incrementar el número de peatones esperando
				if (CruceSemaforos.peatonesPasando >= 10 || CruceSemaforos.turno != 2
						|| CruceSemaforos.vehiculosNSpasando > 0 || CruceSemaforos.vehiculosEOpasando > 0) {
					CruceSemaforos.peatonesEsperando++;
					CruceSemaforos.mutex.release();
					CruceSemaforos.semaforoPeatones.acquire();

					CruceSemaforos.peatonesEsperando--;

				}
				// Entrando en la sección crítica (cruce)
				CruceSemaforos.peatonesPasando++;
				CruceSemaforos.pantalla.acquire();
				System.out.println("Peatón " + id + " cruzando");
				CruceSemaforos.pantalla.release();

				// Si es el turno de los peatones y hay peatones esperando a cruzar y no hay
				// vehículos cruzando y no hay 10 peatones cruzando simultáneamente se libera el
				// semáforo de peatones
				if (CruceSemaforos.turno == 2 && CruceSemaforos.peatonesEsperando > 0
						&& CruceSemaforos.vehiculosEOpasando == 0 && CruceSemaforos.peatonesPasando < 10) {
					CruceSemaforos.semaforoPeatones.release();
				} else {
					CruceSemaforos.mutex.release();

				}
				Thread.sleep(CruceSemaforos.TIEMPO_CRUCE_PEATON); // Tiempo para cruzar el paso de peatones
				CruceSemaforos.mutex.acquire();
				CruceSemaforos.peatonesPasando--;
				CruceSemaforos.pantalla.acquire();
				System.err.println("** Peatón " + id + " ha cruzado");
				CruceSemaforos.pantalla.release();

				// Si es el turno de los vehículos NS y hay vehículosNS esperando y no hay
				// peatones cruzando se libera el semáforo NS
				if (CruceSemaforos.turno == 0 && CruceSemaforos.peatonesPasando == 0
						&& CruceSemaforos.vehiculosNSesperando > 0) {
					CruceSemaforos.semaforoNS.release();
				}

				// Si es el turno de los peatones y no hay vehículos cruzando y no hay 10
				// peatones cruzando simultáneamente se libera el semáforo de peatones
				else if (CruceSemaforos.turno == 2 && CruceSemaforos.peatonesPasando < 10
						&& CruceSemaforos.vehiculosEOpasando == 0) {
					CruceSemaforos.semaforoPeatones.release();
				}

				else {
					CruceSemaforos.mutex.release();
				}

				Thread.sleep(CruceSemaforos.TIEMPO_ESPERA_PEATON); // Espera para intentar cruzar de nuevo
			}
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}
}
