package objeto;

import java.util.List;

/**
 * RasgoObjeto para almacenar los histogramas calculados de un objeto
 * @author oscar
 *
 */
public class RasgoObjetoHistograma extends RasgoObjeto {

	private List<Histograma> histogramas;
	
	public RasgoObjetoHistograma() {
		super();
	}

	public RasgoObjetoHistograma(Rasgo rasgo, Double valor) {
		super(rasgo, valor);
	}

	public List<Histograma> getHistogramas() {
		return histogramas;
	}

	public void setHistogramas(List<Histograma> histogramas) {
		this.histogramas = histogramas;
	}

}
