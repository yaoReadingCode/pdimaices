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
	public static final int DEFAULT_IMAGE_WIDTH = 206;
	public static final int DEFAULT_IMAGE_HEIGHT = 206;

	public static void save(Objeto o, Color fondo) {
		if (o != null) {
			int width = Math.max((int) o.getBoundingBox().width(),DEFAULT_IMAGE_WIDTH);
			int height = Math.max((int) o.getBoundingBox().height(),DEFAULT_IMAGE_HEIGHT);
			byte[] data = new byte[width * height* 3]; // Image data array.
			DataBufferByte dbuffer = new DataBufferByte(data, width * height * 3);
			SampleModel sampleModel = RasterFactory.createPixelInterleavedSampleModel(DataBuffer.TYPE_BYTE, width,
							height, 3);
			ColorModel colorModel = PlanarImage.createColorModel(sampleModel);
			Raster raster = RasterFactory.createWritableRaster(sampleModel, dbuffer, new Point(0, 0));
			TiledImage ti = new TiledImage(0, 0, width, height, 0, 0, sampleModel, colorModel);
			ti.setData(raster);
			
			ImageUtil.inicializarImagen(ti, fondo);

			Pixel pixelPunta = o.getPixelPunta();
			Pixel medio = o.getPixelMedio();

			o.calcularMaximosMinimos();
			for (Pixel p : o.getPuntos()) {
				pintarPunto(p, o, ti, medio, width, height);
			}
			for (Pixel p : o.getContorno()) {
				//p.setCol(Color.red);
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
			
			o.setPathImage("image\\" + o.getName() + "_" + System.currentTimeMillis() + ".tif");

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
					double[] backgroundColor = {fondo.getRed(), fondo.getGreen(), fondo.getBlue()};
 					ParameterBlock pb= new ParameterBlock();
					pb.addSource(ti);
					pb.add(centerX);
					pb.add(centerY);
					pb.add(angle);
					pb.add(new InterpolationBilinear());
					pb.add(backgroundColor);
					RenderingHints hints = JAI.getDefaultInstance().getRenderingHints();
					image =JAI.create("rotate",pb,hints);
					
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
	 * Calcula la distancia de un punto a una recta formada por los coeficientes a y b. y = a * x + b
	 * @param punto
	 * @param a Coeficiente a de la ecuacion de la recta
	 * @param b Coeficiente b de la ecuacion de la recta
	 * @return
	 */
	public static double distanciaPuntoARecta(Pixel punto, double a, double b){
		double distancia = Math.abs(a * punto.getX() - punto.getY() + b) / Math.sqrt(Math.pow(a, 2) + 1);
		return distancia;
	}
	
	/**
	 * Calcula la distancia de un punto a una recta formada por el segemento iniRecta - finRecta
	 * @param punto
	 * @param iniRecta
	 * @param finRecta
	 * @return
	 */
	public static double distanciaPuntoARecta(Pixel punto, Pixel iniRecta, Pixel finRecta){
		Double pendiente = null;
		if (finRecta.getXDouble() - iniRecta.getXDouble() != 0)
			pendiente = (finRecta.getYDouble() - iniRecta.getYDouble()) / (finRecta.getXDouble() - iniRecta.getXDouble());
		if (pendiente == null){
			return Math.abs(iniRecta.getY()- punto.getY());
		}
		else if (pendiente != 0){

			CoeficientesRecta coefRecta = new CoeficientesRecta();
			coeficientesRecta(pendiente, iniRecta, coefRecta);
			//Obtenemos los coeficientes de la recta de la forma y = a * x + b
			double a = - coefRecta.a / coefRecta.b;
			double b = - coefRecta.c / coefRecta.b;
			return distanciaPuntoARecta(punto, a, b);
		}
		else{
			return Math.abs(iniRecta.getX()- punto.getX());
		}
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
	
	/**
	 * Calcula la pendiente de la recta
	 * @param inicio
	 * @param fin
	 * @return
	 */
	public static Double calcularPendienteRecta(Pixel inicio, Pixel fin){
		Double pendiente = null;
		if (fin.getXDouble() - inicio.getXDouble() != 0)
			pendiente = (fin.getYDouble() - inicio.getYDouble()) / (fin.getXDouble() - inicio.getXDouble());
		return pendiente;
	}
	
	/**
	 * Calcula la media del vector de valores pasado como parámetro
	 * @param vector
	 * @return Valor medio
	 */
	public static double media (double[] vector){
		double sum = 0;
		for(double x: vector){
			sum+=x;
		}
		if (vector.length > 0){
			return sum / vector.length;
		}
		return 0;
	}
	
	/**
	 * Calcula la media del vector de valores pasado como parámetro
	 * @param vector
	 * @return Valor medio
	 */
	public static double varianza (double[] vector){
		double sumX = 0;
		double sumX2 = 0;
		for(double x: vector){
			sumX+=x;
			sumX2+=x*x;
		}
		if (vector.length > 0){
			double media = sumX / vector.length;
			double mediaCuadrados = sumX2 / vector.length;
			return mediaCuadrados - Math.pow(media, 2);
		}
		return 0;
	}
	
	/**
	 * Calcula el coeficiente de correlacion entre dos vectores
	 * @param vector
	 * @return Valor medio
	 */
	public static double coeficienteCorrelacion (double[] vectorX, double[] vectorY){
		double sumX = 0;
		double sumX2 = 0;
		double sumY = 0;
		double sumY2 = 0;
		double sumProductoXY = 0;
		double n = vectorX.length;
		for(int i = 0; i < n; i++){
			double x = vectorX[i];
			double y = vectorY[i];
			sumX+= x;
			sumX2+=Math.pow(x, 2);
			sumY+= y;
			sumY2+=Math.pow(y, 2);
			sumProductoXY += x * y;
		}
		if (n > 0){
			double mediaX = sumX / n;
			double mediaY = sumY / n;
			double covarianza = sumProductoXY / n - mediaX * mediaY;
			double desvioX = Math.sqrt(sumX2 / n - Math.pow(mediaX, 2));
			double desvioY = Math.sqrt(sumY2 / n - Math.pow(mediaY, 2));
			return covarianza / (desvioX * desvioY);
		}
		return 0;
	}
	
	/**
	 * Calcula la distancia de Bhattacharya entre dos Histogramas
	 * @return
	 */
	public static double distanciaBhattacharya(double[] vectorX, double[] vectorY){
		double total = 0;
		for(int i = 0; i < vectorX.length; i++){
			double valorAuxiliar = Math.sqrt(vectorX[i]) * Math.sqrt(vectorY[i]);
			total+= valorAuxiliar;
		}
		return total;
	}
	public static void main(String[] args) {
		double[] X = {1, 2, 3, 4, 5, 6, 7};
		double[] Y = {7, 6, 5, 4, 3, 2, 1};
		double correlacion = coeficienteCorrelacion(X, Y);
		System.out.println(correlacion);
			
	}
}
