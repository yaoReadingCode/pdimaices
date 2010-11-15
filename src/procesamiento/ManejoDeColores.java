package procesamiento;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;

import objeto.Objeto;
import objeto.Pixel;

public class ManejoDeColores {
	private Color colorDeFondo = null;
	private ArrayList<Color> colores = new ArrayList<Color>();
	private int cantidad = 0;
	private int epsilon = 40;
	private int width = 0;
	private int height = 0;
	private byte[] data = null;
	private int[][] matriz = null;
	private int[] pixels = null;
	private int maxOffset = 0;
	private int maxCount = 0;
	private int[] arreglo = null;
	private int maxArreglo = 0;
	private int max = 0;

	public int getMaxOffset() {
		return maxOffset;
	}

	public void setMaxOffset(int maxOffset) {
		this.maxOffset = maxOffset;
	}

	public int[] getPixels() {
		return pixels;
	}

	public void setPixels(int[] pixels) {
		this.pixels = pixels;
	}

	public boolean isAzulCompleto(int i, int j, int offset, int nbands) {
		offset = j * width * nbands + i * nbands;
		if (offset + 3 >= maxOffset && offset <= 0)
			return true;
		int R = 0;
		int G = 0;
		int B = 0;
		int band = 0;
		R = pixels[offset + band];
		band++;
		if (band < nbands)
			G = pixels[offset + band];
		band++;
		if (band < nbands)
			B = pixels[offset + band];
		if (!isAzulInt(R, G, B)) {
			return false;
		}
		return true;
	}

	public ManejoDeColores(int w, int h) {
		width = w;
		height = h;
		matriz = new int[width][height];
		data = new byte[width * height * 3];
		maxCount = (width * height * 3);
		maxArreglo = width * height * 2;
		arreglo = new int[maxArreglo];

	}

	public Color getColorDeFondo() {
		return colorDeFondo;
	}

	public void setColorDeFondo(Color colorDeFondo) {
		this.colorDeFondo = colorDeFondo;
	}

	public void agregarColor(Color col) {
		cantidad++;

		if (diferencia(col.getRed(), colorDeFondo.getRed()) <= epsilon)
			if (diferencia(col.getGreen(), colorDeFondo.getGreen()) <= epsilon)
				// if (diferencia(col.getBlue(),colorDeFondo.getBlue()) <=
				// epsilon)
				colores.add(col);

	}

	public void agregarColorDistAzul(Color col) {
		cantidad++;
		if (!isAzul(col))
			colores.add(col);

	}

	public boolean isAzul(Color col) {
		int R = col.getRed();
		int G = col.getGreen();
		int B = col.getBlue();
		if ((B <= G) && (B <= R))
			return false;
		return true;
	}

	public boolean isAzulInt(int R, int G, int B) {
		if ((B <= G) && (B <= R))
			return false;
		if (B > 220 && R > 220 && G > 220)
			return false;
		return true;
	}

	public int diferencia(int col, int fondo) {
		int dif = 0;
		dif = col - fondo;
		if (dif >= 0)
			return dif;
		dif = dif * (-1);
		return dif;
	}

	public boolean isVisitado(int i, int j) {
		if (matriz[i][j] == 0)
			return false;
		return true;
	}

	public void setMatriz(int i, int j, int valor) {
		matriz[i][j] = valor;
	}

	public Pixel crearPixel(int i, int j, int offset, int nbands) {
		offset = j * width * nbands + i * nbands;
		if (offset + 3 >= maxOffset && offset <= 0)
			return null;
		int R = 0;
		int G = 0;
		int B = 0;
		int band = 0;
		R = pixels[offset + band];
		band++;
		if (band < nbands)
			G = pixels[offset + band];
		band++;
		if (band < nbands)
			B = pixels[offset + band];
		Pixel p = new Pixel(i, j, R, G, B);
		return p;
	}

	/*
	 * public void marcarObjeto(int i, int j, int offset, int nbands){
	 * 
	 * int count=jwidthnbands+inbands; if (count+3 < maxCount && count > 0) if
	 * (i-1 > 0 && i+1 < width && j-1 > 0 && j+1 < height){ if(matriz[i][j] ==
	 * 0){ if (!isAzulCompleto(i, j, offset, nbands)){ matriz[i][j]= 1;
	 * setData(count,(byte)255); if ((i+1 < width)&&(matriz[i+1][j] == 0)){ if
	 * (!isAzulCompleto(i+1, j, offset, nbands)){ marcarObjeto(i+1, j, offset,
	 * nbands); }else{ matriz[i+1][j]= -1; count=jwidthnbands+(i+1)nbands;
	 * setData(count,(byte)0); } }
	 * 
	 * if ((j+1 < height)&&(matriz[i][j+1] == 0)){ if (!isAzulCompleto(i, j+1,
	 * offset, nbands)){ marcarObjeto(i, j+1, offset, nbands); }else{
	 * matriz[i][j+1]= -1; count=(j+1)widthnbands+(i)nbands;
	 * setData(count,(byte)0); } } if ((j-1 >0)&&(matriz[i][j-1] == 0)){ if
	 * (!isAzulCompleto(i, j-1, offset, nbands)){ marcarObjeto(i, j-1, offset,
	 * nbands); }else{ matriz[i][j-1]= -1; count=(j-1)widthnbands+(i)nbands;
	 * setData(count,(byte)0); } } if ((i-1 > 0)&&(matriz[i-1][j] == 0)){ if
	 * (!isAzulCompleto(i-1, j, offset, nbands)){ marcarObjeto(i-1, j, offset,
	 * nbands); }else{ matriz[i-1][j]= -1; count=jwidthnbands+(i-1)nbands;
	 * setData(count,(byte)0); } }
	 * 
	 * }else{ matriz[i][j]= -1; setData(count,(byte)0); } } }
	 * 
	 * }
	 */

