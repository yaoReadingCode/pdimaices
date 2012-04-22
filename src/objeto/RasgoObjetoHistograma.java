package objeto;

import java.util.List;

/**
 * RasgoObjeto para almacenar los histogramas calculados de un objeto
 * @author oscar
 *
 */
public class RasgoObjetoHistograma extends RasgoObjeto {
	
	private static final Double DEFAULT_VALOR_RASGO_CLASE = 1.0;
	
	private List<Histograma> histogramas;
	
	public RasgoObjetoHistograma() {
		super();
	}

	public RasgoObjetoHistograma(Rasgo rasgo, Double valor, Clase clase) {
		super(rasgo, valor, clase);
	}

	public List<Histograma> getHistogramas() {
		return histogramas;
	}

	public void setHistogramas(List<Histograma> histogramas) {
		this.histogramas = histogramas;
	}
	
	/**
	 * Retorna el valor 1 para todos los histogramas ya que valor ideal de comparación de dos histogramas es 1.
	 * Esto asegura que al decidir si un objeto pertece a una de dos clases, se seleccione aquella clase para la cual
	 * el valor de comparación del histograma en la clase se mas cercano a 1 (@see objeto.clase#distanciaPromedio(Objeto)). 
	 * @see objeto.RasgoObjeto#getValorRasgoClase()
	 */
	public Double getValorRasgoClase() {
		return DEFAULT_VALOR_RASGO_CLASE;
	}
	
	

}
