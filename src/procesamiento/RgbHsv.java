package procesamiento;

import java.awt.image.Raster;

/**
 * Clase que contiene métodos para hacer conversiones entre formatos HSV y RGB
 * 
 * @author oscar
 * 
 */

public class RgbHsv {
	public static int minimo(int r, int g, int b) {
		int min = r;
		if (g < min)
			min = g;
		if (b < min)
			min = b;
		return min;
	}

	public static int maximo(int r, int g, int b) {
		int max = r;
		if (g > max)
			max = g;
		if (b > max)
			max = b;
		return max;
	}

	public static float[] RGBtoHSV(int r, int g, int b) {
		float min, max, delta;
		float h, s, v;
		float hsv[] = new float[3];
		min = minimo(r, g, b);
		max = maximo(r, g, b);
		v = max; // v

		delta = max - min;

		if (max != 0)
			s = delta / max; // s
		else {
			// r = g = b = 0 // s = 0, v is undefined
			s = 0;
			h = 0;
			hsv[1] = s;
			hsv[0] = h;
			return hsv;
		}

		if (r == max)
			h = (g - b) / delta; // between yellow & magenta
		else if (g == max)
			h = 2 + (b - r) / delta; // between cyan & yellow
		else
			h = 4 + (r - g) / delta; // between magenta & cyan

		h *= 60; // degrees
		if (h < 0)
			h += 360;
		hsv[0] = h;
		hsv[1] = s * 100;
		hsv[2] = v * 100 / 255;
		return hsv;
	}

	public static int HSVtoRGB(float h, float s, float v) {
		// h,s,v in [0,1]
		float rr = 0, gg = 0, bb = 0;
		float hh = (6 * h) % 6;
		int c1 = (int) hh;
		float c2 = hh - c1;
		float x = (1 - s) * v;
		float y = (1 - (s * c2)) * v;
		float z = (1 - (s * (1 - c2))) * v;
		switch (c1) {
		case 0:
			rr = v;
			gg = z;
			bb = x;
			break;
		case 1:
			rr = y;
			gg = v;
			bb = x;
			break;
		case 2:
			rr = x;
			gg = v;
			bb = z;
			break;
		case 3:
			rr = x;
			gg = y;
			bb = v;
			break;
		case 4:
			rr = z;
			gg = x;
			bb = v;
			break;
		case 5:
			rr = v;
			gg = x;
			bb = y;
			break;
		}
		int N = 256;
		int r = Math.min(Math.round(rr * N), N - 1);
		int g = Math.min(Math.round(gg * N), N - 1);
		int b = Math.min(Math.round(bb * N), N - 1);
		// create int-packed RGB-color:
		int rgb = ((r & 0xff) << 16) | ((g & 0xff) << 8) | b & 0xff;
		return rgb;
	}

	/**
	 * Crea un rango HSV a partir de una porcion de una imagen
	 * 
	 * @param raster
	 * @return
	 */
	public static HSVRange createHsvRange(Raster raster, HSVRange excludeRange) {
		HSVRange range = new HSVRange();
		Float hMin = null;
		Float hMax = null;
		Float sMin = null;
		Float sMax = null;
		Float vMin = null;
		Float vMax = null;

		for (int x = 0; x < raster.getWidth(); x++)
			for (int y = 0; y < raster.getHeight(); y++) {
				int[] pixel = null;

				pixel = raster.getPixel(x, y, pixel);
				int r = pixel[0];
				int g = pixel[0];
				int b = pixel[0];

				if (pixel.length == 3) {
					g = pixel[1];
					b = pixel[2];
				}

				float[] hsv = RgbHsv.RGBtoHSV(r, g, b);
				if (excludeRange == null || !excludeRange.isEnRango(hsv[0], hsv[1], hsv[2])){
					if (hMin == null || hsv[0] < hMin)
						hMin = hsv[0];
					if (hMax == null || hsv[0] > hMax)
						hMax = hsv[0];
					if (sMin == null || hsv[1] < sMin)
						sMin = hsv[1];
					if (sMax == null || hsv[1] > sMax)
						sMax = hsv[1];
					if (vMin == null || hsv[2] < vMin)
						vMin = hsv[2];
					if (vMax == null || hsv[2] > vMax)
						vMax = hsv[2];
				}
			}
		range.setHMin(hMin);
		range.setHMax(hMax);
		range.setSMin(sMin);
		range.setSMax(sMax);
		range.setVMin(vMin);
		range.setVMax(vMax);
		return range;
	}

	public static void main(String[] args) {
		float[] hsv = RGBtoHSV(83, 106, 186);

		System.out.println(hsv[0] + "," + hsv[1] + "," + hsv[2]);
	}

}
