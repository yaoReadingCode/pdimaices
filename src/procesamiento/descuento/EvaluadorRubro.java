package procesamiento.descuento;

import aplicarFiltros.Agrupador;

public abstract class EvaluadorRubro {
	
	private Float valor;
	
	public EvaluadorRubro(Float valor) {
		super();
		this.valor = valor;
	}

	public abstract boolean cumpleNorma(Agrupador rubro);

	public Float getValor() {
		return valor;
	}

	public void setValor(Float valor) {
		this.valor = valor;
	}
}
