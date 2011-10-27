package procesamiento.clasificacion;

import objeto.Objeto;
import objeto.RasgoClase;
import objeto.RasgoObjeto;

public class CantidadPuntosDivision extends EvaluadorRasgo {
	public CantidadPuntosDivision() {
		super();
		// TODO Auto-generated constructor stub
	}

	public CantidadPuntosDivision(RasgoClase rasgo, Double minimo, Double maximo) {
		super(rasgo, minimo, maximo);
	}

	 
	public RasgoObjeto calcularValor(Objeto objeto) {
		double valor = 0;
		if (objeto.getPuntosDivisionContorno() != null){
			valor = objeto.getPuntosDivisionContorno().size();
		}
		return new RasgoObjeto(this.getRasgoClase().getRasgo(),valor);
	}

}
