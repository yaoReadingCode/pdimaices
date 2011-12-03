package procesamiento.clasificacion;

import java.util.ArrayList;
import java.util.List;

import objeto.Histograma;
import objeto.Objeto;
import objeto.ObjetoUtil;
import objeto.RasgoClase;
import objeto.RasgoObjeto;
import objeto.RasgoObjetoHistograma;

public class EvaluadorHistogramaGris extends EvaluadorRasgo {
	public static final String HISTOGRAMA_GRIS = "GRIS";
	public static final double CORRELACION_MINIMA = 0.8;
	public EvaluadorHistogramaGris() {
		super();
	}
	public EvaluadorHistogramaGris(RasgoClase rasgo, Double minimo,
			Double maximo) {
		super(rasgo, minimo, maximo);
	}

	/**
	 * @see procesamiento.clasificacion.EvaluadorRasgo#calcularValor(objeto.Objeto)
	 */
	public RasgoObjeto calcularValor(Objeto objeto) {
		List<Histograma> histogramas = calcularHistogramas(objeto);
		RasgoObjetoHistograma rasgoObjeto = new RasgoObjetoHistograma(this.getRasgoClase().getRasgo(),null);
		rasgoObjeto.setHistogramas(histogramas);
		return rasgoObjeto;
	}
	
	/**
	 * Calcula los histogramas del objeto
	 */
	public List<Histograma> calcularHistogramas(Objeto objeto) {

		Histograma histoObjeto = objeto.getHistograma(HISTOGRAMA_GRIS);
		if (histoObjeto == null){
			List<Histograma> histogramas = new ArrayList<Histograma>();
			
			Histograma histograma = new Histograma();
			histograma.setTipo(HISTOGRAMA_GRIS);

			int cantPuntos = objeto.getPuntos().size();
			double[] valor = new double[256];
			for(int i = 0; i < 256; i++){
				valor[i] = objeto.getAcumuladorGris()[i] / cantPuntos;
			}
			histograma.setValores(valor);
			histogramas.add(histograma);
			objeto.getHistogramas().addAll(histogramas);
			return histogramas;
		}
		return objeto.getHistogramas();

	}
	
	/**
	 * Busca el histograma con del tipo pasado como paránetro
	 * @param tipo
	 * @param histogramas
	 * @return
	 */
	private Histograma getHistograma(String tipo, List<Histograma> histogramas){
		for(Histograma h:histogramas){
			if (h.getTipo().equals(tipo))return h;
		}
		return null;
	}
	
	/**
	 * @see procesamiento.clasificacion.EvaluadorRasgo#isEnRango(objeto.Objeto, objeto.RasgoObjeto)
	 */
	public boolean isEnRango(Objeto objeto, RasgoObjeto rasgoObjeto) {
		List<Histograma> histogramas = ((RasgoObjetoHistograma) rasgoObjeto).getHistogramas();
		Histograma hGris = getHistograma(HISTOGRAMA_GRIS, histogramas);
		Histograma hGrisClase = this.getRasgoClase().getClase().getHistograma(HISTOGRAMA_GRIS);

		if (hGrisClase == null)
			return true;

		double coeficienteCorrelacion = ObjetoUtil.coeficienteCorrelacion(hGrisClase.getValores(), hGris.getValores());
		if (coeficienteCorrelacion < CORRELACION_MINIMA){
			return false;
		}
		return true;
	}
}
