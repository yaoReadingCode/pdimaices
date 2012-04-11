package procesamiento.clasificacion;

import objeto.Objeto;

public class ObjetoReferencia {
	// /**
	// * Area en cm2 del objeto de referencia
	// */
	// private Double areaCm = 10.0;
	//	
	// /**
	// * Area en pixeles del objeto de referencia
	// */
	// private Double areaPixel = 10.0;
	//
	// /**
	// * Color del objeto de referencia en formato int utilizado para
	// encontrarlo en la imagen
	// */
	// private Integer colorRgb;
	//	
	//
	// public Double getAreaCm() {
	// return areaCm;
	// }
	//
	// public void setAreaCm(Double areaCm) {
	// this.areaCm = areaCm;
	// }
	//
	// public Double getAreaPixel() {
	// return areaPixel;
	// }
	//
	// public void setAreaPixel(Double areaPixel) {
	// this.areaPixel = areaPixel;
	// }
	//
	// public Integer getColorRgb() {
	// return colorRgb;
	// }
	//
	// public void setColorRgb(Integer colorRgb) {
	// this.colorRgb = colorRgb;
	// }
	//
	// public Color getColor() {
	// if (getColorRgb() != null)
	// return new Color(getColorRgb());
	// return null;
	// }
	//
	// public void setColor(Color color) {
	// if (color != null)
	// setColorRgb(color.getRGB());
	// else
	// setColorRgb(null);
	// }

	private static Double cantPixel;

	private static Double cantMM = 23.0;
	
	private static Double cant_Pixeles_X_MM;

	private static Double diametroZaranda = 4.76;
	
	private static Objeto referencia;
	
	private static Double errorCalculos;

	/**
	 * Retorna la relacion entre el area en pixeles y el area en milimetros
	 * 
	 * @return
	 */
	public static Double getRelacionPixelMM() {
		// if (getAreaPixel() != null && getAreaCm() != null && getAreaPixel()
		// != 0)
		if (cantPixel != null) {
			return cantMM /cantPixel;
		}
		return 1.0;
	}

	public static void inicializarObjetoReferencia() {
		
		cantPixel = 0.0;
		referencia = null;
		errorCalculos = 0.0;
		cant_Pixeles_X_MM = 1.0;
	}
	
	public static void setObjetoReferencia(Objeto obj) {
		
		cantPixel = obj.getDiametro();
		referencia = obj;
		errorCalculos = (obj.getAlto()-obj.getAncho());
		if (errorCalculos < 0) errorCalculos = errorCalculos*-1;
		errorCalculos = errorCalculos/cantMM;
		cant_Pixeles_X_MM = (cantPixel / cantMM);
	}
	
	public static Double mayorDiametroEnMM(Objeto obj){
		if(obj.getAlto() > obj.getAncho()){
			return obj.getAlto() / cant_Pixeles_X_MM;
		}
		return obj.getAncho() / cant_Pixeles_X_MM;
	}
	
	public static boolean isGranoQuebrado(Objeto obj){
		
		if(ObjetoReferencia.mayorDiametroEnMM(obj) < diametroZaranda) return true;
		return false;
	}

	public static Double getCantPixel() {
		return cantPixel;
	}

	public static Double getCantMM() {
		return cantMM;
	}

	public static Double getCant_Pixeles_X_MM() {
		return cant_Pixeles_X_MM;
	}

	public static Double getDiametroZaranda() {
		return diametroZaranda;
	}

	public static Objeto getReferencia() {
		return referencia;
	}

	public static Double getErrorCalculos() {
		return errorCalculos;
	}
	
	

}
