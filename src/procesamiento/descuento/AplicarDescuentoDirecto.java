package procesamiento.descuento;

import aplicarFiltros.Agrupador;

public class AplicarDescuentoDirecto extends AplicarDescuento {

	public AplicarDescuentoDirecto() {
		super();
	}

	public AplicarDescuentoDirecto(Double descuento){
		super(descuento);
	}
	
	public double eval(EvaluadorRubro valorEsperado, Agrupador valorObtenido) {
		return getDescuento();
	}

}
