package ejercicio3;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Monitor {
	
	private ReentrantLock llave = new ReentrantLock(true); //FIFO
	private Condition[] mesa;
	private Condition maquinaLibre = llave.newCondition();
	private boolean[] mesasOcupadas;
	private boolean[] maquinasOcupadas;
	private int[] tiemposEsperaMesas;
	private int nMaquinasOcupadas = 0;
	
	public int[] getTiemposEsperaMesas() {
		return tiemposEsperaMesas;
	}
	
	public Monitor(int nMesas, int nMaquinas) {
		this.tiemposEsperaMesas = new int[nMesas];
		for(int i=0; i < nMesas; i++) {
			tiemposEsperaMesas[i] = 0;
		}
		this.mesa = new Condition[nMesas];
        
		this.mesasOcupadas = new boolean[nMesas];
		for(int i=0;i<nMesas;i++) {
			mesa[i] = llave.newCondition();
			mesasOcupadas[i] = false;
		}
		
		this.maquinasOcupadas = new boolean[nMaquinas];
		for(int i=0;i<nMaquinas;i++) {
			maquinasOcupadas[i] = false;
		}
	}
	
	public int asignarMaquina() throws InterruptedException{
		llave.lock();
		try {
			while(nMaquinasOcupadas == Programa.NUM_MAQUINAS) {
				maquinaLibre.await();
			}
			int valor = 0;
			while(maquinasOcupadas[valor]) {
				valor++;
			}
			nMaquinasOcupadas++;
			maquinasOcupadas[valor] = true;
			return valor;
		} finally {
			llave.unlock();
		}
	}
	
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
	
	public void sentarMesa(int id) throws InterruptedException{
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
	
	public void imprimir(String cadena, int n) {
		llave.lock();
		try {
			if(n == 0) {
				System.out.println(cadena);
			}
			else {
				System.err.println(cadena);
			}
		} finally {
			llave.unlock();
		}
	}
	
	
}
