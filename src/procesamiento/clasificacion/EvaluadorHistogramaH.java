package procesamiento.clasificacion;

import java.util.ArrayList;
import java.util.List;

import objeto.Histograma;
import objeto.Objeto;

public class EvaluadorHistogramaH extends EvaluadorHistograma {
	public static final double CORRELACION_MINIMA = 0.7;
	public static final double DISTANCIA_MINIMA = 0.7;


	public EvaluadorHistogramaH() {
		super(Histograma.HISTOGRAMA_H,DISTANCIA_MINIMA,CORRELACION_MINIMA);
	}


	@Override
	public List<Histograma> calcularHistogramas(Objeto objeto) {
		Histograma histoHobjeto = objeto.getHistograma(Histograma.HISTOGRAMA_H);
		if (histoHobjeto == null){
			List<Histograma> histogramas = new ArrayList<Histograma>();
			
			Histograma histogramaH = new Histograma();
			histogramaH.setTipo(Histograma.HISTOGRAMA_H);

			int cantPuntos = objeto.getPuntos().size();
			double[] valorH = new double[Histograma.MAX_VAL_HISTOGRAMA_H + 1];
			for(int i = 0; i < Histograma.MAX_VAL_HISTOGRAMA_H + 1; i++){
				valorH[i] = objeto.getAcumuladorH()[i] / cantPuntos;
			}
			histogramaH.setValores(valorH);
			histogramas.add(histogramaH);
			objeto.getHistogramas().addAll(histogramas);
			return histogramas;
		}
		return objeto.getHistogramas();
	}

}
