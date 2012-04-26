package procesamiento;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * The Class Standar.
 */
public class Standar {
	
	/** The normas. */
	private List<Norma> normas = new ArrayList<Norma>();
	
	/** The fuera standard. */
	private Norma fueraStandard= new Norma("FUERA DE STANDARD");

	/**
	 * Gets the normas.
	 *
	 * @return the normas
	 */
	public List<Norma> getNormas() {
		return normas;
	}

	/**
	 * Sets the normas.
	 *
	 * @param normas the new normas
	 */
	public void setNormas(List<Norma> normas) {
		this.normas = normas;
	}

	/**
	 * Instantiates a new standar.
	 */
	public Standar(){
		//Hacer esto desde archivo
		Norma nor= new Norma("Grado 1");
		nor.addGrado("Humedad", 14.5f);
		nor.addGrado("Peso Hectolitrico", -75);
		nor.addGrado("Grano Dañado", 3);
		nor.addGrado("Grano Partido", 2);
		nor.addGrado("Materia Extraña", 1);
		nor.addGrado("Semilla de Chamico", 2);
		nor.setRebaja(101); //Grado 1 = 1%
		
		normas.add(nor);
		
		Norma nor2= new Norma("Grado 2");
		nor2.addGrado("Humedad", 14.5f);
		nor2.addGrado("Peso Hectolitrico", -72);
		nor2.addGrado("Grano Dañado", 5);
		nor2.addGrado("Grano Partido", 3);
		nor2.addGrado("Materia Extraña", 1.5f);
		nor2.addGrado("Semilla de Chamico", 2);
		nor2.setRebaja(100); //Grado 1 = 0%
		
		normas.add(nor2);
		
		Norma nor3= new Norma("Grado 3");
		nor3.addGrado("Humedad", 14.5f);
		nor3.addGrado("Peso Hectolitrico", -69);		
		nor3.addGrado("Grano Dañado", 8);
		nor3.addGrado("Grano Partido", 5);
		nor3.addGrado("Materia Extraña", 2f);
		nor3.addGrado("Semilla de Chamico", 2);
		nor3.setRebaja(98.5f); //Grado 1 = -1,5%
		
		//Es una copia de Grado 3
		fueraStandard.addGrado("Humedad", 14.5f);
		fueraStandard.addGrado("Peso Hectolitrico", -69);	
		fueraStandard.addGrado("Grano Dañado", 8);
		fueraStandard.addGrado("Grano Partido", 5);
		fueraStandard.addGrado("Materia Extraña", 2f);
		fueraStandard.addGrado("Semilla de Chamico", 2);
		
		
		normas.add(nor3);
	}
	
	/**
	 * Gets the norma.
	 *
	 * @param valores the valores
	 * @return the norma
	 */
	public Rebaja getNorma(Map<String,Float> valores){
		Rebaja result;
		for(Iterator<Norma> n = normas.iterator();n.hasNext();){
			Norma norma = n.next();
			if (norma.isNorma(valores)){		
				result = new Rebaja(norma, norma.calcularDescuento(valores));
				return result;
			}
		}
		//Si no se encuentra dentro del estandar, se calcula el valor de Fuera del Standard
		result = new Rebaja(fueraStandard, fueraStandard.calcularDescuento(valores));
		return result;
	}

}
