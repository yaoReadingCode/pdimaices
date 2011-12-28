package procesamiento;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.media.jai.PlanarImage;
import javax.media.jai.TiledImage;
import javax.swing.JFrame;

import objeto.ClaseObjeto;
import objeto.Objeto;
import objeto.Pixel;
import objeto.Rasgo;
import objeto.RasgoObjeto;
import procesamiento.clasificacion.ClaseObjetoComparator;
import procesamiento.clasificacion.Clasificador;
import procesamiento.clasificacion.EvaluadorClase;
import procesamiento.clasificacion.EvaluadorClaseComparator;
import procesamiento.clasificacion.ObjetoReferencia;
import aplicarFiltros.FrameResultado;
import aplicarFiltros.Visualizador;



/**
 * Comando que realiza la detección de los objetos. 
 * @author oscar
 *
 */
public class DetectarObjetos extends AbstractImageCommand { 
	
	private static PlanarImage originalImage;
	private HSVRange hsvRange;
	private List<Objeto> objetos = new ArrayList<Objeto>();
	

	/**
	 * Constructor
	 * @param image Imagen a procesar
	 * @param originalImage Imagen original sin ningún procesamiento
	 * @param hsvRange Rango de valores HSV para detectar el fondo de la imagen
	 * @throws Exception 
	 */
	public DetectarObjetos(PlanarImage image, PlanarImage originalImage,
			HSVRange hsvRange,Clasificador clasificador) throws Exception {
		super(image);
		this.originalImage = originalImage;
		this.hsvRange = hsvRange;
		setClasificador(clasificador);
		init();
	}
	
	/**
	 * Inicialización de la clase
	 * @throws Exception 
	 */
	protected void init() throws Exception{
		/*
		//AspectRatio aspectRatio = new AspectRatio("Aspect Ratio", 0.9, 0.1);
		Circularidad circularidad = new Circularidad("Circularidad", 1.0, 0.2);
		AspectRatio aspectRadio = new AspectRatio("AspectRadio", 1.0, 0.4);
		Area area = new Area("Area", 3000.0,2000.0);
		Set<EvaluadorRasgo> rasgos = new HashSet<EvaluadorRasgo>();
		rasgos.add(circularidad);
		rasgos.add(aspectRadio);
		rasgos.add(area);
		EvaluadorClase maizAmarillo = getClasificador().getEvaluadorClase(CLASE_MAIZ_AMARILLO);
		maizAmarillo.setColor(Color.GREEN);
		
		getClasificador().getClasificacion().put(maizAmarillo, new ArrayList<Objeto>());
		*/
		getClasificador().inicializarClasificacion();

	}

	/**
	 * Implementa el algoritmo de detección de los objetos.
	 * Reliza la siguiente secuencia de procesamiento:
	 * 	1- Elimina el fondo de la imagen
	 * 	2- Binariza la imagen
	 * 	3- Opening de la imagen
	 * 	4- Closing de la imagen
	 * 	5- Detecta el contorno el contorno grueso de los objetos
	 * 	6- Detecta el contorno exterior de 1 pixel de los objetos
	 */
	public PlanarImage execute() {
		if (getOriginalImage() != null && getHsvRange() != null) {
			Date fechaInicio = new Date(System.currentTimeMillis()); 

			Visualizador.aumentarProgreso(0, "Binarizando...");
			Binarizar ef = new Binarizar(getOriginalImage(), getHsvRange());
			PlanarImage binaryImage = ef.execute();
			PlanarImage output = binaryImage;
			/*
			Visualizador.aumentarProgreso(15, "Detectando Contorno Grueso...");
			DetectarContornoGrueso dcg = new DetectarContornoGrueso(output);
			output = dcg.execute();
			*/
			Visualizador.aumentarProgreso(20, "Detectando Contorno...");
			DetectarContorno dc = new DetectarContorno(output, getOriginalImage(), new Color(100, 100, 100), Color.RED);
			dc.setBinaryImage(binaryImage);
			dc.setClasificador(getClasificador());
			dc.setRangeFondo(getHsvRange());
			output = dc.execute();
			
			List<Objeto> objetos = dc.getObjetos();			
			
			setObjetos(objetos);
			
			Visualizador.aumentarProgreso(30, "Clasificando objetos...");
			clasificarObjetos(dc.getObjetos());
			
			Visualizador.aumentarProgreso(10, "Pintando contorno...");
			output = pintarContorno(getOriginalImage());
			
			ef.postExecute();
			dc.postExecute(); 
			Visualizador.aumentarProgreso(10, "");
			
			visualizarResultado();
			System.out.println("Inicio: " + (new Date(System.currentTimeMillis())).toString() + " - FIN: " + fechaInicio.toString());
			System.out.println("TIEMPO TOTAL DE CALCULO: " + ((System.currentTimeMillis() - fechaInicio.getTime())/1000));
			return output; 
		}
		return null;
	}
	
