package procesamiento.descuento;

import aplicarFiltros.Agrupador;

public abstract class AplicarDescuento {
	
	private Double descuento;

	public abstract double eval(EvaluadorRubro valorEsperado, Agrupador valorObtenido);

	public AplicarDescuento() {
		super();
	}

	public AplicarDescuento(Double descuento) {
		super();
		this.descuento = descuento;
	}

	public Double getDescuento() {
		return descuento;
	}

	public void setDescuento(Double descuento) {
		this.descuento = descuento;
	}

}
