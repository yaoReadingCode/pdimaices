package procesamiento.clasificacion;

import java.awt.Color;

import objeto.Objeto;
import objeto.RasgoClase;
import objeto.RasgoObjeto;

public class PorcentajeColorAmarrillo  extends EvaluadorRasgo{

	
	public PorcentajeColorAmarrillo(RasgoClase rasgo, Double minimo, Double maximo) {
		super(rasgo, minimo, maximo);
	}
	/**
	 * 
	 */
	public RasgoObjeto calcularValor(Objeto objeto) {
		Color colorPromedio = objeto.colorPromedio();
		Double colorAmarrillo = (double)( colorPromedio.getGreen() + colorPromedio.getRed())/2; 
		return new RasgoObjeto(this.getRasgoClase().getRasgo(),colorAmarrillo);
	}
}
