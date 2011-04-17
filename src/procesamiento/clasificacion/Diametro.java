
package procesamiento.clasificacion;

import objeto.Objeto;
import objeto.RasgoClase;
import objeto.RasgoObjeto;

/**
 * Evalua el alto de un objeto 
 */
public class Diametro extends EvaluadorRasgo {
	public Diametro() {
		super();
	}

	public Diametro(RasgoClase rasgo, Double valor, Double desvioEstandar) {
		super(rasgo, valor, desvioEstandar);
	}

	public RasgoObjeto calcularValor(Objeto objeto) {
		Double diametro = Math.max(objeto.getAlto(), objeto.getAncho());
		return new RasgoObjeto(this.getRasgoClase().getRasgo(),diametro);
	}
}
