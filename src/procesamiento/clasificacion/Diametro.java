
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

	public Diametro(RasgoClase rasgo, Double minimo, Double maximo) {
		super(rasgo, minimo, maximo);
	}

	public RasgoObjeto calcularValor(Objeto objeto) {
		try {
			Double diametro = Math.max(objeto.getAlto(), objeto.getAncho()) * ObjetoReferencia.getRelacionPixelMM();
			return new RasgoObjeto(this.getRasgoClase().getRasgo(),diametro);
		}
		catch(Exception e){
			return new RasgoObjeto(this.getRasgoClase().getRasgo(),0.0);
		}
	}
}
