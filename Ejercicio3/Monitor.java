package Ejercicio3;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Clase Monitor que controla la entrada de los clientes al banco.
 */
public class Monitor {

	private ReentrantLock llave = new ReentrantLock(true); // FIFO
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
	 * Constructor de la clase Monitor.
	 * 
	 * @param nMesas    Número de mesas del banco.
	 * @param nMaquinas Número de máquinas del banco.
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
	 * Método que asigna una máquina a un cliente.
	 * 
	 * @return Índice de la máquina asignada.
	 * @throws InterruptedException
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
	 * Método que libera una máquina.
	 * 
	 * @param indice Índice de la máquina a liberar.
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
	 * Método que asigna una mesa a un cliente.
	 * 
	 * @param tiempoCliente Tiempo que tarda el cliente en ser atendido.
	 * @return Índice de la mesa asignada.
	 * @throws InterruptedException
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
	 * Método que sienta a un cliente en una mesa.
	 * 
	 * @param id Identificador de la mesa.
	 * @throws InterruptedException
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
	 * Método que imprime una cadena.
	 * 
	 * @param cadena Cadena a imprimir.
	 * @param n      Número que indica si la cadena es de error o no, para poder
	 *               imprimir con color y así poder distinguir en la consola.
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