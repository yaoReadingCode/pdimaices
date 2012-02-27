package procesamiento;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class Standar {
	private List<Norma> normas = new ArrayList<Norma>();

	public List<Norma> getNormas() {
		return normas;
	}

	public void setNormas(List<Norma> normas) {
		this.normas = normas;
	}

	public Standar(){
		//Hacer esto desde archivo
		Norma nor= new Norma("Grado 1");
		nor.addGrado("Grano Dañado", 3);
		nor.addGrado("Grano Partido", 2);
		nor.addGrado("Materia Extraña", 1);
		nor.addGrado("Semilla de Chamico", 2);
		nor.setRebaja(1.5f);
		
		normas.add(nor);
		
		Norma nor2= new Norma("Grado 2");
		nor2.addGrado("Grano Dañado", 5);
		nor2.addGrado("Grano Partido", 3);
		nor2.addGrado("Materia Extraña", 1.5f);
		nor2.addGrado("Semilla de Chamico", 2);
		
		normas.add(nor2);
		
		Norma nor3= new Norma("Grado 3");
		nor3.addGrado("Grano Dañado", 8);
		nor3.addGrado("Grano Partido", 5);
		nor3.addGrado("Materia Extraña", 2f);
		nor3.addGrado("Semilla de Chamico", 2);
		
		normas.add(nor3);
	}
	
	public Rebaja getNorma(Map<String,Float> valores){
		Rebaja result;
		for(Iterator<Norma> n = normas.iterator();n.hasNext();){
			Norma norma = n.next();
			if (norma.isNorma(valores)){
				
				result = new Rebaja(norma, norma.calcularDescuento(valores));
				return result;
			}
		}
		Norma nor4= new Norma("FUERA DE STANDARD");
		result = new Rebaja(nor4, nor4.calcularDescuento(valores));
		return result;
	}

}
