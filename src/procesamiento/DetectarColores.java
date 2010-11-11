package procesamiento;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Point;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.awt.image.SampleModel;


import javax.media.jai.*;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;

import objeto.Objeto;

import com.sun.media.jai.codecimpl.util.RasterFactory;
import com.sun.media.jai.widget.DisplayJAI;

public class DetectarColores {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		
//		 Open the image (using the name passed as a command line parameter)
		PlanarImage pi = JAI.create("fileload", args[0]);
		int width = pi.getWidth();
		int height = pi.getHeight();
		
		SampleModel sm = pi.getSampleModel();
		int nbands = sm.getNumBands();
		Raster inputRaster = pi.getData();
		int[] pixels = new int[nbands*width*height];
		inputRaster.getPixels(0,0,width,height,pixels);
		int offset;

		ManejoDeColores manejador = new ManejoDeColores(width,height);
		Color colFo=new Color(154,156,220);
		manejador.setColorDeFondo(colFo);
		manejador.setPixels(pixels);
		manejador.setMaxOffset(width*height);
		
		int count = 0; // Temporary counter.
		
		int cantidadDeObjetos=0;
		
		for(int h=0;h<height;h++)
			for(int w=0;w<width;w++)
			{
				offset = h*width*nbands+w*nbands;
				
				if (!manejador.isVisitado(w,h))
					if(!manejador.isAzulCompleto(w, h, offset, nbands)){
						//manejador.marcarObjeto(w, h,  offset, nbands);

						Objeto img = manejador.detectarObjeto(w, h,  offset, nbands,250, 250,250, "");
						if (img.medida() > 2000){
							if(img.medida() > 10000){
								cantidadDeObjetos++;
								System.out.println("Maiz de tamaño sano: "+cantidadDeObjetos+ " tamaño: " + img.medida());
								Color col = img.colorPromedio();
								System.out.println(cantidadDeObjetos+ " Color Promedio: " + col.toString());
								
								//manejador.repitar(offset, nbands, 10, 250, 10);
								//manejador.repitarImagenColor(offset, nbands, 10, 250, 10, img);
								
								if(col.getRed() < 200 )
									manejador.repitarImagenColor(offset, nbands, 250, 150, 100, img);
								else
									manejador.repitarImagenColor(offset, nbands, 10, 250, 10, img);
								
							}else{
								System.out.println("Maiz roto de tamaño: " + img.medida());
								manejador.repitar(offset, nbands, 250, 10, 10);
							}
								
						}else{
							System.out.println("Error de: " + img.medida());
							manejador.repitar(offset, nbands, 50, 0, 0);
						}
					}else{
						manejador.setMatriz(w, h, -1);
						manejador.setData(count,(byte)0);
					}
				count += 3;
		}
		
		
		DataBufferByte dbuffer = new DataBufferByte(manejador.getData(),width*height*3);
		// Create an pixel interleaved data sample model.
		SampleModel sampleModel =
		RasterFactory.createPixelInterleavedSampleModel(DataBuffer.TYPE_BYTE,width,height,3);
		// Create a compatible ColorModel.
		ColorModel colorModel =  PlanarImage.createColorModel(sampleModel);
		// Create a WritableRaster.
		Raster raster = RasterFactory.createWritableRaster(sampleModel,dbuffer,
		new Point(0,0));
		// Create a TiledImage using the SampleModel.
		TiledImage tiledImage = new TiledImage(0,0,width,height,0,0,sampleModel,(ColorModel) colorModel);
		// Set the data of the tiled image to be the raster.
		tiledImage.setData(raster);
		// Save the image on a file.
		JAI.create("filestore",tiledImage,"rgbpattern.tif","TIFF");
		
//		/*
		PlanarImage image = JAI.create("fileload", "rgbpattern.tif");
		String imageInfo ="Dimensions: "+image.getWidth()+"x"+image.getHeight()+ " Bands:"+image.getNumBands();
		JFrame frame = new JFrame();
		frame.setTitle("DisplayJAI: "+args[0]);
		// Get the JFrame’s ContentPane.
		Container contentPane = frame.getContentPane();
		contentPane.setLayout(new BorderLayout());
		// Create an instance of DisplayJAI.
		DisplayJAI dj = new DisplayJAI(image);
		// Add to the JFrame’s ContentPane an instance of JScrollPane
		// containing the DisplayJAI instance.
		contentPane.add(new JScrollPane(dj),BorderLayout.CENTER);
		// Add a text label with the image information.
		contentPane.add(new JLabel(imageInfo),BorderLayout.SOUTH);
		// Set the closing operation so the application is finished.
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(1000,800); // adjust the frame size.
		frame.setVisible(true); // show the frame.
		
		
		
		//*/
		System.out.println("W: " + width);
		System.out.println("H: " + height);
		System.out.println("Area: " + manejador.area());
		System.out.println("can: " + manejador.getCantidad());
		System.out.println("cantidad de objetos: " + cantidadDeObjetos);

}
}
