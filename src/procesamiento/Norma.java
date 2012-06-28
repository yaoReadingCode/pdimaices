package procesamiento;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import aplicarFiltros.Agrupador;

import procesamiento.descuento.AplicarDescuento;
import procesamiento.descuento.EvaluadorRubro;


/**
 * The Class Norma.
 */
public class Norma {
	
	/** The name. */
	private String name;
	
	/** The grado. */
	private Map<String,EvaluadorRubro> grado = new HashMap<String, EvaluadorRubro>();
	
	/** The descuento. */
	private Map<String,AplicarDescuento> descuento = new HashMap<String, AplicarDescuento>();
	
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
	public Map<String, AplicarDescuento> getDescuento() {
		return descuento;
	}

	/**
	 * Sets the descuento.
	 *
	 * @param descuento the descuento
	 */
	public void setDescuento(Map<String, AplicarDescuento> descuento) {
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
	public Map<String, EvaluadorRubro> getGrado() {
		return grado;
	}

	/**
	 * Sets the grado.
	 *
	 * @param grado the grado
	 */
	public void setGrado(Map<String, EvaluadorRubro> grado) {
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
	public void addGrado(String tipo, EvaluadorRubro evaluadorRubro){
		grado.put(tipo, evaluadorRubro);
		
	}
	
	/**
	 * Checks if is norma.
	 *
	 * @param valores the valores
	 * @return true, if is norma
	 */
	@SuppressWarnings("unchecked")
	public boolean isNorma(Map<String,Agrupador> valores){
		for(Iterator<Entry<String, EvaluadorRubro>> iter = grado.entrySet().iterator(); iter.hasNext();){
			Map.Entry ent = (Map.Entry)iter.next();
			EvaluadorRubro valorRef = (EvaluadorRubro)ent.getValue();
			String tipo = (String)ent.getKey();
			Agrupador valor = valores.get(tipo);
			if ((valorRef != null)&& valorRef.cumpleNorma(valor)){
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
	public float calcularDescuento(Map<String, Agrupador> valores) {
		if (rebaja == 0.0f) {
			float result = 98.5f;//Es 1.5% mas lo que esta fuera del standard
			for (Iterator<Entry<String, EvaluadorRubro>> iter = grado.entrySet()
					.iterator(); iter.hasNext();) {
				Map.Entry ent = (Map.Entry) iter.next();
				EvaluadorRubro valorRef = (EvaluadorRubro) ent.getValue();
				String tipo = (String) ent.getKey();
				Agrupador valor = valores.get(tipo);
				AplicarDescuento des = this.descuento.get(tipo);
				
				
				if ((valorRef != null) && valorRef.cumpleNorma(valor)) {
					result = result - (des.eval(valorRef, valor)); 
				}
			}
			return result;
		}
		return rebaja;
	}
}
