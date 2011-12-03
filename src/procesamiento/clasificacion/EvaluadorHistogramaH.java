package procesamiento.clasificacion;

import java.util.ArrayList;
import java.util.List;

import objeto.Histograma;
import objeto.Objeto;

public class EvaluadorHistogramaH extends EvaluadorHistograma {
	public static final double CORRELACION_MINIMA = 0.7;
	public static final double DISTANCIA_MINIMA = 0.7;


	public EvaluadorHistogramaH() {
		super(Histograma.HISTOGRAMA_H,CORRELACION_MINIMA,DISTANCIA_MINIMA);
	}


	@Override
	public List<Histograma> calcularHistogramas(Objeto objeto) {
		Histograma histoRobjeto = objeto.getHistograma(Histograma.HISTOGRAMA_H);
		if (histoRobjeto == null){
			List<Histograma> histogramas = new ArrayList<Histograma>();
			
			Histograma histogramaS = new Histograma();
			histogramaS.setTipo(Histograma.HISTOGRAMA_H);

			int cantPuntos = objeto.getPuntos().size();
			double[] valorS = new double[Histograma.MAX_VAL_HISTOGRAMA_H + 1];
			for(int i = 0; i < Histograma.MAX_VAL_HISTOGRAMA_H + 1; i++){
				valorS[i] = objeto.getAcumuladorH()[i] / cantPuntos;
			}
			histogramaS.setValores(valorS);
			histogramas.add(histogramaS);
			objeto.getHistogramas().addAll(histogramas);
			return histogramas;
		}
		return objeto.getHistogramas();
	}

}
