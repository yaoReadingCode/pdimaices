package procesamiento.descuento;

import aplicarFiltros.Agrupador;

public abstract class AplicarDescuento {

	public abstract float eval(EvaluadorRubro valorEsperado, Agrupador valorObtenido);
	
}
