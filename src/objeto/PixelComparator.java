package objeto;

import java.util.Comparator;

public class PixelComparator implements Comparator<Pixel> {
	
	/**
	 * Flag que indica si se invierte la comparación
	 */
	private boolean invert = false;
	
	public PixelComparator() {
		super();
	}

	public PixelComparator(boolean invert) {
		super();
		this.invert = invert;
	}

	public boolean isInvert() {
		return invert;
	}

	public void setInvert(boolean invert) {
		this.invert = invert;
	}

	public int compare(Pixel o1, Pixel o2) {
		int comparacion = 0; 
		if (o1.getPeso() < o2.getPeso())
			comparacion = -1;
		if (o1.getPeso() > o2.getPeso())
			comparacion = 1;
		if (!invert)
			return (-1) * comparacion;
		return comparacion;
	}

}
