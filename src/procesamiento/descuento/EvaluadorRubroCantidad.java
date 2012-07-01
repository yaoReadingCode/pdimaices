package procesamiento.descuento;

import aplicarFiltros.Agrupador;

public class EvaluadorRubroCantidad extends EvaluadorRubro {
	
	public EvaluadorRubroCantidad() {
		super();
	}

	public EvaluadorRubroCantidad(Double valor) {
		super(valor);
	}

	@Override
	public boolean cumpleNorma(Agrupador rubro) {
		return rubro.getCantidad() > getValor();
	}

}
