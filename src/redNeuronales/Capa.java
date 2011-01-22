package redNeuronales;

import java.util.Random;

public class Capa {
	//Numeros de Neuronas en la capa
	private int nNeuronas = 0;
	//Pesos
	private double[][] pesos;
	//Cambio de Pesos
	private double[][] cambioPesos;
	//valor de cada neurona
	private double valor[];
	//valor deseado de salida
	private double valorDeseado[];
	//Error obtenido
	private double error[];
	//valor de la bias por cada neurona
	private double bias[];
	//valor del peso para el bias de la neura
	private double pesosBias[];
	//valor del learning rate, es el valor del aprendisaje, mayor el valor impicara que aumentara mas rapido el peso
	
	private double learningRate = 0.8;
	//Capa padre de esta capa
	private Capa padre = null;
	//Capa hija de esta capa
	private Capa hija  = null;
	
	/**
	 * Constructor
	 */
	public Capa(){
	}
	
	public void iniciarCapa(){
		
		//Creamos el arreglo de los valores de la neurona
		valor = new double [nNeuronas];
		//Creamos el arreglo de los valores deseados de salida
		valorDeseado = new double [nNeuronas];
		//Creamos el arreglo de los errores
		error = new double [nNeuronas];
		//Creamos el arreglo de los bias
		bias = new double [nNeuronas];
		//Creamos el arreglo de los pesos de la bias
		pesosBias = new double [nNeuronas];
		
		//Creamos el arreglo para los pesos
		if (padre != null){
			pesos = new double[nNeuronas][padre.getnNeuronas()];
			cambioPesos = new double[nNeuronas][padre.getnNeuronas()];
			for(int i=0;i<nNeuronas;i++){
				for(int j=0;j<padre.getnNeuronas();j++){
					pesos[i][j]=1.0;
					cambioPesos[i][j]=0.0;
				}
			}
		}
		
		for(int j=0;j<nNeuronas;j++){
			valor[j]=0.0;
			valorDeseado[j]=0.0;
			error[j]=0.0;
			bias[j]=-1.0; //Puedo iniciarlo rn -1.0 o en 1.0
			pesosBias[j]=0.0;
		}
	}
	/**
	 * Esta funcion inicializa los pesos con valores aleatorios
	 */
	public void inicializarPesos(){
		Random rnd = new Random();
		for(int i=0;i<nNeuronas;i++){
			for(int j=0;j<padre.getnNeuronas();j++){
				pesos[i][j]=rnd.nextDouble() - rnd.nextDouble();
			}
		}
		for(int j=0;j<nNeuronas;j++){
			pesosBias[j]=rnd.nextDouble() - rnd.nextDouble();
		}
	}
	/**
	 * Esta funcion calcula el valor de cada neurona
	 */
	public void calcularNeuronas(){
		double valorN = 0.0;
		if (padre != null){
			//pesos = new double[nNeuronas][padre.getnNeuronas()];
			cambioPesos = new double[nNeuronas][padre.getnNeuronas()];
			for(int i=0;i<nNeuronas;i++){
				valorN = 0.0;
				for(int j=0;j<padre.getnNeuronas();j++){
					valorN = valorN + (padre.getValor()[j]*pesos[i][j]); 
				}
				valorN   = valorN + (bias[i]*pesosBias[i]);
				valor[i] = 1.0 / (1 + Math.exp(-valorN));
			}
		}
	}
	
	/**
	 * 
	 */
	public void calcularErrorCapaSalida(){
		for(int j=0;j<nNeuronas;j++){
			error[j]=(valorDeseado[j] - valor[j]) * valor[j] * (1.0 - valor[j]);
		}
	}
	/**
	 * 
	 */
	public void calcularErrorCapaOculta(){
		double sumatorio=0.0;
		for(int j=0;j<hija.getnNeuronas();j++){
			for(int i=0;i<nNeuronas;i++){
				sumatorio = sumatorio + (hija.getError()[j]*hija.getPesos()[j][i]);
			}
		}
		for(int i=0;i<nNeuronas;i++){
			error[i] = sumatorio * valor[i] * (1.0 - valor[i]);
		}
	}
	
	public void ajustePesos(){
		double delta = 0.0;
		for(int i=0;i<nNeuronas;i++){
			for(int j=0;j<padre.getnNeuronas();j++){
				delta = learningRate * error[i]*padre.getValor()[j];
				cambioPesos[i][j]=delta;
				pesos[i][j]+=delta;
			}
		}
	}
	public int getnNeuronas() {
		return nNeuronas;
	}
	public void setnNeuronas(int nNeuronas) {
		this.nNeuronas = nNeuronas;
	}

	public double[][] getPesos() {
		return pesos;
	}
	public void setPesos(double[][] pesos) {
		this.pesos = pesos;
	}
	public double[][] getCambioPesos() {
		return cambioPesos;
	}
	public void setCambioPesos(double[][] cambioPesos) {
		this.cambioPesos = cambioPesos;
	}
	public double[] getValor() {
		return valor;
	}
	public void setValor(double[] valor) {
		this.valor = valor;
	}
	public double[] getValorDeseado() {
		return valorDeseado;
	}
	public void setValorDeseado(double[] valorDeseado) {
		this.valorDeseado = valorDeseado;
	}
	public double[] getError() {
		return error;
	}
	public void setError(double[] error) {
		this.error = error;
	}
	public double[] getBias() {
		return bias;
	}
	public void setBias(double[] bias) {
		this.bias = bias;
	}
	public double[] getPesosBias() {
		return pesosBias;
	}
	public void setPesosBias(double[] pesosBias) {
		this.pesosBias = pesosBias;
	}
	public double getLearningRate() {
		return learningRate;
	}
	public void setLearningRate(double learningRate) {
		this.learningRate = learningRate;
	}
	public Capa getPadre() {
		return padre;
	}
	/**
	 * 
	 * @param padre
	 */
	public void setPadre(Capa padre) {
		this.padre = padre;
		
	}
	public Capa getHija() {
		return hija;
	}
	/**
	 * 
	 * @param hija
	 */
	public void setHija(Capa hija) {
		this.hija = hija;
	}
	
	
	
	
	
	
}
