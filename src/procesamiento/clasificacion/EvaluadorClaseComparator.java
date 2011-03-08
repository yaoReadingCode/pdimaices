package procesamiento.clasificacion;

import java.util.Comparator;

public class EvaluadorClaseComparator implements Comparator<EvaluadorClase> {

	@Override
	public int compare(EvaluadorClase arg0, EvaluadorClase arg1) {
		return arg0.getClase().getOrdenEvaluacion().compareTo(arg1.getClase().getOrdenEvaluacion());
	}

}
