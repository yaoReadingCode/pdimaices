package procesamiento.clasificacion;

import objeto.Objeto;
import objeto.RasgoClase;

/**
 * Evalua el alto de un objeto 
 */
public class Alto extends EvaluadorRasgo {
	public Alto() {
		super();
	}

	public Alto(RasgoClase rasgo, Double valor, Double desvioEstandar) {
		super(rasgo, valor, desvioEstandar);
	}

	public Double calcularValor(Objeto objeto) {
		return objeto.getAlto();
	}
}
