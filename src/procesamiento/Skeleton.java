package procesamiento;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import javax.media.jai.PlanarImage;
import javax.media.jai.TiledImage;

import objeto.Pixel;

public class Skeleton extends AbstractImageCommand {

	/**
	 * Pixels marcados para borrar
	 */
	private List<Pixel> marcados = new ArrayList<Pixel>();

	public Skeleton(PlanarImage image) {
		super(image);
	}

	private List<Pixel> getMarcados() {
		return marcados;
	}

	/**
	 * Recupera el pixel (x,y) de la imagen
	 * 
	 * @param x
	 * @param y
	 * @param width
	 * @param nbands
	 * @param pixels
	 * @return
	 */
	public Pixel getPixel(int x, int y, PlanarImage image) {
		int[] pixel = ImageUtil.readPixel(x, y, (TiledImage) image);
		int r = pixel[0];
		int g = pixel[0];
		int b = pixel[0];

		if (pixel.length == 3){
			g = pixel[1];
			b = pixel[2];
		}
		Color colorPixel = new Color(r, g, b);
		return new Pixel(x, y, colorPixel);
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
	 * Retorna si un pixel es fondo de la imagen
	 * 
	 * @param pixel
	 * @return
	 */
	private boolean isFondo(Pixel pixel) {
		int umbralFondo = 100;
		return pixel.getCol().getRed() < umbralFondo;
	}
	
	/**
	 * Devuelve si un pixel es contorno: si tiene un vecino que es fondo
	 * 
	 * @param pixel
	 * @param width
	 * @param nbands
	 * @param pixels
	 * @param umbralFondo
	 * @return
	 */
	public boolean isContorno(Pixel pixel, PlanarImage image) {
		int x = pixel.getX();
		int y = pixel.getY();

		if (isFondo(pixel))
			return false;
		Pixel actual = null;
		int width = getImage().getWidth();
		int height = getImage().getHeight();
		
		if (x - 1 < 0 || y -1 < 0 || x + 1 >= width || y + 1 >= height )
			return true;
		
		if (x - 1 >= 0) {

			actual = getPixel(x - 1, y, image);
			if (isFondo(actual))
				return true;
		}
		if (y - 1 >= 0) {
			actual = getPixel(x, y - 1, image);
			if (isFondo(actual))
				return true;
		}

		if (y + 1 < height) {
			actual = getPixel(x, y + 1, image);
			if (isFondo(actual))
				return true;
		}

		if (x + 1 < width) {
			actual = getPixel(x + 1, y, image);
			if (isFondo(actual))
				return true;
		}

		return false;
	}

	/**
	 * Retorna el pixel adyacente a uno dado en una dirección determinada
	 * 
	 * @param pixel
	 *            Pixel actual
	 * @param direccion
	 *            Dirección para recuperar el adyacente
	 * @return Pixel adyacente
	 */
	public Pixel getAdyacente(Pixel pixel, int direccion, PlanarImage image) {
		Pixel ady = pixel.getAdyacente(direccion, image.getWidth(), image.getHeight());
		if (ady != null) {
			return getPixel(ady.getX(), ady.getY(), image);
		}
		return null;
	}

	/**
	 * Retorna la cantidad de vecinos del pixel que no son fondo
	 * @param pixel
	 * @param image
	 * @return
	 */
	private int vecinosNoFondo(Pixel pixel, PlanarImage image){
		int cont = 0;
		for (int dir = 0; dir< 8; dir++){
			Pixel p = getAdyacente(pixel,dir , image);
			if (p != null && !isFondo(p))
				cont++;
		}
		return cont;
	}
	
	/**
	 * Retorna el número de transiciones 0-1 en una secuencia ordenada P2 , P3 , P4 , P5 , P6 , P7 , P8, P9
	 * @param pixel
	 * @param image
	 * @return
	 */
	private int numeroDeTransiciones0a1(Pixel pixel, PlanarImage image){
		int cont = 0;
		Pixel p = getAdyacente(pixel,0 , image);
		int anterior = 1;
		int actual = 1;
		if (p != null && isFondo(p))
			anterior = 0;
		for (int dir = 1; dir< 8; dir++){
			p = getAdyacente(pixel,dir , image);
			if (p != null){
				if (isFondo(p))
					actual = 0;
				else 
					actual = 1;
				if (anterior == 0 && actual == 1)
					cont++;
				anterior = actual;
			}
		}
		//Cierro el ciclo. Comparo p9 con p2
		p = getAdyacente(pixel,0 , image);
		if (p != null){
			if (isFondo(p))
				actual = 0;
			else 
				actual = 1;
			if (anterior == 0 && actual == 1)
				cont++;
		}
		return cont;
	}
	
	/**
	 * Borra los pixeles marcados
	 * @param image
	 * @param marcados
	 */
	private void borrarMarcados(PlanarImage image, List<Pixel> marcados){
		if (image != null){
			for(Pixel pixel: marcados){
				pintarPixel((TiledImage) image, pixel.getX(), pixel.getY(), Color.BLACK);
			}
		}
	}
	
	/**
	 * 
	 * @param image
	 * @return
	 */
	private PlanarImage paso1(PlanarImage image){
		if (image != null){
			TiledImage ti = (TiledImage) image;
			int width = getImage().getWidth();
			int height = getImage().getHeight();
			
			getMarcados().clear();
			for (int h = 0; h < height; h++)
				for (int w = 0; w < width; w++) {
					Pixel pixel = getPixel(w, h, ti);
					if (!isFondo(pixel)){
					//if (isContorno(pixel, ti)){
						int N = vecinosNoFondo(pixel, ti);
						int S = numeroDeTransiciones0a1(pixel, ti);
						Pixel p2 = getAdyacente(pixel, Pixel.DIR_N, ti);
						Pixel p4 = getAdyacente(pixel, Pixel.DIR_E, ti);
						Pixel p6 = getAdyacente(pixel, Pixel.DIR_S, ti);
						//Pixel p7 = getAdyacente(pixel, Pixel.DIR_SO, ti);
						Pixel p8 = getAdyacente(pixel, Pixel.DIR_O, ti);
						
						if (pixel.getX() == 63 && pixel.getY() == 77 && N == 2)
							System.out.println("entro");
						
						if (N <= 1)
							getMarcados().add(pixel);
						else if (2 <= N && N <=6 && S == 1){
								if (((p2 != null && isFondo(p2)) || 
									 (p4 != null && isFondo(p4)) || 
									 (p6 != null && isFondo(p6)))&& 
									((p4 != null && isFondo(p4)) || 
									 (p6 != null && isFondo(p6)) || 
									 (p8 != null && isFondo(p8)))
									 //&&(p7 != null && !isFondo(p7))
									 )
									//if (!rompeContinuidad(pixel, ti))
										getMarcados().add(pixel);
							}
					}
				}
			
			borrarMarcados(ti, getMarcados());
			return ti;		
		}
		return null;
	}
	
	/**
	 * 
	 * @param image
	 * @return
	 */
	private PlanarImage paso2(PlanarImage image){
		if (image != null){
			TiledImage ti = (TiledImage) image;
			int width = getImage().getWidth();
			int height = getImage().getHeight();
			
			getMarcados().clear();
			for (int h = 0; h < height; h++)
				for (int w = 0; w < width; w++) {
					Pixel pixel = getPixel(w, h, ti);
					if (!isFondo(pixel)){
					//if (isContorno(pixel, ti)){
						int N = vecinosNoFondo(pixel, ti);
						int S = numeroDeTransiciones0a1(pixel, ti);
						Pixel p2 = getAdyacente(pixel, Pixel.DIR_N, ti);
						//Pixel p3 = getAdyacente(pixel, Pixel.DIR_NE, ti);
						Pixel p4 = getAdyacente(pixel, Pixel.DIR_E, ti);
						Pixel p6 = getAdyacente(pixel, Pixel.DIR_S, ti);
						Pixel p8 = getAdyacente(pixel, Pixel.DIR_O, ti);
						
						if (pixel.getX() == 63 && pixel.getY() == 77 && N == 2)
							System.out.println("entro");
						
						if (N <=1)
							getMarcados().add(pixel);
						else if (2 <= N && N <=6 && S == 1){
								if (((p2 != null && isFondo(p2)) || 
									 (p4 != null && isFondo(p4)) || 
									 (p8 != null && isFondo(p8)))&& 
									((p2 != null && isFondo(p2)) || 
									 (p6 != null && isFondo(p6)) || 
									 (p8 != null && isFondo(p8)))
									 //&&	(p3 != null && !isFondo(p3))
									 )
										//if (!rompeContinuidad(pixel, ti))
											getMarcados().add(pixel);
							}
					}
				}
			borrarMarcados(ti, getMarcados());
			return ti;		
		}
		return null;
	}
	
	
	public PlanarImage execute() {
		if (getImage() != null){
			TiledImage ti = ImageUtil.createTiledImage(getImage(), ImageUtil.tileWidth, ImageUtil.tileHeight);
			PlanarImage image = ti;
			int pasos = 0;
			do {
				image = paso1(image);
				image = paso2(image);
				pasos++;
				System.out.println(pasos);
			} 
			//while (false);
			while (getMarcados().size() > 0); 
			return image;		
		}
		return null;
	}

	public String getCommandName() {
		return this.getClass().getCanonicalName();
	}

	public void postExecute() {
		// TODO Auto-generated method stub

	}

}
