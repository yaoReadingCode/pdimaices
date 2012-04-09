package procesamiento;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;


/**
 * The Class Norma.
 */
public class Norma {
	
	/** The name. */
	private String name;
	
	/** The grado. */
	private Map<String,Float> grado = new HashMap<String, Float>();
	
	/** The descuento. */
	private Map<String,Float> descuento = new HashMap<String, Float>();
	
	/** The rebaja. */
	private float rebaja = 0.0f;

	/**
	 * Gets the rebaja.
	 *
	 * @return the rebaja
	 */
	public float getRebaja() {
		return rebaja;
	}

	/**
	 * Sets the rebaja.
	 *
	 * @param rebaja the new rebaja
	 */
	public void setRebaja(float rebaja) {
		this.rebaja = rebaja;
	}

	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Gets the descuento.
	 *
	 * @return the descuento
	 */
	public Map<String, Float> getDescuento() {
		return descuento;
	}

	/**
	 * Sets the descuento.
	 *
	 * @param descuento the descuento
	 */
	public void setDescuento(Map<String, Float> descuento) {
		this.descuento = descuento;
	}

	/**
	 * Sets the name.
	 *
	 * @param name the new name
	 */
	public void setName(String name) {
		this.name = name;
	}


	/**
	 * Gets the grado.
	 *
	 * @return the grado
	 */
	public Map<String, Float> getGrado() {
		return grado;
	}

	/**
	 * Sets the grado.
	 *
	 * @param grado the grado
	 */
	public void setGrado(Map<String, Float> grado) {
		this.grado = grado;
	}

	/**
	 * Instantiates a new norma.
	 *
	 * @param name the name
	 */
	public Norma(String name){
		this.name = name;
	}
	
	/**
	 * Adds the grado.
	 *
	 * @param tipo the tipo
	 * @param valor the valor
	 */
	public void addGrado(String tipo, float valor){
		grado.put(tipo, valor);
		
	}
	
	/**
	 * Checks if is norma.
	 *
	 * @param valores the valores
	 * @return true, if is norma
	 */
	@SuppressWarnings("unchecked")
	public boolean isNorma(Map<String,Float> valores){
		for(Iterator<Entry<String, Float>> iter = grado.entrySet().iterator(); iter.hasNext();){
			Map.Entry ent = (Map.Entry)iter.next();
			Float valorRef = (Float)ent.getValue();
			String tipo = (String)ent.getKey();
			Float valor = valores.get(tipo);
			if ((valorRef != null)&& (valor > valorRef)){
				return false;
			}
		}
		return true;
	}


	/**
	 * Calcular descuento.
	 *
	 * @param valores the valores
	 * @return the float
	 */
	@SuppressWarnings("unchecked")
	public float calcularDescuento(Map<String, Float> valores) {
		if (rebaja == 0.0f) {
			float result = 98.5f;
			for (Iterator<Entry<String, Float>> iter = grado.entrySet()
					.iterator(); iter.hasNext();) {
				Map.Entry ent = (Map.Entry) iter.next();
				Float valorRef = (Float) ent.getValue();
				String tipo = (String) ent.getKey();
				Float valor = valores.get(tipo);
				if ((valorRef != null) && (valor > valorRef)) {
					result = result + ((valorRef - valor)); //Es 1.5% mas lo que esta fuera del standard
				}
			}
			return result;
		}
		return rebaja;
	}
}