	private void visualizarResultado() {
		FrameResultado frame = new FrameResultado();
		frame.setClasificador( getClasificador());
		frame.setResultados();

		frame.setTitle("Clasificación");
		frame.setSize(1130, 700);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		//frame.setModal(true);
		frame.pack();
		frame.setResizable(true);
		frame.setVisible(true);
		
		//JOptionPane.showInputDialog(frame);
		
		
	}

	/**
	 * Pinta el pixel (x,y) de la imagen con el color pasado como parámetro
	 * 
	 * @param image
	 * @param x
	 * @param y
	 * @param color
	 */
	public void pintarPixel(TiledImage image, int x, int y, Color color) {
		int pixel[] = {color.getRed(), color.getGreen(), color.getBlue()};
		ImageUtil.writePixel(x, y, pixel, image);
	}

	
	/**
	 * Pinta el contorno de los objetos detectados
	 * @return
	 */
	public PlanarImage pintarContorno(PlanarImage image) {
		if (getOriginalImage() != null){
			TiledImage ti = ImageUtil.createTiledImage(image, ImageUtil.tileWidth, ImageUtil.tileHeight);
			Graphics2D g = ti.createGraphics();
			Set<EvaluadorClase> clases = getClasificador().getClasificacion().keySet();
			for(EvaluadorClase c: clases){
				List<Objeto> objetosClase = getClasificador().getClasificacion().get(c);
				for (Objeto obj: objetosClase) {
					if (c.getColor() != null)
						for (Pixel p : obj.getContorno()) {
							pintarPixel(ti, p.getX(), p.getY(), c.getColor());
						}
					
					Rasgo r = new Rasgo();
					r.setNombre("AREA");
					RasgoObjeto areaRasgo = obj.getRasgo(r);
					
					/*
					g.setColor(Color.green);
					if (obj.getName().endsWith("1")){
						for (Triangulo t: obj.getTriangulosContenedores()){
							Polygon pol = new Polygon();
							pol.addPoint(t.getP1().getX(), t.getP1().getY());
							pol.addPoint(t.getP2().getX(), t.getP2().getY());
							pol.addPoint(t.getP3().getX(), t.getP3().getY());
							g.drawPolygon(pol);
						}
					}
					*/
					Pixel medio = obj.getPixelMedio();
					g.drawString(obj.getName(),(int) medio.getX()-15,(int)medio.getY()-15);
					/*
					if (areaRasgo != null){
						
						Font f = new Font("Helvetica",Font.PLAIN,12);
						g.setColor(Color.white);
						g.setFont(f);
						
						g.drawString(areaRasgo.toString(),(int) medio.getX()-15,(int)medio.getY());
						//g.drawString(circularidadRasgo.toString(),(int) medio.getX()-15,(int)medio.getY()+15);
						//g.drawString(aspectRatioRasgo.toString(),(int) medio.getX()-15,(int)medio.getY()+30);
					}*/
				}

			}

			
			return ti;
		}
		return null;

	}
	
