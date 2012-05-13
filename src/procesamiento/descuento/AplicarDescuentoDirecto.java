package procesamiento.descuento;

public class AplicarDescuentoDirecto extends AplicarDescuento {

	private float descuentoDirecto;
	public AplicarDescuentoDirecto(float descuento){
		this.descuentoDirecto = descuento;
	}
	public float eval(float valorEsperado, float valorObtenido) {
		return descuentoDirecto;
	}

}
