package procesamiento;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import aplicarFiltros.Agrupador;

import procesamiento.descuento.AplicarDescuento;
import procesamiento.descuento.AplicarDescuentoDirecto;
import procesamiento.descuento.AplicarDescuentoPorcentual;
import procesamiento.descuento.EvaluadorRubroCantidad;
import procesamiento.descuento.EvaluadorRubroPorcentaje;


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
		nor.addGrado("Humedad", new EvaluadorRubroPorcentaje(14.5f));
		nor.addGrado("Peso Hectolitrico", new EvaluadorRubroPorcentaje(-75f));
		nor.addGrado("Grano Dañado", new EvaluadorRubroPorcentaje(3f));
		nor.addGrado("Grano Partido", new EvaluadorRubroPorcentaje(2f));
		nor.addGrado("Materia Extraña", new EvaluadorRubroPorcentaje(1f));
		nor.addGrado("Semilla de Chamico", new EvaluadorRubroCantidad(2f));
		nor.setRebaja(101); //Grado 1 = 1%
		
		normas.add(nor);
		
		Norma nor2= new Norma("Grado 2");
		nor2.addGrado("Humedad", new EvaluadorRubroPorcentaje(14.5f));
		nor2.addGrado("Peso Hectolitrico", new EvaluadorRubroPorcentaje(-72f));
		nor2.addGrado("Grano Dañado", new EvaluadorRubroPorcentaje(5f));
		nor2.addGrado("Grano Partido", new EvaluadorRubroPorcentaje(3f));
		nor2.addGrado("Materia Extraña", new EvaluadorRubroPorcentaje(1.5f));
		nor2.addGrado("Semilla de Chamico", new EvaluadorRubroCantidad(2f));
		nor2.setRebaja(100); //Grado 1 = 0%
		
		normas.add(nor2);
		
		Norma nor3= new Norma("Grado 3");
		nor3.addGrado("Humedad", new EvaluadorRubroPorcentaje(14.5f));
		nor3.addGrado("Peso Hectolitrico", new EvaluadorRubroPorcentaje(-69f));		
		nor3.addGrado("Grano Dañado", new EvaluadorRubroPorcentaje(8f));
		nor3.addGrado("Grano Partido", new EvaluadorRubroPorcentaje(5f));
		nor3.addGrado("Materia Extraña", new EvaluadorRubroPorcentaje(2f));
		nor3.addGrado("Semilla de Chamico", new EvaluadorRubroCantidad(2f));
		nor3.setRebaja(98.5f); //Grado 1 = -1,5%
		
		//Es una copia de Grado 3
		fueraStandard.addGrado("Humedad", new EvaluadorRubroPorcentaje(14.5f));
		fueraStandard.addGrado("Peso Hectolitrico", new EvaluadorRubroPorcentaje(-69f));		
		fueraStandard.addGrado("Grano Dañado", new EvaluadorRubroPorcentaje(8f));
		fueraStandard.addGrado("Grano Partido", new EvaluadorRubroPorcentaje(5f));
		fueraStandard.addGrado("Materia Extraña", new EvaluadorRubroPorcentaje(2f));
		fueraStandard.addGrado("Semilla de Chamico", new EvaluadorRubroCantidad(2f));
		
		Map<String, AplicarDescuento> descuento = new HashMap<String, AplicarDescuento>();
		
		AplicarDescuento descuentoCha = new AplicarDescuentoDirecto(1.3f);
		descuento.put("Semilla de Chamico", descuentoCha);
		
		AplicarDescuento descuentoDaniados = new AplicarDescuentoPorcentual(1f);
		descuento.put("Grano Dañado", descuentoDaniados);
		
		AplicarDescuento descuentoPartido = new AplicarDescuentoPorcentual(0.25f);
		descuento.put("Grano Partido", descuentoPartido);
		
		AplicarDescuento descuentoExtr = new AplicarDescuentoPorcentual(1f);
		descuento.put("Materia Extraña", descuentoExtr);
		
		AplicarDescuento descuentoHumedad = new AplicarDescuentoDirecto(1f);
		descuento.put("Humedad", descuentoHumedad);
		
		AplicarDescuento descuentoHectolitrico = new AplicarDescuentoPorcentual(0.4f);
		descuento.put("Peso Hectolitrico", descuentoHectolitrico);
		
		
		fueraStandard.setDescuento(descuento);
		
		
		normas.add(nor3);
	}
	
	/**
	 * Gets the norma.
	 *
	 * @param valores the valores
	 * @return the norma
	 */
	public Rebaja getNorma(Map<String,Agrupador> valores){
		Rebaja result;
		for(Iterator<Norma> n = normas.iterator();n.hasNext();){
			Norma norma = n.next();
			if (norma.isNorma(valores)){		
				result = new Rebaja(norma, norma.getRebaja());
				return result;
			}
		}
		//Si no se encuentra dentro del estandar, se calcula el valor de Fuera del Standard
		result = new Rebaja(fueraStandard, fueraStandard.calcularDescuento(valores));
		return result;
	}

}
