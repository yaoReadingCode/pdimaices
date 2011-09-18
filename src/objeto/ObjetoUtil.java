package objeto;

import java.awt.Color;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.renderable.ParameterBlock;
import java.util.ArrayList;
import java.util.List;

import javax.media.jai.InterpolationBilinear;
import javax.media.jai.JAI;
import javax.media.jai.PlanarImage;
import javax.media.jai.RasterFactory;
import javax.media.jai.TiledImage;

import procesamiento.ImageUtil;
import procesamiento.clasificacion.CoeficientesRecta;

public class ObjetoUtil {
	private static final int ORIENTACION_ABAJO = 90;
	//private static PlanarImage inputImage = JAI.create("fileload", "limpia.tif");
	//private static int desplazamiento = 50;

	/*
	public PlanarImage getInputImage() {
		return inputImage;
	}

	public static void setInputImage(PlanarImage inputImageSet) {
		inputImage = inputImageSet;
	}*/

	public static void save(Objeto o) {
		if (o != null) {
			int width = 206;
			int height = 206;
			byte[] data = new byte[width * height* 3]; // Image data array.
			DataBufferByte dbuffer = new DataBufferByte(data, width * height * 3);
			SampleModel sampleModel = RasterFactory.createPixelInterleavedSampleModel(DataBuffer.TYPE_BYTE, width,
							height, 3);
			ColorModel colorModel = PlanarImage.createColorModel(sampleModel);
			Raster raster = RasterFactory.createWritableRaster(sampleModel, dbuffer, new Point(0, 0));
			TiledImage ti = new TiledImage(0, 0, width, height, 0, 0, sampleModel, colorModel);
			ti.setData(raster);

			Pixel pixelPunta = o.getPixelPunta();
			Pixel medio = o.getPixelMedio();

			o.calcularMaximosMinimos();
			for (Pixel p : o.getPuntos()) {
				pintarPunto(p, o, ti, medio, width, height);
			}
			for (Pixel p : o.getContorno()) {
				pintarPunto(p, o, ti, medio, width, height);
			}
			
			/*
			o.getPixelPunta1().setCol(Color.RED);
			o.getPixelPunta2().setCol(Color.RED);
			pintarPunto(o.getPixelPunta1(), o, ti, medio, width, height);
			pintarPunto(o.getPixelPunta2(), o, ti, medio, width, height);
			
			int pixel[] = { Color.RED.getRed(), Color.RED.getGreen(), Color.RED.getBlue() };
			ImageUtil.writePixel(width / 2, height / 2, pixel, ti);
			*/
			
			o.setPathImage("image\\" + o.getName() + ".tif");

			PlanarImage image = ti;
			if (pixelPunta != null){
				Pixel p = pixelPunta.getCoordenadasCartesianas();
				Pixel m = medio.getCoordenadasCartesianas();
				p.restar(m);
				Double angulo = p.getAnguloPolar();
				if (angulo != null){
					float angle= (float) Math.toRadians(angulo + ORIENTACION_ABAJO);
					angle = (float)((int)(angle * 100000))/100000.0f;
					float centerX= width / 2f;
					float centerY= height / 2f;
 					ParameterBlock pb= new ParameterBlock();
					pb.addSource(ti);
					pb.add(centerX);
					pb.add(centerY);
					pb.add(angle);
					pb.add(new InterpolationBilinear());
					RenderingHints hints = JAI.getDefaultInstance().getRenderingHints();
					image =JAI.create("rotate",pb,hints);
					/*
					Rectangle rec = new Rectangle();
					rec.x = (int)centerX - width / 2;
					//if (rec.x < 0) rec.x = 0;
					rec.y = (int)centerY - height / 2;
					//if (rec.y < 0) rec.y = 0;
					rec.width = width;
					rec.height = height;*/
					
					ParameterBlock pbCrop= new ParameterBlock();
					pbCrop.addSource(image);
					pbCrop.add(new Float(0));
					pbCrop.add(new Float(0));
					pbCrop.add(new Float(width));
					pbCrop.add(new Float(height));
					image =JAI.create("crop",pbCrop,hints);
					
					/*
					Raster dataRaster = image.getData(rec);
					ti.setData(dataRaster);*/
					JAI.create("filestore", image, o.getPathImage(), "TIFF");
					return;
				}
			}
			JAI.create("filestore", ti, o.getPathImage(), "TIFF");
		}

	}
	private static void pintarPunto(Pixel p, Objeto o, TiledImage ti, Pixel medio, int width, int height){
		Color interior = p.getCol();
		if (interior != null) {
			int pixel[] = { interior.getRed(), interior.getGreen(),
					interior.getBlue() };

			int x = (p.getX() - medio.getX()) + (width / 2);
			int y = (p.getY() - medio.getY()) + (height / 2);

			ImageUtil.writePixel(x, y, pixel, ti);
		}else{
			int pixel[] = {50, 50,	50};

			int x = (p.getX() - medio.getX()) + (width / 2);
			int y = (p.getY() - medio.getY()) + (height / 2);

			ImageUtil.writePixel(x, y, pixel, ti);
			
		}
		
	}

