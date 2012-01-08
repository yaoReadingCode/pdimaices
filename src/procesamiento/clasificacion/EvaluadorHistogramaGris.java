package procesamiento.clasificacion;

import java.util.ArrayList;
import java.util.List;

import objeto.Histograma;
import objeto.Objeto;

public class EvaluadorHistogramaGris extends EvaluadorHistograma {
	public static final double CORRELACION_MINIMA = 0.7;
	public static final double DISTANCIA_MINIMA = 0.7;
	
	public EvaluadorHistogramaGris() {
		super(Histograma.HISTOGRAMA_GRIS,DISTANCIA_MINIMA,CORRELACION_MINIMA);
	}
	
	/**
	 * Calcula los histogramas del objeto
	 */
	public List<Histograma> calcularHistogramas(Objeto objeto) {

		Histograma histoObjeto = objeto.getHistograma(Histograma.HISTOGRAMA_GRIS);
		if (histoObjeto == null){
			List<Histograma> histogramas = new ArrayList<Histograma>();
			
			Histograma histograma = new Histograma();
			histograma.setTipo(Histograma.HISTOGRAMA_GRIS);

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
	
}
