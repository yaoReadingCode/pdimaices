package procesamiento;

import java.awt.image.Raster;

import javax.media.jai.PlanarImage;

public class ObtenerRangoColorObjeto extends AbstractImageCommand {
	HSVRange rangoObjeto = new HSVRange();
	HSVRange rangoFondo = null;
	
	public ObtenerRangoColorObjeto(PlanarImage image, HSVRange rangoFondo) {
		super(image);
		this.rangoFondo = rangoFondo;
	}

	public HSVRange getRangoFondo() {
		return rangoFondo;
	}

	public void setRangoFondo(HSVRange rangoFondo) {
		this.rangoFondo = rangoFondo;
	}

	public HSVRange getRangoObjeto() {
		return rangoObjeto;
	}

	public PlanarImage execute() {
		if (getImage() != null) {
			PlanarImage image = getImage();
			int width = getImage().getWidth();
			int height = getImage().getHeight();
			int tWidth = ImageUtil.tileWidth;
			int tHeight =  ImageUtil.tileHeight;
			
			int[] hValue = new int[361] ;
			int[] sValue = new int[101];
			int[] vValue = new int[257];

			// We must process all tiles.
			for (int tw = image.getMinTileX(); tw <= image.getMaxTileX(); tw++)
				for (int th = image.getMinTileY(); th <= image.getMaxTileY(); th++) {
					// Get a raster for that tile.
					Raster wr = image.getTile(tw, th);

					for (int w = 0; w < tWidth; w++)
						for (int h = 0; h < tHeight; h++) {
							int x = tw * tWidth + w;
							int y = th * tHeight + h;
							if (x < width && y < height) {
								
								try {
									int[] pixel = null;
									
									pixel = wr.getPixel(x, y, pixel);
									int r = pixel[0];
									int g = pixel[0];
									int b = pixel[0];

									if (pixel.length == 3) {
										g = pixel[1];
										b = pixel[2];
									}

									float[] hsv = RgbHsv.RGBtoHSV(r, g, b);
									if (!rangoFondo.isEnRango(hsv[0],hsv[1],hsv[2])) {
										
										if (hsv[0] > 100)
											System.out.println("entro");

										hValue[(int)hsv[0]]++;
										sValue[(int)hsv[1]]++;
										vValue[(int)hsv[2]]++;
									} 
									
								} 
								catch (Exception e) {
									System.out.println("x: "+x + ", y: "+ y);
									e.printStackTrace();
									return null;
								}
							}
						}
					
				}
			int minValue = 20;
			for(int h = 0; h< hValue.length; h++)
				for(int s = 0; s< sValue.length; s++)
					for(int v = 50; v< vValue.length; v++){
						if (!rangoFondo.isEnRango(h, s, v)){
							if (hValue[h]> minValue){
								if (rangoObjeto.getHMin() == null || rangoObjeto.getHMin() > h)
									rangoObjeto.setHMin(h * 1f);
								if (rangoObjeto.getHMax() == null || rangoObjeto.getHMax() < h)
									rangoObjeto.setHMax(h * 1f);
							}
							if (sValue[s]> minValue){
								if (rangoObjeto.getSMin() == null || rangoObjeto.getSMin() > s)
									rangoObjeto.setSMin(s * 1f);
								if (rangoObjeto.getSMax() == null || rangoObjeto.getSMax() < s)
									rangoObjeto.setSMax(s * 1f);
							}
							if (vValue[v]> minValue){
								if (rangoObjeto.getVMin() == null || rangoObjeto.getVMin() > v)
									rangoObjeto.setVMin(v * 1f);
								if (rangoObjeto.getVMax() == null || rangoObjeto.getVMax() < v)
									rangoObjeto.setVMax(v * 1f);
							}
						}
				
				
				
			}
			return image;
		}
		return null;
	}

	public String getCommandName() {
		return this.getClass().getName();
	}

	public void postExecute() {
		// TODO Auto-generated method stub

	}

	
	public String getInfo() {
		StringBuffer bf = new StringBuffer();
		bf.append("HMin: " + getRangoObjeto().getHMin() + ", HMax: " + getRangoObjeto().getHMax()+ "\n");
		bf.append("SMin: " + getRangoObjeto().getSMin() + ", SMax: " + getRangoObjeto().getSMax()+ "\n");
		bf.append("VMin: " + getRangoObjeto().getVMin() + ", VMax: " + getRangoObjeto().getVMax());
		System.out.println(bf.toString());
		return bf.toString();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
