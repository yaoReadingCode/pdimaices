package redNeuronales;

public class Red {
	private Capa entrada = new Capa();
	private Capa oculta = new Capa();
	private Capa salida = new Capa();

	public Capa getEntrada() {
		return entrada;
	}

	public void setEntrada(Capa entrada) {
		this.entrada = entrada;
	}

	public Capa getOculta() {
		return oculta;
	}

	public void setOculta(Capa oculta) {
		this.oculta = oculta;
	}

	public Capa getSalida() {
		return salida;
	}

	public void setSalida(Capa salida) {
		this.salida = salida;
	}

	public void inicializarRed(int nEntrada, int nOculta, int nSalida) {
		// Entrada
		entrada.setnNeuronas(nEntrada);
		entrada.iniciarCapa();

		// Capa Oculta

		oculta.setnNeuronas(nOculta);
		oculta.setPadre(entrada);

		entrada.setHija(oculta);

		salida.setnNeuronas(nSalida);
		salida.setPadre(oculta);

		oculta.setHija(salida);

		oculta.iniciarCapa();
		salida.iniciarCapa();

		// La capa de entrada no tiene pesos
		oculta.inicializarPesos();
		salida.inicializarPesos();
	}

	public void setValorNeurona(int neurona, double valor) {
		if (neurona >= 0)
			if (neurona < entrada.getnNeuronas())
				entrada.getValor()[neurona] = valor;
	}

	public double obtenerSalida(int neurona) {
		return salida.getValor()[neurona];
	}

	public void colocarSalidaDeseada(int neurona, double valor) {
		if (neurona >= 0)
			if (neurona < salida.getnNeuronas())
				salida.getValorDeseado()[neurona] = valor;

	}

	public void feedForward() {
		entrada.calcularNeuronas();
		oculta.calcularNeuronas();
		salida.calcularNeuronas();
	}

	public void backPropagate() {
		salida.calcularErrorCapaSalida();
		salida.ajustePesos();
		oculta.calcularErrorCapaOculta();
		oculta.ajustePesos();
	}

	public int idValorMasAlto() {
		double maximo = salida.getValor()[0];
		int indice = 0;
		for (int j = 0; j < salida.getnNeuronas(); j++) {
			if (salida.getValor()[j] > maximo) {
				maximo = salida.getValor()[j];
				indice = j;
			}
		}
		return indice;
	}

	public double calcularError() {
		double error = 0.0;
		for (int j = 0; j < salida.getnNeuronas(); j++) {
			error += (salida.getValor()[j] - salida.getValorDeseado()[j])
					* (salida.getValor()[j] - salida.getValorDeseado()[j]);
		}
		error /= salida.getnNeuronas();
		return error;
	}
}
