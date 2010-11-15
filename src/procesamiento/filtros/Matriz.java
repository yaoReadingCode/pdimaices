package procesamiento.filtros;

public class Matriz {
	private String nombre;
	private float[] values = { 0, 0, 0, 0, 0, 0, 0, 0, 0 };
	private boolean habilitado = true;

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public float[] getValues() {
		return values;
	}

	public void setValues(float[] values) {
		this.values = values;
	}

	 
	public String toString() {
		return getNombre() + " ["+((habilitado)? "Habilitado":"Deshabilitado")+ "]";
	}
	
	public boolean isHabilitado() {
		return habilitado;
	}

	public void setHabilitado(boolean habilitado) {
		this.habilitado = habilitado;
	}

	 
	public boolean equals(Object m) {
		if (m == null)
			return false;
		if (!(m instanceof Matriz))
			return false;
		Matriz matriz = (Matriz) m;
		if (getNombre() != null)
			return getNombre().equals(matriz.getNombre());
		return false;
	}

}
