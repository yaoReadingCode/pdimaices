package objeto;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.renderable.ParameterBlock;

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
			int height = 174;
			byte[] data = new byte[width * height* 3]; // Image data array.
			DataBufferByte dbuffer = new DataBufferByte(data, width * height * 3);
			SampleModel sampleModel = RasterFactory.createPixelInterleavedSampleModel(DataBuffer.TYPE_BYTE, width,
							height, 3);
			ColorModel colorModel = PlanarImage.createColorModel(sampleModel);
			Raster raster = RasterFactory.createWritableRaster(sampleModel, dbuffer, new Point(0, 0));
			TiledImage ti = new TiledImage(0, 0, width, height, 0, 0, sampleModel, colorModel);
			ti.setData(raster);

			if (o.getName().equals("Maiz221"))
				System.out.println("");
			Pixel pixelPunta = o.getPixelPunta();
			Pixel medio = o.getPixelMedio();

			o.calcularMaximosMinimos();
			for (Pixel p : o.getPuntos()) {
				pintarPunto(p, o, ti, medio, width, height);
			}
			for (Pixel p : o.getContorno()) {
				pintarPunto(p, o, ti, medio, width, height);
			}
			
			o.getPixelPunta1().setCol(Color.RED);
			o.getPixelPunta2().setCol(Color.BLUE);
			pintarPunto(o.getPixelPunta1(), o, ti, medio, width, height);
			pintarPunto(o.getPixelPunta2(), o, ti, medio, width, height);
			
			int pixel[] = { Color.RED.getRed(), Color.RED.getGreen(), Color.RED.getBlue() };
			ImageUtil.writePixel(width / 2, height / 2, pixel, ti);

			o.setPathImage("image\\" + o.getName() + ".tif");
			PlanarImage image = ti;
			
			if (pixelPunta != null){
				Pixel p = pixelPunta.getCoordenadasCartesianas();
				Pixel m = medio.getCoordenadasCartesianas();
				p.restar(m);
				Double angulo = p.getAnguloPolar();
				if (angulo != null){
					float angle= (float) Math.toRadians(angulo + ORIENTACION_ABAJO);
					float centerX= width / 2;
					float centerY= height / 2;
					angle = (float)((angle * 10000000))/10000000.0f; 
					ParameterBlock pb= new ParameterBlock();
					pb.addSource(ti);
					pb.add(centerX);
					pb.add(centerY);
					pb.add(angle);
					pb.add(new InterpolationBilinear());
					RenderingHints hints = JAI.getDefaultInstance().getRenderingHints();
					image =JAI.create("rotate",pb,hints);
				}
			}
			Rectangle rec = new Rectangle();
			
			rec.x = image.getWidth() / 2 - Math.max(width,height) / 2;
			if (rec.x < 0)
				rec.x = 0;
			rec.y = image.getHeight() / 2 - Math.max(width,height) / 2;
			if (rec.y < 0)
				rec.y = 0;
			rec.width = width;
			rec.height = height;
			Raster dataRaster = image.getData(rec);
			ti.setData(dataRaster);
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
	public static void coeficientesRecta(double pendiente, Pixel punto, CoeficientesRecta coeficientes){
		coeficientes.a = -1 * pendiente;
		coeficientes.b = 1;
		coeficientes.c = pendiente * punto.getXDouble() - punto.getYDouble();
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

	public static Double calcularAngulo(Pixel pInicio, Pixel pMedio, Pixel pFin){
		Double angulo = null;
		Double pendiente1 = null;
		if (pMedio.getXDouble() - pInicio.getXDouble() != 0)
			pendiente1 = (pMedio.getYDouble() - pInicio.getYDouble()) / (pMedio.getXDouble() - pInicio.getXDouble());
		else{
			if (pFin.getXDouble() - pMedio.getXDouble() != 0){
				pendiente1 = (pFin.getYDouble() - pMedio.getYDouble()) / (pFin.getXDouble() - pMedio.getXDouble());
			}
			else
				return 0.0;
		}
		Double pendiente2 = 0.0;
		if (pendiente1 != 0)
			pendiente2 = -1 / pendiente1;

		if (pendiente1 == pendiente2)
			return 0.0;

		CoeficientesRecta coefR1 = new CoeficientesRecta();
		CoeficientesRecta coefR2 = new CoeficientesRecta();
		coeficientesRecta(pendiente1, pInicio, coefR1);
		coeficientesRecta(pendiente2, pFin, coefR2);
		Pixel pInterseccion = calcularInterseccionRectas(coefR1.a, coefR1.b, coefR1.c, coefR2.a, coefR2.b, coefR2.c);
		if (pInterseccion != null){
			pInterseccion.setMaxX(pInicio.getMaxX());
			pInterseccion.setMaxY(pInicio.getMaxY());
			if (!pInterseccion.equals(pMedio)){
				double ladoA = pFin.distancia(pMedio);
				double ladoB = pFin.distancia(pInterseccion);
				if (ladoA != 0)
					angulo = Math.toDegrees(Math.asin(ladoB / ladoA));

				double ladoPuntoMedio = Pixel.lado2(pInicio, pMedio, pFin);
				double ladoPuntoInterseccion = Pixel.lado2(pMedio, pInterseccion, pFin);
				if (ladoPuntoInterseccion > 0 && angulo != null && ladoPuntoMedio < 0)
					angulo = 180 - angulo;

			}else
				angulo = 90.0;
		
		}
		return angulo;
	}
	
	public static void main(String[] args) {
		Pixel punto = new Pixel(293,678,null,1000,1000);
		Pixel pInicio = new Pixel(293,688,null,1000,1000);
		Pixel pFin = new Pixel(303,674,null,1000,1000);
		Double angulo1 = ObjetoUtil.calcularAngulo(pInicio, punto, pFin);
		System.out.println(angulo1);

		punto = new Pixel(346,699,null,1000,1000);
		pInicio = new Pixel(347,689,null,1000,1000);
		pFin = new Pixel(340,709,null,1000,1000);
		Double angulo2 = ObjetoUtil.calcularAngulo(pInicio, punto, pFin);
		System.out.println(angulo2);

	}
}
