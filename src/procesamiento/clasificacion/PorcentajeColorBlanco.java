package procesamiento.clasificacion;

import java.awt.Color;

import objeto.Objeto;
import objeto.RasgoClase;
import objeto.RasgoObjeto;

public class PorcentajeColorBlanco extends EvaluadorRasgo{

	public PorcentajeColorBlanco(RasgoClase nombre, Double valor,
			Double desvioEstandar) {
		super(nombre, valor, desvioEstandar);
		
	}

	/**
	 * 
	 */
	public RasgoObjeto calcularValor(Objeto objeto) {
		Color colorPromedio = objeto.colorPromedio();
		Double colorAmarrillo = (double)( colorPromedio.getGreen() + colorPromedio.getRed() + colorPromedio.getBlue())/3; 
		return new RasgoObjeto(this.getRasgoClase().getRasgo(),colorAmarrillo);
	}

}
