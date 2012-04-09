package procesamiento;


/**
 * The Class Rebaja.
 */
public class Rebaja {
	
	/** The norma. */
	Norma norma;
	
	/** The descuento. */
	float descuento;
	
	/**
	 * Gets the norma.
	 *
	 * @return the norma
	 */
	public Norma getNorma() {
		return norma;
	}
	
	/**
	 * Gets the descuento.
	 *
	 * @return the descuento
	 */
	public float getDescuento() {
		return descuento;
	}
	
	/**
	 * Instantiates a new rebaja.
	 *
	 * @param n the n
	 * @param d the d
	 */
	public Rebaja(Norma n, float d){
		this.norma = n;
		this.descuento = d;
	}

}
