package Ejercicio2;

/**
 * Hilo que simula un vehículo intentando cruzar en un cruce controlado por
 * semáforos. Los vehículos van alternando de direccion (NS = 0 ò EO = 1). La
 * clase gestiona la sincronización del cruce mediante el uso de semáforos para
 * garantizar que solo los vehículos en la misma dirección crucen
 * simultáneamente, y coordina el cruce evitando conflictos con peatones y
 * vehículos que cruzan en la dirección opuesta.
 */
public class Vehiculo implements Runnable {
	private final int id;
	private int direccion; // 0 = Norte-Sur, 1 = Este-Oeste

	/**
	 * Constructor para inicializar un vehículo con un identificador único y una
	 * dirección inicial.
	 * 
	 * @param id        Identificador del vehículo, usado para trazar su progreso y
	 *                  manejar su comportamiento en el cruce.
	 * @param direccion Dirección inicial del vehículo, donde 0 = Norte-Sur y 1 =
	 *                  Este-Oeste.
	 */
	public Vehiculo(int id, int direccion) {
		this.id = id;
		this.direccion = direccion;
	}

	/**
	 * Simula el comportamiento del vehículo para intentar cruzar el cruce
	 * controlado por semáforos. Los vehículos esperan su turno según la dirección
	 * actual del semáforo y cruzan cuando es seguro hacerlo. Utiliza semáforos para
	 * coordinar el cruce y para asegurar que no hay peatones o vehículos cruzando
	 * simultáneamente. Los vehículos cruzan en grupos de 4 vehículos, y esperan su
	 * turno si hay menos de 4 vehículos cruzando o si no es su turno. Una vez que
	 * cruzan, esperan un tiempo antes de intentar cruzar de nuevo.
	 *
	 */
	@Override
	public void run() {
		try {
			while (true) {
				// NORTE-SUR
				if (direccion == 0) {

					CruceSemaforos.mutex.acquire();
					// No se puede cruzar, incrementar el número de vehículos esperando
					if (CruceSemaforos.vehiculosNSpasando >= 4 || CruceSemaforos.turno != 0
							|| CruceSemaforos.peatonesPasando > 0 || CruceSemaforos.vehiculosEOpasando > 0) {
						CruceSemaforos.vehiculosNSesperando++;
						CruceSemaforos.mutex.release();
						CruceSemaforos.semaforoNS.acquire();

						CruceSemaforos.vehiculosNSesperando--;

					}

					// Entrando en la sección crítica (cruce)
					CruceSemaforos.vehiculosNSpasando++;
					CruceSemaforos.pantalla.acquire();
					System.out.println("VehiculosNS " + id + " cruzando");
					CruceSemaforos.pantalla.release();

					// Si es el turno de los vehículos NS y hay vehículosNS esperando a cruzar y no
					// hay peatones cruzando y no hay 4 vehículos cruzando simultáneamente se libera
					// el semáforo NS
					if (CruceSemaforos.turno == 0 && CruceSemaforos.vehiculosNSesperando > 0
							&& CruceSemaforos.peatonesPasando == 0 && CruceSemaforos.vehiculosNSpasando < 4) {
						CruceSemaforos.semaforoNS.release();
					} else {
						// Si no se cumple ninguna de las condiciones anteriores se libera el mutex
						CruceSemaforos.mutex.release();

					}
					Thread.sleep(CruceSemaforos.TIEMPO_CRUCE_VEHICULO); // Tiempo para cruzar
					CruceSemaforos.mutex.acquire();
					CruceSemaforos.vehiculosNSpasando--;
					CruceSemaforos.pantalla.acquire();
					System.err.println("** VehiculoNS " + id + " ha cruzado");
					CruceSemaforos.pantalla.release();

					// Si es el turno de EO y no hay vehiculosNS pasando y hay vehiculos EO
					// esperando, se libera el semáforo de E0
					if (CruceSemaforos.turno == 1 && CruceSemaforos.vehiculosNSpasando == 0
							&& CruceSemaforos.vehiculosEOesperando > 0) {
						CruceSemaforos.semaforoEO.release();
					}
					// Si es el turno de NS y no hay peatones cruzando y hay vehiculosNS esperando y
					// no hay 4 vehículos cruzando simultáneamente se libera el semáforo de NS
					else if (CruceSemaforos.turno == 0 && CruceSemaforos.peatonesPasando == 0
							&& CruceSemaforos.vehiculosNSpasando < 4 && CruceSemaforos.vehiculosNSesperando > 0) {
						CruceSemaforos.semaforoNS.release();
					}
					// Si no se cumple ninguna de las condiciones anteriores se libera el mutex
					else {
						CruceSemaforos.mutex.release();
					}

				}
				// ESTE-OESTE
				else {
					CruceSemaforos.mutex.acquire();
					// No se puede cruzar, incrementar el número de vehículos esperando
					if (CruceSemaforos.vehiculosEOpasando >= 4 || CruceSemaforos.turno != 1
							|| CruceSemaforos.vehiculosNSpasando > 0 || CruceSemaforos.peatonesPasando > 0) {

						CruceSemaforos.vehiculosEOesperando++;
						CruceSemaforos.mutex.release();
						CruceSemaforos.semaforoEO.acquire();

						CruceSemaforos.vehiculosEOesperando--;

					}

					// Entrando en la sección crítica (cruce)
					CruceSemaforos.vehiculosEOpasando++;
					CruceSemaforos.pantalla.acquire();
					System.out.println("VehiculosEO " + id + " cruzando");
					CruceSemaforos.pantalla.release();

					// Si es el turno de los vehículos EO y hay vehículosEO esperando a cruzar y no
					// hay vehículosNS cruzando y no hay 4 vehículos cruzando simultáneamente se
					// libera el semáforo EO
					if (CruceSemaforos.turno == 1 && CruceSemaforos.vehiculosEOesperando > 0
							&& CruceSemaforos.vehiculosNSpasando == 0 && CruceSemaforos.vehiculosEOpasando < 4) {
						CruceSemaforos.semaforoEO.release();
					} else {
						// Si no se cumple ninguna de las condiciones anteriores se libera el mutex
						CruceSemaforos.mutex.release();

					}
					Thread.sleep(CruceSemaforos.TIEMPO_CRUCE_VEHICULO); // Tiempo para cruzar
					CruceSemaforos.pantalla.acquire();
					System.err.println("** VehiculoEO " + id + " ha cruzado");
					CruceSemaforos.pantalla.release();

					CruceSemaforos.mutex.acquire();
					CruceSemaforos.vehiculosEOpasando--;

					// Si es el turno de los peatones y no hay vehículosEO pasando y hay peatones
					// esperando, se libera el semáforo de peatones
					if (CruceSemaforos.turno == 2 && CruceSemaforos.vehiculosEOpasando == 0
							&& CruceSemaforos.peatonesEsperando > 0) {
						CruceSemaforos.semaforoPeatones.release();
					}

					// Si es el turno de EO y no hay vehículosEO pasando y hay vehículosEO
					// esperando, se libera el semáforo de EO
					else if (CruceSemaforos.turno == 1 && CruceSemaforos.vehiculosEOpasando == 0
							&& CruceSemaforos.vehiculosEOesperando > 0) {
						CruceSemaforos.semaforoEO.release();
					} else if (CruceSemaforos.turno == 1 && CruceSemaforos.vehiculosNSpasando == 0
							&& CruceSemaforos.vehiculosEOpasando < 4 && CruceSemaforos.vehiculosEOesperando > 0) {
						CruceSemaforos.semaforoEO.release();
					}
					// Si no se cumple ninguna de las condiciones anteriores se libera el mutex
					else {
						CruceSemaforos.mutex.release();
					}
				}

				Thread.sleep(CruceSemaforos.TIEMPO_ESPERA_VEHICULO); // Espera para intentar cruzar de nuevo
				this.direccion = (this.direccion + 1) % 2;
			}
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}
}
