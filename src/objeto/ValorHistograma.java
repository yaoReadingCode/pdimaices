package objeto;

public class ValorHistograma {
	
	private Long id;
	
	private Histograma histograma;
	
	private Double valor;

	public ValorHistograma() {
		// TODO Auto-generated constructor stub
	}

	public ValorHistograma(Histograma histograma, Double valor) {
		super();
		this.histograma = histograma;
		this.valor = valor;
		
		//Borrar
	}


	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Histograma getHistograma() {
		return histograma;
	}

	public void setHistograma(Histograma histograma) {
		this.histograma = histograma;
	}

	public Double getValor() {
		return valor;
	}

	public void setValor(Double valor) {
		this.valor = valor;
	}

	
}
