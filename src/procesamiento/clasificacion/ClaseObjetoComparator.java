package procesamiento.clasificacion;

import java.util.Comparator;

import objeto.ClaseObjeto;
/**
 * Comparador de clases de un objeto según la distancia promedio
 * @author oscar
 *
 */
public class ClaseObjetoComparator implements Comparator<ClaseObjeto> {

	public int compare(ClaseObjeto clase1, ClaseObjeto clase2) {
		if (clase1.getDistanciaPromedio() != null && clase2.getDistanciaPromedio() != null){
			int compare = clase1.getDistanciaPromedio().compareTo(clase2.getDistanciaPromedio());
			if (compare != 0)
				return compare;
			else
				return clase1.getClase().getOrdenEvaluacion().compareTo(clase2.getClase().getOrdenEvaluacion());
				
		}
		if (clase1.getDistanciaPromedio() != null)
			return -1;
		if (clase2.getDistanciaPromedio() != null)
			return 1;
		return 0;
	}

}
