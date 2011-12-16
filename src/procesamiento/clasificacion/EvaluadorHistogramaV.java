package procesamiento.clasificacion;

import java.util.ArrayList;
import java.util.List;

import objeto.Histograma;
import objeto.Objeto;

public class EvaluadorHistogramaV extends EvaluadorHistograma {
	public static final double CORRELACION_MINIMA = 0.7;
	public static final double DISTANCIA_MINIMA = 0.7;


	public EvaluadorHistogramaV() {
		super(Histograma.HISTOGRAMA_V,DISTANCIA_MINIMA,CORRELACION_MINIMA);
	}


	@Override
	public List<Histograma> calcularHistogramas(Objeto objeto) {
		Histograma histoVobjeto = objeto.getHistograma(Histograma.HISTOGRAMA_V);
		if (histoVobjeto == null){
			List<Histograma> histogramas = new ArrayList<Histograma>();
			
			Histograma histogramaV = new Histograma();
			histogramaV.setTipo(Histograma.HISTOGRAMA_V);

			int cantPuntos = objeto.getPuntos().size();
			double[] valorV = new double[Histograma.MAX_VAL_HISTOGRAMA_V + 1];
			for(int i = 0; i < Histograma.MAX_VAL_HISTOGRAMA_V + 1; i++){
				valorV[i] = objeto.getAcumuladorV()[i] / cantPuntos;
			}
			histogramaV.setValores(valorV);
			histogramas.add(histogramaV);
			objeto.getHistogramas().addAll(histogramas);
			return histogramas;
		}
		return objeto.getHistogramas();
	}

}
