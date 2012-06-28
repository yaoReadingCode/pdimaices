package procesamiento.descuento;

import aplicarFiltros.Agrupador;

public class AplicarDescuentoDirecto extends AplicarDescuento {

	private float descuentoDirecto;
	public AplicarDescuentoDirecto(float descuento){
		this.descuentoDirecto = descuento;
	}
	public float eval(EvaluadorRubro valorEsperado, Agrupador valorObtenido) {
		return descuentoDirecto;
	}

}
