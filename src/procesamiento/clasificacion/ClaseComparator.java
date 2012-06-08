package procesamiento.clasificacion;

import java.util.Comparator;

import objeto.Clase;

public class ClaseComparator implements Comparator<Clase> {

	@Override
	public int compare(Clase c1, Clase c2) {
		if (c1 != null && c2 != null){
			return c1.getDescripcion().compareTo(c2.getDescripcion());
		}
		return 0;
	}

}
