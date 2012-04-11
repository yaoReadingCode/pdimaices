package procesamiento.clasificacion;

import objeto.Objeto;
import objeto.RasgoClase;
import objeto.RasgoObjeto;
/**
 * Evalua el Aspact Ratio de un objeto en base a su Area:
 * Area del objeto / Area del rectangulo que contiene al objeto
 * @author Oscar Giorgetti
 *
 */
public class AspectRatioArea extends EvaluadorRasgo {

	public AspectRatioArea() {
		super();
	}


	public AspectRatioArea(RasgoClase rasgo, Double minimo, Double maximo) {
		super(rasgo, minimo, maximo);
	}

	public RasgoObjeto calcularValor(Objeto objeto) {
		double altoMRC = objeto.getAlto();
		double anchoMRC = objeto.getAncho();
	
		double areaMRC = altoMRC * anchoMRC;
		double aspectRatio = areaMRC / objeto.getArea();
		return new RasgoObjeto(this.getRasgoClase().getRasgo(),aspectRatio);
	}
}
