package pruebas;

import java.awt.Dimension;
import java.awt.RenderingHints;
import java.awt.image.renderable.ParameterBlock;
import java.io.IOException;
import java.util.HashMap;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.ImageWriteParam;
import javax.media.jai.ImageLayout;
import javax.media.jai.JAI;
import javax.media.jai.PlanarImage;
import javax.media.jai.TileCache;

import com.sun.imageio.plugins.jpeg.JPEGImageReader;
import com.sun.imageio.plugins.jpeg.JPEGImageReaderSpi;
import com.sun.media.imageio.plugins.tiff.TIFFCompressor;
import com.sun.media.imageio.plugins.tiff.TIFFImageWriteParam;
import com.sun.media.imageioimpl.plugins.tiff.TIFFImageReaderSpi;
import com.sun.media.imageioimpl.plugins.tiff.TIFFImageWriterSpi;
import com.sun.media.jai.codec.TIFFEncodeParam;


public class ReadWriteBigImages {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		int tileWidth = 256;
		int tileHeight = 256;
		
		//setting cache
		TileCache cache = JAI.createTileCache();
		long size = 512*1024*1024L;
		cache.setMemoryCapacity(size);

		JAI.setDefaultTileSize(new Dimension(tileWidth, tileHeight));
		
		ImageLayout tileLayout = new ImageLayout();
		tileLayout.setTileWidth(tileWidth);
		tileLayout.setTileHeight(tileHeight);

		HashMap map = new HashMap();
		map.put(JAI.KEY_IMAGE_LAYOUT, tileLayout);
		map.put(JAI.KEY_TILE_CACHE, cache);
		map.put(JAI.KEY_CACHED_TILE_RECYCLING_ENABLED, true);
		RenderingHints tileHints = new RenderingHints(map);
		
		
		 JPEGImageReaderSpi 	readerJPEGSpi = new JPEGImageReaderSpi();
		 final ImageReadParam readP = new ImageReadParam();
		
		 final ParameterBlock pbjRead = new ParameterBlock();
		 pbjRead.add("maices_hd2.jpg");
		 pbjRead.add(0);
		 pbjRead.add(Boolean.FALSE);
		 pbjRead.add(Boolean.FALSE);
		 pbjRead.add(Boolean.FALSE);
		 pbjRead.add(null);
		 pbjRead.add(null);
		 pbjRead.add(readP);
		 pbjRead.add(readerJPEGSpi.createReaderInstance());
		 
		 PlanarImage image = JAI.create("imageread",pbjRead,tileHints);
		
		 System.out.println("*******JPEG Image************");
		 System.out.println("width: " + image.getWidth());
		 System.out.println("height: " + image.getHeight());
		 System.out.println("tile width: " + image.getTileWidth());
		 System.out.println("tile height: " + image.getTileHeight());

		 
		// Save the image on a file. We cannot just store it, we must set the image encoding parameters
			// to ensure that it will be stored as a tiled image.
		TIFFEncodeParam tep = new TIFFEncodeParam();
		tep.setWriteTiled(true);
		tep.setTileSize(tileWidth,tileHeight);
		
		 TIFFImageWriterSpi	writerSpi = new TIFFImageWriterSpi();
		 final TIFFImageWriteParam writeP = new TIFFImageWriteParam(null);
		 writeP.setTilingMode(TIFFImageWriteParam.MODE_EXPLICIT);
		 writeP.setTiling(tileWidth, tileHeight, 0, 0);
		 writeP.setCompressionMode(TIFFImageWriteParam.MODE_EXPLICIT);
		 writeP.setCompressionType("PackBits");
		 writeP.setCompressionQuality(1.0f);
		
		 final ParameterBlock pbjWrite = new ParameterBlock();
		 pbjWrite.addSource(image);
		 pbjWrite.add("maices_hd2.tif");
		 pbjWrite.add("TIFF");
		 pbjWrite.add(Boolean.FALSE);
		 pbjWrite.add(Boolean.FALSE);
		 pbjWrite.add(Boolean.FALSE);
		 pbjWrite.add(Boolean.FALSE);
		 pbjWrite.add(new Dimension(tileWidth,tileHeight));
		 pbjWrite.add(null);
		 pbjWrite.add(null);
		 pbjWrite.add(null);
		 pbjWrite.add(null);
		 pbjWrite.add(null);
		 pbjWrite.add(writeP);
		 pbjWrite.add(writerSpi.createWriterInstance());
		 
		 image = JAI.create("imagewrite",pbjWrite,tileHints);
		
		 System.out.println("*******Tiled Image************");
		 System.out.println("width: " + image.getWidth());
		 System.out.println("height: " + image.getHeight());
		 System.out.println("tile width: " + image.getTileWidth());
		 System.out.println("tile height: " + image.getTileHeight());
		
	}

}
