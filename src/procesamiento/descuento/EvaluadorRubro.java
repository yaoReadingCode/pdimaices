package procesamiento.descuento;

import aplicarFiltros.Agrupador;

public abstract class EvaluadorRubro {
	
	private Double valor;
	
	public EvaluadorRubro() {
		super();
	}

	public EvaluadorRubro(Double valor) {
		super();
		this.valor = valor;
	}

	public abstract boolean cumpleNorma(Agrupador rubro);

	public Double getValor() {
		return valor;
	}

	public void setValor(Double valor) {
		this.valor = valor;
	}
}
