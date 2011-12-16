package procesamiento.clasificacion;

import java.util.ArrayList;
import java.util.List;

import objeto.Histograma;
import objeto.Objeto;
import objeto.ObjetoUtil;
import objeto.RasgoClase;
import objeto.RasgoObjeto;
import objeto.RasgoObjetoHistograma;

public class EvaluadorHistogramaRGB extends EvaluadorRasgo {
	
	public static final double CORRELACION_MINIMA = 0.7;
	public static final double DISTANCIA_MINIMA = 0.7;

	public EvaluadorHistogramaRGB() {
		// TODO Auto-generated constructor stub
	}

	public EvaluadorHistogramaRGB(RasgoClase rasgo, Double valor,
			Double desvioEstandar) {
		super(rasgo, valor, desvioEstandar);
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

		Histograma histoRobjeto = objeto.getHistograma(Histograma.HISTOGRAMA_R);
		if (histoRobjeto == null){
			List<Histograma> histogramas = new ArrayList<Histograma>();
			
			Histograma histogramaR = new Histograma();
			histogramaR.setTipo(Histograma.HISTOGRAMA_R);

			Histograma histogramaG = new Histograma();
			histogramaG.setTipo(Histograma.HISTOGRAMA_G);

			Histograma histogramaB = new Histograma();
			histogramaB.setTipo(Histograma.HISTOGRAMA_B);
			
			int cantPuntos = objeto.getPuntos().size();
			double[] valorR = new double[Histograma.MAX_VAL_HISTOGRAMA_R + 1];
			double[] valorG = new double[Histograma.MAX_VAL_HISTOGRAMA_G + 1];
			double[] valorB = new double[Histograma.MAX_VAL_HISTOGRAMA_B + 1];
			for(int i = 0; i < 256; i++){
				valorR[i] = objeto.getAcumuladorR()[i] / cantPuntos;
				valorG[i] = objeto.getAcumuladorG()[i] / cantPuntos;
				valorB[i] = objeto.getAcumuladorB()[i] / cantPuntos;
			}
			histogramaR.setValores(valorR);
			histogramaG.setValores(valorG);
			histogramaB.setValores(valorB);
			histogramas.add(histogramaR);
			histogramas.add(histogramaG);
			histogramas.add(histogramaB);
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
		Histograma hRObjeto = getHistograma(Histograma.HISTOGRAMA_R, histogramas);
		Histograma hGObjeto = getHistograma(Histograma.HISTOGRAMA_G, histogramas);
		Histograma hBObjeto = getHistograma(Histograma.HISTOGRAMA_B, histogramas);
		Histograma hRClase = this.getRasgoClase().getClase().getHistograma(Histograma.HISTOGRAMA_R);
		Histograma hGClase = this.getRasgoClase().getClase().getHistograma(Histograma.HISTOGRAMA_G);
		Histograma hBClase = this.getRasgoClase().getClase().getHistograma(Histograma.HISTOGRAMA_B);

		if (hRClase == null || hGClase == null || hBClase == null)
			return true;
		/*
		double coeficienteCorrelacion = ObjetoUtil.coeficienteCorrelacion(hRClase.getValores(), hRObjeto.getValores());
		if (coeficienteCorrelacion < CORRELACION_MINIMA){
			return false;
		}
		coeficienteCorrelacion = ObjetoUtil.coeficienteCorrelacion(hGClase.getValores(), hGObjeto.getValores());
		if (coeficienteCorrelacion < CORRELACION_MINIMA){
			return false;
		}
		coeficienteCorrelacion = ObjetoUtil.coeficienteCorrelacion(hBClase.getValores(), hBObjeto.getValores());
		if (coeficienteCorrelacion < CORRELACION_MINIMA){
			return false;
		}*/
		double distanciaMinima = DISTANCIA_MINIMA;
		if (getRasgoClase().getMinimo() != null)
			distanciaMinima = getRasgoClase().getMinimo();
		double distancia = ObjetoUtil.distanciaBhattacharya(hRClase.getValores(), hRObjeto.getValores());
		if (distancia < distanciaMinima){
			return false;
		}
		distancia = ObjetoUtil.distanciaBhattacharya(hGClase.getValores(), hGObjeto.getValores());
		if (distancia < distanciaMinima){
			return false;
		}
		distancia = ObjetoUtil.distanciaBhattacharya(hBClase.getValores(), hBObjeto.getValores());
		if (distancia < distanciaMinima){
			return false;
		}

		return true;
	}
}
