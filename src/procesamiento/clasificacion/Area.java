package procesamiento.clasificacion;

import objeto.Objeto;
import objeto.Rasgo;

public class Area extends EvaluadorRasgo {
	
	public Area() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Area(Rasgo rasgo, Double valor, Double desvioEstandar) {
		super(rasgo, valor, desvioEstandar);
	}

	 
	public Double calcularValor(Objeto objeto) {
		return new Double(objeto.getArea());
	}

}
