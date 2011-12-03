/**
 * 
 */
package objeto;

import java.util.List;

/**
 * Interface que contiene metodos comunes para obtener un Histograma
 * @author oscar giorgetti
 *
 */
public interface HistogramaContainer {
	/**
	 * Retornan el Histograma de un dado tipo
	 * @param tipo
	 * @return
	 */
	public Histograma getHistograma(String tipo);
	
	/**
	 * Retorna la lista de Histograma
	 * @return
	 */
	public List<Histograma> getHistogramas(); 
}
