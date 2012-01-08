package procesamiento.clasificacion;

import java.util.List;

import objeto.Histograma;
import objeto.Objeto;
import objeto.ObjetoUtil;
import objeto.RasgoObjeto;
import objeto.RasgoObjetoHistograma;

public abstract class EvaluadorHistograma extends EvaluadorRasgo {
	
	private String tipoHistograma;
	
	private double distanciaMinima = 0.0;
	
	private double correlacionMinima = 0.0;
	
	public EvaluadorHistograma(String tipoHistograma) {
		super();
		this.tipoHistograma = tipoHistograma;
	}

	public EvaluadorHistograma(String tipoHistograma, double distanciaMinima,
			double correlacionMinima) {
		super();
		this.tipoHistograma = tipoHistograma;
		this.distanciaMinima = distanciaMinima;
		this.correlacionMinima = correlacionMinima;
	}


	public String getTipoHistograma() {
		return tipoHistograma;
	}

	public void setTipoHistograma(String tipoHistograma) {
		this.tipoHistograma = tipoHistograma;
	}
	
	public double getDistanciaMinima() {
		return distanciaMinima;
	}

	public void setDistanciaMinima(double distanciaMinima) {
		this.distanciaMinima = distanciaMinima;
	}

	public double getCorrelacionMinima() {
		return correlacionMinima;
	}

	public void setCorrelacionMinima(double correlacionMinima) {
		this.correlacionMinima = correlacionMinima;
	}

	/**
	 * @see procesamiento.clasificacion.EvaluadorRasgo#calcularValor(objeto.Objeto)
	 */
	public RasgoObjeto calcularValor(Objeto objeto) {
		List<Histograma> histogramas = calcularHistogramas(objeto);
		RasgoObjetoHistograma rasgoObjeto = new RasgoObjetoHistograma(this.getRasgoClase().getRasgo(),null, this.getRasgoClase().getClase());
		rasgoObjeto.setHistogramas(histogramas);
		double distanciaSum = 0;
		for(Histograma h:histogramas){
			Histograma hClase = this.getRasgoClase().getClase().getHistograma(h.getTipo());
			double distancia = -1;
			if (hClase != null){
				distancia = ObjetoUtil.distanciaBhattacharya(h.getValores(), hClase.getValores());
			}
			distanciaSum += distancia;
		}
		if (histogramas.size() > 0){
			double distanciaProm = distanciaSum / histogramas.size();
			rasgoObjeto.setValor(distanciaProm);
		}
		return rasgoObjeto;
	}
	
	/**
	 * Calcula los histogramas del objeto
	 */
	public abstract List<Histograma> calcularHistogramas(Objeto objeto);


	
	/**
	 * Busca el histograma con del tipo pasado como paránetro
	 * @param tipo
	 * @param histogramas
	 * @return
	 */
	protected Histograma getHistograma(String tipo, List<Histograma> histogramas){
		for(Histograma h:histogramas){
			if (h.getTipo().equals(tipo))return h;
		}
		return null;
	}
	
	/**
	 * @see procesamiento.clasificacion.EvaluadorRasgo#isEnRango(objeto.Objeto, objeto.RasgoObjeto)
	 */
	public boolean isEnRango(Objeto objeto, RasgoObjeto rasgoObjeto) {
		if (rasgoObjeto.getValor() == null)
			return false;
		double distanciaMinima = getDistanciaMinima();
		if (getRasgoClase().getMinimo() != null){
			distanciaMinima = getRasgoClase().getMinimo();
		}
		if (rasgoObjeto.getValor() < distanciaMinima){
			return false;
		}
		return true;
	}
}
