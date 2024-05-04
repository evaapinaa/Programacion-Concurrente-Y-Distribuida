package Ejercicio3;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Esta clase actúa como un monitor para sincronizar la interacción entre los
 * diferentes clientes en el banco. Controla el acceso concurrente a las
 * máquinas y las mesas, asegurando que los clientes sean atendidos sin
 * problema. Implementa mecanismos de bloqueo y condiciones para manejar la
 * asignación de recursos y la espera de los clientes.
 */

public class Monitor {

	private ReentrantLock llave = new ReentrantLock(true); // true => FIFO
	private Condition[] mesa;
	private Condition maquinaLibre = llave.newCondition();
	private boolean[] mesasOcupadas;
	private boolean[] maquinasOcupadas;
	private int[] tiemposEsperaMesas;
	private int nMaquinasOcupadas = 0;

	/**
	 * Método que devuelve los tiempos de espera de las mesas.
	 * 
	 * @return Array de enteros con los tiempos de espera de las mesas.
	 */
	public int[] getTiemposEsperaMesas() {
		return tiemposEsperaMesas;
	}

	/**
	 * Inicializa el monitor con el número especificado de mesas y máquinas.
	 * Inicializa los diversos arrays para gestionar el estado de ocupación de cada
	 * recurso y las colas de espera asociadas.
	 * 
	 * @param nMesas    Número total de mesas disponibles en el banco.
	 * @param nMaquinas Número total de máquinas disponibles en el banco.
	 */

	public Monitor(int nMesas, int nMaquinas) {
		this.tiemposEsperaMesas = new int[nMesas];
		for (int i = 0; i < nMesas; i++) {
			tiemposEsperaMesas[i] = 0;
		}
		this.mesa = new Condition[nMesas];

		this.mesasOcupadas = new boolean[nMesas];
		for (int i = 0; i < nMesas; i++) {
			mesa[i] = llave.newCondition();
			mesasOcupadas[i] = false;
		}

		this.maquinasOcupadas = new boolean[nMaquinas];
		for (int i = 0; i < nMaquinas; i++) {
			maquinasOcupadas[i] = false;
		}
	}

	/**
	 * Asigna una máquina disponible a un cliente. Si no hay máquinas disponibles,
	 * el cliente espera hasta que una se libere.
	 * 
	 * @return El índice de la máquina asignada al cliente.
	 * @throws InterruptedException Si el hilo es interrumpido mientras espera.
	 */

	public int asignarMaquina() throws InterruptedException {
		llave.lock();
		try {
			while (nMaquinasOcupadas == Programa.NUM_MAQUINAS) {
				maquinaLibre.await();
			}
			int valor = 0;
			while (maquinasOcupadas[valor]) {
				valor++;
			}
			nMaquinasOcupadas++;
			maquinasOcupadas[valor] = true;
			return valor;
		} finally {
			llave.unlock();
		}
	}

	/**
	 * Libera una máquina que estaba siendo utilizada por un cliente. Notifica a
	 * todos los demás clientes en espera.
	 * 
	 * @param indice Índice de la máquina que se libera.
	 */

	public void dejarMaquina(int indice) {
		llave.lock();
		try {
			nMaquinasOcupadas--;
			maquinasOcupadas[indice] = false;
			maquinaLibre.signalAll();
		} finally {
			llave.unlock();
		}
	}

	/**
	 * Asigna al cliente la mesa con el menor tiempo de espera acumulado. Calcula el
	 * tiempo adicional que el cliente añadirá a la espera de esa mesa.
	 * 
	 * @param tiempoCliente Tiempo que el cliente necesitará en la mesa.
	 * @return Índice de la mesa asignada al cliente.
	 * @throws InterruptedException Si el hilo es interrumpido mientras espera.
	 */

	public int asignarMesa(int tiempoCliente) throws InterruptedException {
		llave.lock();
		try {
			int indiceMesaMenorTiempo = 0;
			for (int i = 0; i < tiemposEsperaMesas.length; i++) {
				if (tiemposEsperaMesas[i] < tiemposEsperaMesas[indiceMesaMenorTiempo]) {
					indiceMesaMenorTiempo = i;
				}
			}
			tiemposEsperaMesas[indiceMesaMenorTiempo] += tiempoCliente;
			return indiceMesaMenorTiempo;
		} finally {
			llave.unlock();
		}
	}

	/**
	 * Asigna una mesa específica a un cliente, asegurando que la mesa esté libre de
	 * antes. Utiliza un mecanismo de bloqueo para controlar el acceso concurrente y
	 * garantizar que solo un cliente a la vez pueda ocupar una mesa. Si la mesa ya
	 * está ocupada, el cliente entrante espera hasta que la mesa se libere.
	 * 
	 * @param id Identificador de la mesa a ocupar.
	 * @throws InterruptedException Si el hilo que intenta sentarse en la mesa es
	 *                              interrumpido mientras espera.
	 */
	public void sentarMesa(int id) throws InterruptedException {
		llave.lock();
		try {
			while (mesasOcupadas[id]) {
				mesa[id].await();
			}
			mesasOcupadas[id] = true;
		} finally {
			llave.unlock();
		}
	}

	/**
	 * Libera una mesa después de que un cliente haya sido atendido. Actualiza el
	 * tiempo de espera de la mesa y notifica al siguiente cliente que puede ser
	 * atendido en esa mesa.
	 * 
	 * @param id            Índice de la mesa que se libera.
	 * @param tiempoCliente Tiempo que el cliente pasó siendo atendido en la mesa.
	 */
	public void dejarMesa(int id, int tiempoCliente) {
		llave.lock();
		try {
			tiemposEsperaMesas[id] = tiemposEsperaMesas[id] - tiempoCliente;
			mesasOcupadas[id] = false;
			mesa[id].signal();
		} finally {
			llave.unlock();
		}
	}

	/**
	 * Imprime mensajes utilizando un reentrant lock para garantizar que los los
	 * mensajes se impriman sin intercalación.
	 * 
	 * @param cadena Cadena de texto a imprimir.
	 * @param n      Indicador de tipo de mensaje (0 para normal, 1 para error:
	 *               "imprimir con color en la consola").
	 */

	public void imprimir(String cadena, int n) {
		llave.lock();
		try {
			if (n == 0) {
				System.out.println(cadena);
			} else {
				System.err.println(cadena);
			}
		} finally {
			llave.unlock();
		}
	}

}