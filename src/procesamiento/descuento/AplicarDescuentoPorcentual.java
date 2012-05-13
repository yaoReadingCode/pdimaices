package procesamiento.descuento;

public class AplicarDescuentoPorcentual extends AplicarDescuento {

	private float descuneto;
	
	public AplicarDescuentoPorcentual(float porcentajeDescuento){
		
		this.descuneto = porcentajeDescuento;
	}
	
	
	public float eval(float valorEsperado, float valorObtenido) {
		return (valorObtenido - valorEsperado) * descuneto;
	}

}