	/***
	 * Calcula los coeficientes a, b y c de la recta a * x + b * y + c = 0 formada por el vector director
	 * de la recta y un punto de la misma
	 * @param vectorDirector Vector director de la recta
	 * @param puntoRecta Punto que pertenece a la recta
	 * @param a
	 * @param b
	 * @param c
	 */
	public static void coeficientesRecta(Double pendiente, Pixel punto, CoeficientesRecta coeficientes){
		if (pendiente != null){
			coeficientes.a = -1 * pendiente;
			coeficientes.b = 1;
			coeficientes.c = pendiente * punto.getXDouble() - punto.getYDouble();
		}
		else{
			coeficientes.a = 1;
			coeficientes.b = 0;
			coeficientes.c = -1 * punto.getX();
		}
	}
	/**
	 * Calcula el punto de intersección de dos rectas: <a * x + b * y + c = 0> y <d * x + e * y + f = 0> 
	 * @param a
	 * @param b
	 * @param c
	 * @param d
	 * @param e
	 * @param f
	 * @return
	 */
	public static Pixel calcularInterseccionRectas(double a, double b, double c, double d, double e, double f){
		Double x = null, y = null;
		Double c1 = 0.0, c2 = 0.0;
		if (a - d != 0){
			c1 = a * (e - b)/(a - d);
			c2 = a * (f - c)/(a - d);
		}
		if (c1 + b != 0)
			y = (-c2 - c)/(c1 + b);
		if (d != 0 && y != null)
			x = (-f - e * y)/ d;
		if (x != null && y != null)
			return new Pixel(x,y,null);
		return null;
	}
	
	/**
	 * Calcula la distancia de un punto a una recta
	 * @param p
	 * @param recta
	 * @return
	 */
	public static double distancia(Pixel p, CoeficientesRecta recta){
		double distancia = Math.abs((-1 * recta.a * p.getXDouble() - p.getYDouble() - recta.c) / Math.sqrt(Math.pow(recta.a,2) + 1));
		return distancia;
	}

