package procesamiento.clasificacion;

import java.util.List;

import objeto.Histograma;
import objeto.Objeto;

public class EvaluadorHistogramaHSV extends EvaluadorHistograma {
	public static final double CORRELACION_MINIMA = 0.7;
	public static final double DISTANCIA_MINIMA = 0.7;
	
	private EvaluadorHistogramaH evaluadorH;
	private EvaluadorHistogramaS evaluadorS;
	private EvaluadorHistogramaV evaluadorV;

	public EvaluadorHistogramaHSV() {
		super("HSV", DISTANCIA_MINIMA, CORRELACION_MINIMA);
		evaluadorH = new EvaluadorHistogramaH();
		evaluadorS = new EvaluadorHistogramaS();
		evaluadorV = new EvaluadorHistogramaV();
	}

	@Override
	public List<Histograma> calcularHistogramas(Objeto objeto) {
		evaluadorH.calcularHistogramas(objeto);
		evaluadorS.calcularHistogramas(objeto);
		evaluadorV.calcularHistogramas(objeto);
		return objeto.getHistogramas();
	}
	
	/**
	 * @see procesamiento.clasificacion.EvaluadorRasgo#isEnRango(objeto.Objeto, objeto.RasgoObjeto)
	 */
	/*
	public boolean isEnRango(Objeto objeto, RasgoObjeto rasgoObjeto) {
		List<Histograma> histogramas = ((RasgoObjetoHistograma) rasgoObjeto).getHistogramas();
		Histograma hObjeto = getHistograma(evaluadorH.getTipoHistograma(), histogramas);
		Histograma hClase = this.getRasgoClase().getClase().getHistograma(evaluadorH.getTipoHistograma());
		if (hObjeto == null)
			return false;
		if (hClase == null)
			return true;
		Histograma sObjeto = getHistograma(evaluadorS.getTipoHistograma(), histogramas);
		Histograma sClase = this.getRasgoClase().getClase().getHistograma(evaluadorS.getTipoHistograma());
		if (sObjeto == null)
			return false;
		if (sClase == null)
			return true;
		Histograma vObjeto = getHistograma(evaluadorV.getTipoHistograma(), histogramas);
		Histograma vClase = this.getRasgoClase().getClase().getHistograma(evaluadorV.getTipoHistograma());
		if (vObjeto == null)
			return false;
		if (vClase == null)
			return true;

		double distanciaH = ObjetoUtil.distanciaBhattacharya(hClase.getValores(), hObjeto.getValores());
		double distanciaS = ObjetoUtil.distanciaBhattacharya(sClase.getValores(), sObjeto.getValores());
		double distanciaV = ObjetoUtil.distanciaBhattacharya(vClase.getValores(), vObjeto.getValores());
		double distancia = (distanciaH + distanciaS + distanciaV) / 3;
		
		double distanciaMinima = getDistanciaMinima();
		if (getRasgoClase().getMinimo() != null){
			distanciaMinima = getRasgoClase().getMinimo();
		}
		if (distancia < distanciaMinima){
			return false;
		}
		return true;
	}	*/
}
