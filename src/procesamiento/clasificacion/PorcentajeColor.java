package procesamiento.clasificacion;

import java.awt.Color;

import objeto.Objeto;
import objeto.RasgoClase;
import objeto.RasgoObjeto;

public class PorcentajeColor extends EvaluadorRasgo{

	public PorcentajeColor(RasgoClase rasgo, Double minimo, Double maximo) {
		super(rasgo, minimo, maximo);
	}
	/**
	 * 
	 */
	public RasgoObjeto calcularValor(Objeto objeto) {
		Color colorPromedio = objeto.colorPromedio();
		Double color = (double)(colorPromedio.getRGB());
		return new RasgoObjeto(this.getRasgoClase().getRasgo(),color);
	}

}