	/**
	 * Calcula el punto de interseccion entre dos rectas si existe
	 * @param pInicio1
	 * @param pFin1
	 * @param pInicio2
	 * @param pFin2
	 * @return
	 */
	public static Pixel calcularPuntoInterseccion(Pixel pInicio1, Pixel pFin1, Pixel pInicio2, Pixel pFin2){
		Double pendiente1 = null;
		Double pendiente2 = null;
		if (pFin1.getXDouble() - pInicio1.getXDouble() != 0)
			pendiente1 = (pFin1.getYDouble() - pInicio1.getYDouble()) / (pFin1.getXDouble() - pInicio1.getXDouble());
		if (pFin2.getXDouble() - pInicio2.getXDouble() != 0)
			pendiente2 = (pFin2.getYDouble() - pInicio2.getYDouble()) / (pFin2.getXDouble() - pInicio2.getXDouble());

		if (pendiente1 == pendiente2){
			return null;
		}

		CoeficientesRecta coefR1 = new CoeficientesRecta();
		CoeficientesRecta coefR2 = new CoeficientesRecta();
		coeficientesRecta(pendiente1, pInicio1, coefR1);
		coeficientesRecta(pendiente2, pInicio2, coefR2);
		Pixel pInterseccion = calcularInterseccionRectas(coefR1.a, coefR1.b, coefR1.c, coefR2.a, coefR2.b, coefR2.c);
		return pInterseccion;
	}

	
	public static Double calcularAngulo(Pixel pInicio, Pixel pMedio, Pixel pFin){
		Double angulo = null;
		Double pendiente1 = null;
		if (pMedio.getXDouble() - pInicio.getXDouble() != 0)
			pendiente1 = (pMedio.getYDouble() - pInicio.getYDouble()) / (pMedio.getXDouble() - pInicio.getXDouble());
		else{
			pendiente1 = null;
		}
		Pixel pInterseccion = null;
		if (pendiente1 == null){
			pInterseccion = new Pixel(pInicio.getXDouble(),pFin.getYDouble(),null);
		}
		else if (pendiente1 != 0){
			Double pendiente2 =  -1 / pendiente1;

			CoeficientesRecta coefR1 = new CoeficientesRecta();
			CoeficientesRecta coefR2 = new CoeficientesRecta();
			coeficientesRecta(pendiente1, pInicio, coefR1);
			coeficientesRecta(pendiente2, pFin, coefR2);
			pInterseccion = calcularInterseccionRectas(coefR1.a, coefR1.b, coefR1.c, coefR2.a, coefR2.b, coefR2.c);
		}
		else{
			pInterseccion = new Pixel(pFin.getXDouble(),pInicio.getYDouble(),null);
		}
		
		if (pInterseccion != null){
			pInterseccion.setMaxX(pInicio.getMaxX());
			pInterseccion.setMaxY(pInicio.getMaxY());
			if (!pInterseccion.equals(pMedio)){
				double ladoA = pFin.distancia(pMedio);
				double ladoB = pFin.distancia(pInterseccion);
				if (ladoB < 1)
					return 0.0;
				if (ladoA != 0)
					angulo = Math.toDegrees(Math.asin(ladoB / ladoA));

				double ladoPuntoInterseccion = Pixel.lado2(pMedio, pInterseccion, pFin);
				
				if (ladoPuntoInterseccion > 0.0 && angulo != null)
					angulo = 180 - angulo;

			}else
				angulo = 90.0;
		
		}
		// lado del punto pFin Con respecto a la recta pinicio - pmedio
		double ladoPfin =  Pixel.lado2(pInicio, pFin, pMedio);
		if (ladoPfin < 0){
			angulo = 180 - angulo;
		}
		return angulo;
	}
	/**
	 * Crea una linea de pixeles que unen dos puntos
	 * @param p1
	 * @param p2
	 * @return
	 */
	public static List<Pixel> crearLinea(Pixel p1, Pixel p2,int width, int height){
		List<Pixel> linea = new ArrayList<Pixel>();
		Pixel inicio = p1;
		Pixel fin = p2;
		if (p2.getX() < p1.getX() && p1.getX() != p2.getX()){
			inicio = p2;
			fin = p1;
		}
		else{
			if (p2.getX() == p1.getX() && p2.getY() < p1.getY()){
				inicio = p2;
				fin = p1;
			}
		}
		//linea.add(inicio);
		Pixel anterior = inicio;
		if (fin.getX() != inicio.getX()){
			double a = (fin.getYDouble() - inicio.getYDouble()) / (fin.getXDouble() - inicio.getXDouble());
			double b = fin.getYDouble() - a * fin.getXDouble();
			for(int x = inicio.getX(); x <= fin.getX(); x++){
				int y = (int) Math.round(a * x + b);
				Pixel p = new Pixel(x, y, Color.BLACK,width,height);
				if (!anterior.isAdyacente(p)){
					
					for(int y2 = anterior.getY() + 1; anterior.getY()< p.getY() && y2 < p.getY(); y2++){
						Pixel pAux = new Pixel(p.getX(), y2, Color.BLACK,width,height);
						linea.add(pAux);
						anterior = pAux;
					}

					for(int y2 = anterior.getY() - 1; p.getY() < anterior.getY() && y2 > p.getY(); y2--){
						Pixel pAux = new Pixel(p.getX(), y2, Color.BLACK,width,height);
						linea.add(pAux);
						anterior = pAux;
					}
					
				}
				if (!linea.contains(p)){
					linea.add(p);
					anterior = p;
				}
					
			}
		}
		else{
			for(int y = inicio.getY(); y <= fin.getY(); y++){
				Pixel p = new Pixel(inicio.getX(), y, Color.BLACK,width,height);
				if (!linea.contains(p))
					linea.add(p);	
			}
			
		}
		List<Pixel> lineaPixelesOrd = new ArrayList<Pixel>();
		linea.remove(p1);
		Pixel proximo = p1;
		while(linea.size() > 0){
			 proximo = proximo.getPixelMasCercano(linea);
			 if (!proximo.equals(p2))
				 lineaPixelesOrd.add(proximo);
			 linea.remove(proximo);
		}

		return lineaPixelesOrd;
	}

	
	public static void main(String[] args) {
		
		Pixel pInicio = new Pixel(106,77,null,1000,1000);
		Pixel pMedio = new Pixel(99,87,null,1000,1000);
		Pixel pFin = new Pixel(91,97,null,1000,1000);
		Double angulo1 = ObjetoUtil.calcularAngulo(pInicio, pMedio, pFin);
		System.out.println(angulo1);
		
		
		/*
		Pixel p = new Pixel(50,37,null);
		p.rotar(318.50353164478446);
		System.out.println(p);
		
		p = new Pixel(51,37,null);
		p.rotar(318.50353164478446);
		System.out.println(p);
		*/
		 int a = 2; 
		 int b = 4; 
		  
		 System.out.println((a * b));
		 System.out.println((++a * b));
		 System.out.println((a * ++b));
	}
}
