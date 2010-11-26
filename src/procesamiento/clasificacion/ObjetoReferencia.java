package procesamiento.clasificacion;

import java.awt.Color;

public class ObjetoReferencia {
	/**
	 * Area en cm2 del objeto de referencia
	 */
	private Double areaCm = 10.0;
	
	/**
	 * Area en pixeles del objeto de referencia
	 */
	private Double areaPixel = 10.0;

	/**
	 * Color del objeto de referencia en formato int utilizado para encontrarlo en la imagen
	 */
	private Integer colorRgb;
	

	public Double getAreaCm() {
		return areaCm;
	}

	public void setAreaCm(Double areaCm) {
		this.areaCm = areaCm;
	}

	public Double getAreaPixel() {
		return areaPixel;
	}

	public void setAreaPixel(Double areaPixel) {
		this.areaPixel = areaPixel;
	}

	public Integer getColorRgb() {
		return colorRgb;
	}

	public void setColorRgb(Integer colorRgb) {
		this.colorRgb = colorRgb;
	}

	public Color getColor() {
		if (getColorRgb() != null)
			return new Color(getColorRgb());
		return null;
	}

	public void setColor(Color color) {
		if (color != null)
			setColorRgb(color.getRGB());
		else
			setColorRgb(null);
	}
	
	/**
	 * Retorna la relacion entre el area en pixeles y el area en centimetros
	 * @return
	 */
	public Double getRelacionPixelCm(){
		if (getAreaPixel() != null && getAreaCm() != null && getAreaPixel() != 0)
			return getAreaCm() / getAreaPixel();
		return 1.0;
	}

}