	/**
	 * Pinta el interior de los objetos detectados
	 * 
	 * @param objetos
	 * @return
	 */
	public PlanarImage pintarInterior(PlanarImage image) {
		if (getOriginalImage() != null){
			TiledImage ti = ImageUtil.createTiledImage(image, ImageUtil.tileWidth, ImageUtil.tileHeight);
			Set<EvaluadorClase> clases = getClasificador().getClasificacion().keySet();
			for(EvaluadorClase c: clases){
				List<Objeto> objetosClase = getClasificador().getClasificacion().get(c);
				for (Objeto obj: objetosClase) {
					if (obj.getName().equals("68_1"))
					for (Pixel p : obj.getPuntos()) {
						pintarPixel(ti, p.getX(), p.getY(), c.getColor());
					}
					
				}
			}
			
			
			return ti;
		}
		return null;

	}

	
	/**
	 * Recorre la lista de objetos y los clasifica
	 * @param objetos
	 */
	protected void clasificarObjetos(List<Objeto> objetos) {
		EvaluadorClase indeterminado = getClasificador().getEvaluadorClaseIndeterminado();
		indeterminado.setColor(Color.RED);
		List<Objeto> objetosIndeterminados = new ArrayList<Objeto>();
		
		for (Objeto obj : objetos) {
			
			Set<EvaluadorClase> clases = getClasificador().getClasificacion().keySet();
			List<EvaluadorClase> clasesOrdenadas = new ArrayList<EvaluadorClase>(clases);
			Collections.sort(clasesOrdenadas, new EvaluadorClaseComparator());
			boolean sinclasificacion = true;
			for(EvaluadorClase c: clasesOrdenadas){
				if (!c.getClase().isIndeterminado() && !c.getClase().isObjetoReferencia() && c.pertenece(obj,true)){
					ClaseObjeto claseObjeto = new ClaseObjeto(c.getClase());
					obj.addClase(claseObjeto);
					sinclasificacion = false;
					
					claseObjeto.setDistanciaPromedio(c.getClase().distanciaPromedio(obj));
					//break;
				}
			}
			if (sinclasificacion){
				objetosIndeterminados.add(obj);
				ClaseObjeto claseObjeto = new ClaseObjeto(indeterminado.getClase());
				obj.addClase(claseObjeto);
			}
			else{
				if (obj.getClases().size() > 1){
					asignarClaseMasCercana(obj);
				}
				List<Objeto> objetosClase = getClasificador().getObjetosClase(obj.getClases().get(0).getClase());
				objetosClase.add(obj);

			}
		}
		
		getClasificador().getClasificacion().put(indeterminado, objetosIndeterminados);
		
		if (ObjetoReferencia.getReferencia() != null){
			EvaluadorClase objetoReferenciaClase = getClasificador().getEvaluadorClaseObjetoReferencia();
			objetoReferenciaClase.pertenece(ObjetoReferencia.getReferencia(), true);
			List<Objeto> objetosReferencia = new ArrayList<Objeto>();
			objetosReferencia.add(ObjetoReferencia.getReferencia());
			ObjetoReferencia.getReferencia().addClase(objetoReferenciaClase.getClase());
			getClasificador().getClasificacion().put(objetoReferenciaClase, objetosReferencia);
		}
		
		getClasificador().setClasificacionInicial(objetos);
	}
	
	/**
	 * Si un objeto está asignado a más de una clase se busca asignarlo a la clase
	 * cuyos valores medios de rasgos están mas cerca de los rasgos del objeto 
	 * @param obj
	 */
	private void asignarClaseMasCercana(Objeto obj) {
		List<ClaseObjeto> clases = obj.getClases();
		if (clases != null && clases.size() > 0){
			Collections.sort(clases, new ClaseObjetoComparator());
			ClaseObjeto claseMasCercana = clases.get(0); 
			clases.clear();
			clases.add(claseMasCercana);
			obj.setClases(clases);
		}
				
	}

	/*
	 * (non-Javadoc)
	 * @see procesamiento.ImageComand#getCommandName()
	 */
	public String getCommandName() {
		return "Contorno";
	}

	public static PlanarImage getOriginalImage() {
		return originalImage;
	}

	public void setOriginalImage(PlanarImage originalImage) {
		this.originalImage = originalImage;
	}

	public HSVRange getHsvRange() {
		return hsvRange;
	}

	public void setHsvRange(HSVRange hsvRange) {
		this.hsvRange = hsvRange;
	}

	public List<Objeto> getObjetos() {
		return objetos;
	}

	public void setObjetos(List<Objeto> objetos) {
		this.objetos = objetos;
	}

	/*
	 * (non-Javadoc)
	 * @see procesamiento.ImageComand#postExecute()
	 */
	public void postExecute() {
		originalImage = null;

	}

	/**
	 * Retorna información sobre la cantidad de objetos detectados
	 */
	public String getInfo() {
		String info = "Objetos detectados: " + getObjetos().size();
		Set<EvaluadorClase> clases = getClasificador().getClasificacion().keySet();
		for(EvaluadorClase c: clases){
			List<Objeto> objetosClase = getClasificador().getClasificacion().get(c);
			info += "\n" + c.getClase().getNombre() + ": " + objetosClase.size();
		}
		return info;
	}
	
}