	public void agregarPunto(int i, int j) {
		if (max + 2 < maxArreglo) {
			arreglo[max] = i;
			arreglo[max + 1] = j;
			max = max + 2;
		}

	}

	public Objeto detectarObjeto(int w, int h, int offset, int nbands, int R,
			int G, int B, String name) {

		for (int x = 0; x < max; x = x + 2) {
			arreglo[x] = 0;
			arreglo[x + 1] = 0;
		}
		arreglo[0] = w;
		arreglo[1] = h;
		max = 2;
		Objeto imag = new Objeto();
		imag.setName(name);
		for (int x = 0; x < max; x = x + 2) {
			int i = arreglo[x];
			int j = arreglo[x + 1];
			if (matriz[i][j] != -1) {
				matriz[i][j] = 1;
				int count = j * width * nbands + i * nbands;
				setDataInt(count, R, G, B);
				Pixel pix = crearPixel(i + 1, j, offset, nbands);
				imag.agregarPunto(pix);
				if ((i + 1 < width) && (matriz[i + 1][j] == 0)) {
					Pixel p = crearPixel(i + 1, j, offset, nbands);
					if (!p.isColorFondo()) {
						agregarPunto(i + 1, j);
						matriz[i + 1][j] = 1;
					} else {
						matriz[i + 1][j] = -1;
						count = j * width * nbands + (i + 1) * nbands;
						setData(count, (byte) 0);
					}
				}

				if ((j + 1 < height) && (matriz[i][j + 1] == 0)) {
					Pixel p = crearPixel(i, j + 1, offset, nbands);
					if (!p.isColorFondo()) {
						agregarPunto(i, j + 1);
						matriz[i][j + 1] = 1;
					} else {
						matriz[i][j + 1] = -1;
						count = (j + 1) * width * nbands + (i) * nbands;
						setData(count, (byte) 0);
					}
				}
				if ((j - 1 > 0) && (matriz[i][j - 1] == 0)) {
					Pixel p = crearPixel(i, j - 1, offset, nbands);
					if (!p.isColorFondo()) {
						agregarPunto(i, j - 1);
						matriz[i][j - 1] = 1;
					} else {
						matriz[i][j - 1] = -1;
						count = (j - 1) * width * nbands + (i) * nbands;
						setData(count, (byte) 0);
					}
				}
				if ((i - 1 > 0) && (matriz[i - 1][j] == 0)) {
					Pixel p = crearPixel(i - 1, j, offset, nbands);
					if (!p.isColorFondo()) {
						agregarPunto(i - 1, j);
						matriz[i - 1][j] = 1;
					} else {
						matriz[i - 1][j] = -1;
						count = j * width * nbands + (i - 1) * nbands;
						setData(count, (byte) 0);
					}
				}
			}
		}

		return imag;

	}

	public void repitar(int offset, int nbands, int R, int G, int B) {
		for (int x = 0; x < max; x = x + 2) {
			int i = arreglo[x];
			int j = arreglo[x + 1];
			int count = j * width * nbands + i * nbands;
			setDataInt(count, R, G, B);
		}
	}

	public void repitarImagenColor(int offset, int nbands, int R, int G, int B,
			Objeto img) {
		Iterator<Pixel> i = img.getPuntos().iterator();
		while (i.hasNext()) {
			Pixel p = (Pixel) i.next();
			int count = p.getY() * width * nbands + p.getX() * nbands;
			setDataInt(count, R, G, B);
		}
	}

	public void repitarImagen(int offset, int nbands, Objeto img) {
		Iterator<Pixel> i = img.getPuntos().iterator();
		while (i.hasNext()) {
			Pixel p = (Pixel) i.next();
			int count = p.getY() * width * nbands + p.getX() * nbands;
			setDataInt(count, p.getCol().getRed(), p.getCol().getGreen(), p
					.getCol().getBlue());
		}
	}

	public void setData(int count, byte valor) {
		data[count + 0] = valor;
		data[count + 1] = valor;
		data[count + 2] = valor;
	}

	public void setDataInt(int count, int R, int G, int B) {
		data[count + 0] = (byte) B;
		data[count + 1] = (byte) G;
		data[count + 2] = (byte) R;
	}

	public int getPixels(int offset, int band) {
		return pixels[offset + band];
	}

	public int area() {
		return colores.size();
	}

	public int getEpsilon() {
		return epsilon;
	}

	public void setEpsilon(int epsilon) {
		this.epsilon = epsilon;
	}

	public int getCantidad() {
		return cantidad;
	}

	public ArrayList<Color> getColores() {
		return colores;
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int[][] getMatriz() {
		return matriz;
	}

	public void setMatriz(int[][] matriz) {
		this.matriz = matriz;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

}
