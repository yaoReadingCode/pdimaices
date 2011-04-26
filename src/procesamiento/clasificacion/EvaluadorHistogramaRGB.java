package procesamiento.clasificacion;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import objeto.Histograma;
import objeto.Objeto;
import objeto.Pixel;
import objeto.RasgoClase;
import objeto.RasgoObjeto;
import objeto.ValorHistograma;

public class EvaluadorHistogramaRGB extends EvaluadorRasgo {

	public EvaluadorHistogramaRGB() {
		// TODO Auto-generated constructor stub
	}

	public EvaluadorHistogramaRGB(RasgoClase rasgo, Double valor,
			Double desvioEstandar) {
		super(rasgo, valor, desvioEstandar);
		// TODO Auto-generated constructor stub
	}
	public RasgoObjeto calcularValor(Objeto objeto) {
		return super.calcularValor(objeto);
	}
	public List<Histograma> calcularHistogramas(Objeto objeto) {
		List<Histograma> histogramas = new ArrayList<Histograma>();
		
		/*double[] acumuladorR = new double[256];
		double[] acumuladorG = new double[256];
		double[] acumuladorB = new double[256];
		for(int i = 0; i < 256; i++){
			acumuladorR[i] = 0;
			acumuladorG[i] = 0;
			acumuladorB[i] = 0;
		}
		
		for(Pixel p :objeto.getPuntos()){
			Color color = p.getCol();
			if (color != null){
				acumuladorR[color.getRed()]++;
				acumuladorG[color.getGreen()]++;
				acumuladorB[color.getBlue()]++;
			}
		}
		*/
		Histograma histogramaR = new Histograma(this.getRasgoClase().getRasgo(),0.0);
		histogramaR.setMaxValores(256);
		histogramaR.setTipo("R");

		Histograma histogramaG = new Histograma(this.getRasgoClase().getRasgo(),0.0);
		histogramaG.setMaxValores(256);
		histogramaG.setTipo("G");

		Histograma histogramaB = new Histograma(this.getRasgoClase().getRasgo(),0.0);
		histogramaB.setMaxValores(256);
		histogramaB.setTipo("B");
		
		int cantPuntos = objeto.getPuntos().size();
		for(int i = 0; i < 256; i++){
			ValorHistograma valorR = new ValorHistograma(histogramaR, objeto.getAcumuladorR()[i] / cantPuntos);
			ValorHistograma valorG = new ValorHistograma(histogramaG, objeto.getAcumuladorG()[i] / cantPuntos);
			ValorHistograma valorB = new ValorHistograma(histogramaB, objeto.getAcumuladorB()[i] / cantPuntos);
			histogramaR.getValores().add(valorR);
			histogramaG.getValores().add(valorG);
			histogramaB.getValores().add(valorB);
		}
		histogramas.add(histogramaR);
		histogramas.add(histogramaG);
		histogramas.add(histogramaB);
		return histogramas;
	}

	public boolean isEnRango(Objeto objeto, boolean addRasgoToObject) {
		List<Histograma> histogramas = calcularHistogramas(objeto);
		for(Histograma h: histogramas)
			objeto.addRasgo(h);	
		return true;
	}
}
