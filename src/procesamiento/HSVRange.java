package procesamiento;

import java.awt.Color;

/**
 * Clase que contiene valores mínimos y máximos de los componentes de un color HSV 
 * @author oscar
 *
 */
public class HSVRange {
	
	public static final Float HMAX_VALUE = 360F;
	public static final Float HMIN_VALUE = 0F;
	public static final Float SMAX_VALUE = 100F;
	public static final Float SMIN_VALUE = 0F;
	public static final Float VMAX_VALUE = 100F;
	public static final Float VMIN_VALUE = 0F;


	/**
	 * Valor H mínimo
	 */
	private Float hMin;
	
	/**
	 * Valor H máximo
	 */
	private Float hMax;
	
	/**
	 * Valor S mínimo
	 */
	private Float sMin;
	
	/**
	 * Valor S máximo
	 */
	private Float sMax;
	
	/**
	 * Valor V mínimo
	 */
	private Float vMin;
	
	/**
	 * Valor V máximo
	 */
	private Float vMax;

	public Float getHMin() {
		return hMin;
	}

	public void setHMin(Float min) {
		hMin = min;
	}

	public Float getHMax() {
		return hMax;
	}

	public void setHMax(Float max) {
		hMax = max;
	}

	public Float getSMin() {
		return sMin;
	}

	public void setSMin(Float min) {
		sMin = min;
	}

	public Float getSMax() {
		return sMax;
	}

	public void setSMax(Float max) {
		sMax = max;
	}

	public Float getVMin() {
		return vMin;
	}

	public void setVMin(Float min) {
		vMin = min;
	}

	public Float getVMax() {
		return vMax;
	}

	public void setVMax(Float max) {
		vMax = max;
	}
	
	public boolean isEnRango(float h, float s, float v){
		if (((this.getHMin() != null && this.getHMin() <= h) || this.getHMin() == null)
				&& ((this.getHMax() != null && this.getHMax() >= h) || this.getHMax() == null)
				&& ((this.getSMin() != null && this.getSMin() <= s) || this.getSMin() == null)
				&& ((this.getSMax() != null && this.getSMax() >= s) || this.getSMax() == null)
				&& ((this.getVMin() != null && this.getVMin() <= v) || this.getVMin() == null)
				&& ((this.getVMax() != null && this.getVMax() >= v) || this.getVMax() == null)) {

			return true;
		} 
		return false;
	}

	public boolean isEnRango(Color color){
		float[] hsv = RgbHsv.RGBtoHSV(color.getRed(), color.getGreen(), color.getBlue());
		return isEnRango(hsv[0], hsv[1], hsv[2]);
	}

	
	public boolean isNulo(){
		if (getHMin() == null && getHMax() == null && getSMin() == null && getSMax() == null && getVMin() == null && getVMax() == null)
			return true;
		return false;
	}
	
	/**
	 * Devuelve el color RGB promedio del rango.
	 * @return Color RGB
	 */
	public Color getColorMedio(){
		Float hmin = (getHMin() != null) ? getHMin(): HMIN_VALUE;
		Float hmax = (getHMax() != null) ? getHMax(): HMAX_VALUE;
		Float smin = (getSMin() != null) ? getSMin(): SMIN_VALUE;
		Float smax = (getSMax() != null) ? getSMax(): SMAX_VALUE;
		Float vmin = (getVMin() != null) ? getVMin(): VMIN_VALUE;
		Float vmax = (getVMax() != null) ? getVMax(): VMAX_VALUE;
		float h = (hmax - hmin) / 2f;
		float s = (smax - smin) / 2f;
		float v = (vmax - vmin) / 2f;
		return new Color(RgbHsv.HSVtoRGB(h / HMAX_VALUE, s / SMAX_VALUE, v / VMAX_VALUE));
	}

}
