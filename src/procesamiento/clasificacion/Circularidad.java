package procesamiento.clasificacion;

import objeto.Objeto;
import objeto.RasgoClase;

public class Circularidad extends EvaluadorRasgo {

	public Circularidad() {
		super();
		// TODO Auto-generated constructor stub
	}


	public Circularidad(RasgoClase rasgo, Double valor, Double desvioEstandar) {
		super(rasgo, valor, desvioEstandar);
	}

	 
	public Double calcularValor(Objeto objeto) {
		/*
		double perimetro = objeto.getLongitudPerimetro();
		double area = objeto.getArea();
		
		if (perimetro != 0){
			return area / perimetro;
		}
		return null;*/

		double perimetro = objeto.getLongitudPerimetro();
		double radio = objeto.getRadio();
		if (radio != 0)
			return perimetro / (2 * Math.PI* radio);
		return null;
	}

}
