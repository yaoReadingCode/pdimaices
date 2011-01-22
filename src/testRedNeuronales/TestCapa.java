package testRedNeuronales;

import redNeuronales.Red;

public class TestCapa {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		int cantidadNeuronaEntrada = 3;
		int cantidadNeuronaOculta = 15;
		int cantidadNeuronaSalida = 2;
		Red redNeuronal = new Red();
		
		redNeuronal.inicializarRed(cantidadNeuronaEntrada, cantidadNeuronaOculta, cantidadNeuronaSalida);
		redNeuronal.setValorNeurona(0, 0.8);
		redNeuronal.setValorNeurona(1, 0.7);
		redNeuronal.setValorNeurona(2, 0.2);
		
		//Calculamos el valor de cada neurona
		redNeuronal.feedForward();
		System.out.println("Error sin aprender:" + redNeuronal.calcularError());
		System.out.println("***********Sin aprender***************");
		for(int i=0 ; i< cantidadNeuronaSalida;i++){
			System.out.println("Neurona: " + i + " - Valor: " + redNeuronal.obtenerSalida(i));
		}
		System.out.println("***********Aprendiendo***************");
		double valorSalida = 0.4;
		/*for(int i=0 ; i< cantidadNeuronaSalida;i++){
			System.out.println("Neurona: " + i + " - ValorDeseado: " + valorSalida);
			redNeuronal.colocarSalidaDeseada(0, valorSalida);
			valorSalida +=0.1;
		}*/
		redNeuronal.colocarSalidaDeseada(0, 1);
		//redNeuronal.colocarSalidaDeseada(0, 0.9);
		redNeuronal.colocarSalidaDeseada(1, 0);
		
		
		System.out.println("Error aprendiendo1:" + redNeuronal.calcularError());
		redNeuronal.backPropagate();
		//Calculamos el valor de cada neurona
		redNeuronal.feedForward();
		System.out.println("***********Despues de aprender***************");
		for(int i=0 ; i< cantidadNeuronaSalida;i++){
			System.out.println("Neurona: " + i + " - Valor: " + redNeuronal.obtenerSalida(i));
		}
		System.out.println("Error aprendiendo:" + redNeuronal.calcularError());
		
		
		
		
		
		

	}

}
