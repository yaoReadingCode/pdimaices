package procesamiento.clasificacion;

import java.awt.Color;

import objeto.Objeto;
import objeto.RasgoClase;
import objeto.RasgoObjeto;

public class PorcentajeColorAmarrillo  extends EvaluadorRasgo{

	
	public PorcentajeColorAmarrillo(RasgoClase nombre, Double valor, Double desvioEstandar) {
		super(nombre, valor, desvioEstandar);
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
