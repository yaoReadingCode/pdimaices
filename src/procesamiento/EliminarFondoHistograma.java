package procesamiento;

import java.awt.image.WritableRaster;
import java.awt.image.renderable.ParameterBlock;

import javax.media.jai.Histogram;
import javax.media.jai.JAI;
import javax.media.jai.PlanarImage;
import javax.media.jai.TiledImage;

import aplicarFiltros.Visualizador;

public class EliminarFondoHistograma extends AbstractImageCommand {

	public EliminarFondoHistograma(PlanarImage image) {
		super(image);
	}
	
	/*
	 * (non-Javadoc)
	 * @see procesamiento.ImageComand#execute()
	 */
	public PlanarImage execute() {
		if (getImage() != null) {
			TiledImage tiledImage = ImageUtil.createTiledImage(getImage(), ImageUtil.tileWidth, ImageUtil.tileHeight);
			int width = getImage().getWidth();
			int height = getImage().getHeight();
			int tWidth = ImageUtil.tileWidth;
			int tHeight =  ImageUtil.tileHeight;
			
			ParameterBlock pb = new ParameterBlock();
			pb.addSource(tiledImage);
			pb.add(null); // The ROI.
			pb.add(1); // Samplings.
			pb.add(1);
			pb.add(new int[] { 256 }); // Num. bins.
			pb.add(new double[] { 0 }); // Min value to be considered.
			pb.add(new double[] { 256 }); // Max value to be considered.
			// Creates the histogram.
			PlanarImage temp = JAI.create("histogram", pb);
			Histogram histogramaImagen = (Histogram) temp.getProperty("histogram");
			
			int paginaActual = 0;
			int[] pixelFondo = {0, 0, 0};
			int[] pixelFondoGris = {0};
			int cantidadMaximaPixeles = tiledImage.getHeight() * tiledImage.getWidth();
			int aumentoProgreso = cantidadMaximaPixeles / 100;
			int cantPixelsProcesados = 0;
			// We must process all tiles.
			for (int tw = tiledImage.getMinTileX(); tw <= tiledImage.getMaxTileX(); tw++)
				for (int th = tiledImage.getMinTileY(); th <= tiledImage.getMaxTileY(); th++) {
					// Get a raster for that tile.
					WritableRaster wr = tiledImage.getWritableTile(tw, th);

					for (int w = 0; w < tWidth; w++)
						for (int h = 0; h < tHeight; h++) {
							int x = tw * tWidth + w;
							int y = th * tHeight + h;
							if (x < width && y < height) {
								try {
									int[] pixel = null;
									pixel = wr.getPixel(x, y, pixel);

									boolean isFondo = true;
									for(int band = 0; band < histogramaImagen.getNumBands(); band++){
										double media = histogramaImagen.getMean()[band];
										double desvioEstandar = histogramaImagen.getStandardDeviation()[band];
										if (media - desvioEstandar > pixel[band] || pixel[band] > media + desvioEstandar){
											isFondo = false;
											break;
										}
									}
									
									if (isFondo){
										if (histogramaImagen.getNumBands() == 1)
											wr.setPixel(x, y, pixelFondoGris);
										else{
											wr.setPixel(x, y, pixelFondo);
										}
									}
								} catch (Exception e) {
									System.out.println("x: "+x + ", y: "+ y);
									e.printStackTrace();
									return null;
								}
							}
							cantPixelsProcesados++;
							int pagina = cantPixelsProcesados / aumentoProgreso;
							if (pagina > paginaActual ){
								paginaActual = pagina;
								Visualizador.aumentarProgreso(1, "");
							}
						}
					tiledImage.releaseWritableTile(tw, th);
				}

			return tiledImage;
		}
		return null;

	}

	public String getCommandName() {
		return "EliminarFondoHistograma";
	}

	public void postExecute() {
		// TODO Auto-generated method stub

	}

}
