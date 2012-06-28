package procesamiento.descuento;

import aplicarFiltros.Agrupador;

public class EvaluadorRubroPorcentaje extends EvaluadorRubro {

	public EvaluadorRubroPorcentaje(Float valor) {
		super(valor);
	}

	@Override
	public boolean cumpleNorma(Agrupador rubro) {
		return rubro.getPorcentaje() > getValor();
	}

}
