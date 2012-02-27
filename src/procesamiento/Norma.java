package procesamiento;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class Norma {
	private String name;
	
	private Map<String,Float> grado = new HashMap<String, Float>();
	
	private Map<String,Float> descuento = new HashMap<String, Float>();
	
	private float rebaja = 0.0f;

	public float getRebaja() {
		return rebaja;
	}

	public void setRebaja(float rebaja) {
		this.rebaja = rebaja;
	}

	public String getName() {
		return name;
	}
	
	public Map<String, Float> getDescuento() {
		return descuento;
	}

	public void setDescuento(Map<String, Float> descuento) {
		this.descuento = descuento;
	}

	public void setName(String name) {
		this.name = name;
	}


	public Map<String, Float> getGrado() {
		return grado;
	}

	public void setGrado(Map<String, Float> grado) {
		this.grado = grado;
	}

	public Norma(String name){
		this.name = name;
	}
	
	public void addGrado(String tipo, float valor){
		grado.put(tipo, valor);
		
	}
	
	@SuppressWarnings("unchecked")
	public boolean isNorma(Map<String,Float> valores){
		for(Iterator<Entry<String, Float>> iter = grado.entrySet().iterator(); iter.hasNext();){
			Map.Entry ent = (Map.Entry)iter.next();
			Float valor = (Float)ent.getValue();
			String tipo = (String)ent.getKey();
			Float valorRef = valores.get(tipo);
			if ((valorRef != null)&& (valor > valorRef)){
				return false;
			}
		}
		return true;
	}


	@SuppressWarnings("unchecked")
	public float calcularDescuento(Map<String, Float> valores) {
		if (rebaja == 0.0f) {
			float result = 1.0f;
			for (Iterator<Entry<String, Float>> iter = grado.entrySet()
					.iterator(); iter.hasNext();) {
				Map.Entry ent = (Map.Entry) iter.next();
				Float valor = (Float) ent.getValue();
				String tipo = (String) ent.getKey();
				Float valorRef = valores.get(tipo);
				if ((valorRef != null) && (valor > valorRef)) {
					result = result - (valor - valorRef);
				}
			}
			return result;
		}
		return rebaja;
	}
}
