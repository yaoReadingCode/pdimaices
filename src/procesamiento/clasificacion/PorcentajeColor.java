package procesamiento.clasificacion;

import java.awt.Color;

import objeto.Objeto;
import objeto.RasgoClase;

public class PorcentajeColor extends EvaluadorRasgo{

	public PorcentajeColor(RasgoClase nombre, Double valor, Double desvioEstandar) {
		super(nombre, valor, desvioEstandar);
	}
	/**
	 * 
	 */
	public Double calcularValor(Objeto objeto) {
		Color colorPromedio = objeto.colorPromedio();
		Double color = (double)(colorPromedio.getRGB()); 
		return color;
	}

}
