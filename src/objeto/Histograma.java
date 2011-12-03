package objeto;




public class Histograma {
	public static final String HISTOGRAMA_DELIMITER = ";";
	public static final String HISTOGRAMA_H = "H";
	public static final String HISTOGRAMA_S = "S";
	public static final String HISTOGRAMA_V = "V";
	public static final String HISTOGRAMA_GRIS = "GRIS";
	public static final String HISTOGRAMA_R = "R";
	public static final String HISTOGRAMA_G = "G";
	public static final String HISTOGRAMA_B = "B";
	public static final int MAX_VAL_HISTOGRAMA_H = 360;
	public static final int MAX_VAL_HISTOGRAMA_S = 100;
	public static final int MAX_VAL_HISTOGRAMA_V = 100;
	public static final int MAX_VAL_HISTOGRAMA_GRIS = 255;
	public static final int MAX_VAL_HISTOGRAMA_R = 255;
	public static final int MAX_VAL_HISTOGRAMA_G = 255;
	public static final int MAX_VAL_HISTOGRAMA_B = 255;

	
	private Long id;
	
	/**
	 * Tipo de histograma: R, G, V, H, S, V, etc.
	 */
	private String tipo;
	
	/**
	 * Cantidad maxima de valores que se pueden cargar para un tipo de histograma dado.
	 * Ej: para los tipos R, G y B el numero maximo de valores sera 128, 256, etc.
	 */
	private Integer maxValores;
		
	/**
	 * Arreglo de valores del histograma
	 */
	private double valores[] = null;
	
	/**
	 * El arreglo de valores del histograma separados por coma
	 */
	private String valoresString = null;

	public Histograma() {
		// TODO Auto-generated constructor stub
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public Integer getMaxValores() {
		return maxValores;
	}

	public void setMaxValores(Integer maxValores) {
		this.maxValores = maxValores;
	}

	public boolean equals(Object obj){
		if (obj == null)
			return false;
		if (!(obj instanceof Histograma))
			return false;
		Histograma r = (Histograma) obj;
		
		return this.getTipo().equals(r.getTipo());
	}
	
	public String toString(){
		return this.getTipo();
	}

	public double[] getValores() {
		return valores;
	}

	public void setValores(double[] values) {
		if (values != null){
			StringBuffer buffer = new StringBuffer();
			for(double val:values){
	            if (buffer.length() > 0) {
	                buffer.append(HISTOGRAMA_DELIMITER);
	            }
				buffer.append(val);
	        }
	        this.valoresString = buffer.toString();
	        this.maxValores = values.length;
			
		}
		else{
			this.valoresString = null;
		}
		this.valores = values;
	}

	public String getValoresString() {
		return valoresString;
	}

	public void setValoresString(String valuesString) {
		this.valoresString = valuesString;
		if (valoresString != null){
			String values[] = valuesString.split(HISTOGRAMA_DELIMITER);
			valores = new double[values.length];
			for(int i = 0; i < values.length; i++){
				valores[i] = Double.valueOf(values[i]);
			}
			this.maxValores = valores.length;
		}
	}

}
