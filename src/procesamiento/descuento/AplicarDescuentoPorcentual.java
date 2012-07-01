package procesamiento.descuento;

import aplicarFiltros.Agrupador;

public class AplicarDescuentoPorcentual extends AplicarDescuento {

	public AplicarDescuentoPorcentual() {
		super();
	}

	public AplicarDescuentoPorcentual(Double porcentajeDescuento){
		
		super(porcentajeDescuento);
	}
	
	public double eval(EvaluadorRubro valorEsperado, Agrupador valorObtenido) {
		return (valorObtenido.getPorcentaje() - valorEsperado.getValor()) * getDescuento();
	}
}
