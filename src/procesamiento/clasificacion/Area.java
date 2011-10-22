package procesamiento.clasificacion;

import objeto.Objeto;
import objeto.RasgoClase;
import objeto.RasgoObjeto;

public class Area extends EvaluadorRasgo {
	
	public Area() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Area(RasgoClase rasgo, Double valor, Double desvioEstandar) {
		super(rasgo, valor, desvioEstandar);
	}

	 
	public RasgoObjeto calcularValor(Objeto objeto) {
		double relacionPixelCm = getObjetoReferencia().getRelacionPixelMM();
		double valor = new Double(objeto.getArea()*relacionPixelCm);
		return new RasgoObjeto(this.getRasgoClase().getRasgo(),valor);
	}

}
