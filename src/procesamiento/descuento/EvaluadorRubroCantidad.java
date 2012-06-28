package procesamiento.descuento;

import aplicarFiltros.Agrupador;

public class EvaluadorRubroCantidad extends EvaluadorRubro {

	public EvaluadorRubroCantidad(Float valor) {
		super(valor);
	}

	@Override
	public boolean cumpleNorma(Agrupador rubro) {
		return rubro.getCantidad() > getValor();
	}

}
