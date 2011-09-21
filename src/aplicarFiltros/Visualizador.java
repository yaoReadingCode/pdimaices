package aplicarFiltros;


public class Visualizador  {
	private static Progress progreso = null;
	private static long tiempoDormido = 100;
	/**
	 * @param args
	 */
	
	
	public static void iniciarProgreso(){
		progreso = new Progress();
		progreso.inicializarProgressBar();
		progreso.dibujar();
		System.out.println("Inicio ProgressBar");
		
	}
	
	public static void aumentarProgreso(int valor, String texto){
		
		progreso.aumentar(valor, texto);
		System.out.println("Aumentar " + valor );
		
	}

	/**
	 * Agrega un texto a visualizar en el log
	 */
	public static void addLogInfo(String texto){
		progreso.addLogInfo(texto);
	}

	
	public static void terminar(){
		progreso.finalizar();
		System.out.println("Fianlizar");
	}
	
	public static void pausarAplicacion(){
		try {
			Thread.sleep(tiempoDormido);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	


}
