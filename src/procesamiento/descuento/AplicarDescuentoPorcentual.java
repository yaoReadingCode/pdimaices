package procesamiento.descuento;

import aplicarFiltros.Agrupador;

public class AplicarDescuentoPorcentual extends AplicarDescuento {

	private float descuneto;
	
	public AplicarDescuentoPorcentual(float porcentajeDescuento){
		
		this.descuneto = porcentajeDescuento;
	}
	
	
	public float eval(EvaluadorRubro valorEsperado, Agrupador valorObtenido) {
		return (valorObtenido.getPorcentaje() - valorEsperado.getValor()) * descuneto;
	}

}
