package procesamiento.clasificacion;

import java.util.Comparator;

public class EvaluadorRasgoComparator implements Comparator<EvaluadorRasgo> {
	/**
	 * Compara los pesos de los objetos RasgoClase pasados como parámetro.
	 * Ordena en orden decreciente de peso.
	 */
	public int compare(EvaluadorRasgo o1, EvaluadorRasgo o2) {
		if (o1.getRasgoClase().getPeso() > o2.getRasgoClase().getPeso())
			return -1;
		if (o1.getRasgoClase().getPeso() < o2.getRasgoClase().getPeso())
			return 1;
		return 0;
	}

}
