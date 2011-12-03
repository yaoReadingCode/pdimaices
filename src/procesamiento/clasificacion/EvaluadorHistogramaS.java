package procesamiento.clasificacion;

import java.util.ArrayList;
import java.util.List;

import objeto.Histograma;
import objeto.Objeto;

public class EvaluadorHistogramaS extends EvaluadorHistograma {
	public static final double CORRELACION_MINIMA = 0.7;
	public static final double DISTANCIA_MINIMA = 0.7;


	public EvaluadorHistogramaS() {
		super(Histograma.HISTOGRAMA_S,CORRELACION_MINIMA,DISTANCIA_MINIMA);
	}


	@Override
	public List<Histograma> calcularHistogramas(Objeto objeto) {
		Histograma histoRobjeto = objeto.getHistograma(Histograma.HISTOGRAMA_S);
		if (histoRobjeto == null){
			List<Histograma> histogramas = new ArrayList<Histograma>();
			
			Histograma histogramaS = new Histograma();
			histogramaS.setTipo(Histograma.HISTOGRAMA_S);

			int cantPuntos = objeto.getPuntos().size();
			double[] valorS = new double[Histograma.MAX_VAL_HISTOGRAMA_S + 1];
			for(int i = 0; i < Histograma.MAX_VAL_HISTOGRAMA_S + 1; i++){
				valorS[i] = objeto.getAcumuladorS()[i] / cantPuntos;
			}
			histogramaS.setValores(valorS);
			histogramas.add(histogramaS);
			objeto.getHistogramas().addAll(histogramas);
			return histogramas;
		}
		return objeto.getHistogramas();
	}

}
