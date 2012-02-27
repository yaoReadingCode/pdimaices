package procesamiento;

public class Rebaja {
	Norma norma;
	float descuento;
	public Norma getNorma() {
		return norma;
	}
	public float getDescuento() {
		return descuento;
	}
	
	public Rebaja(Norma n, float d){
		this.norma = n;
		this.descuento = d;
	}

}
