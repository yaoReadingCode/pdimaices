package procesamiento.clasificacion;

import objeto.Objeto;
import objeto.RasgoClase;
/**
 * Evalua el ancho de un objeto
 *
 */
public class Ancho extends EvaluadorRasgo {

	public Ancho() {
		super();
	}

	public Ancho(RasgoClase rasgo, Double valor, Double desvioEstandar) {
		super(rasgo, valor, desvioEstandar);
	}

	public Double calcularValor(Objeto objeto) {
		return objeto.getAncho();
	}

}
