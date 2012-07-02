package procesamiento;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import objeto.Grado;
import objeto.RubroCalidad;
import objeto.ToleranciaRubro;
import procesamiento.clasificacion.Clasificador;
import procesamiento.descuento.AplicarDescuento;
import aplicarFiltros.Agrupador;


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
	public Standar(Clasificador clasificador){
		List<Grado> grados = clasificador.getGradosCalidad();
		for(Grado grado: grados){
			Norma norma = createNorma(grado);
			if (grado.isFueraEstandar())
				fueraStandard = norma;
			else{
				normas.add(norma);
			}
		}
		/*
		//Hacer esto desde archivo
		Norma nor= new Norma("Grado 1");
		nor.addGrado("Humedad", new EvaluadorRubroPorcentaje(14.5));
		nor.addGrado("Peso Hectolitrico", new EvaluadorRubroPorcentaje(-75.0));
		nor.addGrado("Grano Dañado", new EvaluadorRubroPorcentaje(3.0));
		nor.addGrado("Grano Partido", new EvaluadorRubroPorcentaje(2.0));
		nor.addGrado("Materia Extraña", new EvaluadorRubroPorcentaje(1.0));
		nor.addGrado("Semilla de Chamico", new EvaluadorRubroCantidad(2.0));
		nor.setRebaja(101); //Grado 1 = 1%
		
		normas.add(nor);
		
		Norma nor2= new Norma("Grado 2");
		nor2.addGrado("Humedad", new EvaluadorRubroPorcentaje(14.5));
		nor2.addGrado("Peso Hectolitrico", new EvaluadorRubroPorcentaje(-72.0));
		nor2.addGrado("Grano Dañado", new EvaluadorRubroPorcentaje(5.0));
		nor2.addGrado("Grano Partido", new EvaluadorRubroPorcentaje(3.0));
		nor2.addGrado("Materia Extraña", new EvaluadorRubroPorcentaje(1.5));
		nor2.addGrado("Semilla de Chamico", new EvaluadorRubroCantidad(2.0));
		nor2.setRebaja(100); //Grado 1 = 0%
		
		normas.add(nor2);
		
		Norma nor3= new Norma("Grado 3");
		nor3.addGrado("Humedad", new EvaluadorRubroPorcentaje(14.5));
		nor3.addGrado("Peso Hectolitrico", new EvaluadorRubroPorcentaje(-69.0));		
		nor3.addGrado("Grano Dañado", new EvaluadorRubroPorcentaje(8.0));
		nor3.addGrado("Grano Partido", new EvaluadorRubroPorcentaje(5.0));
		nor3.addGrado("Materia Extraña", new EvaluadorRubroPorcentaje(2.0));
		nor3.addGrado("Semilla de Chamico", new EvaluadorRubroCantidad(2.0));
		nor3.setRebaja(98.5f); //Grado 1 = -1,5%
		
		//Es una copia de Grado 3
		fueraStandard.addGrado("Humedad", new EvaluadorRubroPorcentaje(14.5));
		fueraStandard.addGrado("Peso Hectolitrico", new EvaluadorRubroPorcentaje(-69.0));		
		fueraStandard.addGrado("Grano Dañado", new EvaluadorRubroPorcentaje(8.0));
		fueraStandard.addGrado("Grano Partido", new EvaluadorRubroPorcentaje(5.0));
		fueraStandard.addGrado("Materia Extraña", new EvaluadorRubroPorcentaje(2.0));
		fueraStandard.addGrado("Semilla de Chamico", new EvaluadorRubroCantidad(2.0));
		
		Map<String, AplicarDescuento> descuento = new HashMap<String, AplicarDescuento>();
		
		AplicarDescuento descuentoCha = new AplicarDescuentoDirecto(1.3);
		descuento.put("Semilla de Chamico", descuentoCha);
		
		AplicarDescuento descuentoDaniados = new AplicarDescuentoPorcentual(1.0);
		descuento.put("Grano Dañado", descuentoDaniados);
		
		AplicarDescuento descuentoPartido = new AplicarDescuentoPorcentual(0.25);
		descuento.put("Grano Partido", descuentoPartido);
		
		AplicarDescuento descuentoExtr = new AplicarDescuentoPorcentual(1.0);
		descuento.put("Materia Extraña", descuentoExtr);
		
		AplicarDescuento descuentoHumedad = new AplicarDescuentoDirecto(1.0);
		descuento.put("Humedad", descuentoHumedad);
		
		AplicarDescuento descuentoHectolitrico = new AplicarDescuentoPorcentual(0.4);
		descuento.put("Peso Hectolitrico", descuentoHectolitrico);
		
		
		fueraStandard.setDescuento(descuento);
		
		
		normas.add(nor3);*/
	}
	
	/**
	 * Crea una instancia de Norma a partir de una instancia de Grado
	 * @param grado
	 * @return
	 */
	private Norma createNorma(Grado grado) {
		Norma n = new Norma(grado.getNombre());
		n.setRebaja(grado.getRebaja());
		Map<RubroCalidad, AplicarDescuento> descuento = new HashMap<RubroCalidad, AplicarDescuento>();
		for(ToleranciaRubro tr:grado.getToleranciaRubros()){
			n.addGrado(tr.getRubro(),tr.getEvaluadorValorRubro());
			if(tr.getDescuento() != null){
				descuento.put(tr.getRubro(), tr.getEvaluadorDescuento());
			}
		}
		n.setDescuento(descuento);
		return n;
	}

	/**
	 * Gets the norma.
	 *
	 * @param valores the valores
	 * @return the norma
	 */
	public Rebaja getNorma(Map<RubroCalidad,Agrupador> valores){
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
