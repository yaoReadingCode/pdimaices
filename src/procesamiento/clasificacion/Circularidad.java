package procesamiento.clasificacion;

import objeto.Objeto;
import objeto.RasgoClase;
import objeto.RasgoObjeto;

public class Circularidad extends EvaluadorRasgo {

	public Circularidad() {
		super();
		// TODO Auto-generated constructor stub
	}


	public Circularidad(RasgoClase rasgo, Double valor, Double desvioEstandar) {
		super(rasgo, valor, desvioEstandar);
	}

	 
	public RasgoObjeto calcularValor(Objeto objeto) {
		/*
		double perimetro = objeto.getLongitudPerimetro();
		double area = objeto.getArea();
		
		if (perimetro != 0){
			return area / perimetro;
		}
		return null;*/

		double perimetro = objeto.getLongitudPerimetro();
		double radio = objeto.getRadio();
		Double circularidad = null;
		if (radio != 0)
			circularidad = perimetro / (2 * Math.PI* radio);
		
		return new RasgoObjeto(this.getRasgoClase().getRasgo(),circularidad);
	}

}
