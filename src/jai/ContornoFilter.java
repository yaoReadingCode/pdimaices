package jai;

import java.awt.Dimension;
import java.awt.image.renderable.ParameterBlock;

import javax.media.jai.Interpolation;
import javax.media.jai.InterpolationNearest;
import javax.media.jai.JAI;
import javax.media.jai.KernelJAI;
import javax.media.jai.ParameterBlockJAI;
import javax.media.jai.PlanarImage;
import javax.media.jai.TiledImage;
import javax.media.jai.operator.MedianFilterDescriptor;
import javax.media.jai.widget.ScrollingImagePanel;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JWindow;

import procesamiento.Binarizar;
import procesamiento.Binarizar;
import procesamiento.HSVRange;
import procesamiento.ImageUtil;

import com.sun.media.jai.widget.DisplayJAI;

public class ContornoFilter {
	public static void main(String[] args) {
		// Read the image. Assume args[0] points to its filename.
		if (args.length > 0) {
		     // Load the image.
		     PlanarImage im0 = (PlanarImage)JAI.create("fileload", args[0]);
		     
		     ParameterBlock pb2 = new ParameterBlock();
		     pb2.addSource(im0);
		     pb2.add(MedianFilterDescriptor.MEDIAN_MASK_SQUARE);
		     pb2.add(3);
		     PlanarImage im1 = JAI.create("medianfilter", pb2);
		     
		     /*
		     double[][] matrix1 = {{ 1./3, 1./3, 1./3, 0 }};
		     ParameterBlock pb1 = new ParameterBlock();
		     pb1.addSource(im0);
		     pb1.add(matrix1);
		     PlanarImage gray1 = JAI.create("bandcombine", pb1, null);
		     */
		     
		     HSVRange range = new HSVRange();
		     range.setHMin(60f);
		     range.setHMax(340f);
		     range.setSMin(9f);
		     Binarizar ef = new Binarizar(ImageUtil.reformatImage(im0, new Dimension(256, 256)), range);
		     TiledImage output1 = (TiledImage) ef.execute();

		     /*
		     //KernelJAI kern_h = new KernelJAI(3,3,data_h);
		     //KernelJAI kern_v = new KernelJAI(3,3,data_v);
		     KernelJAI kern_h = KernelJAI.GRADIENT_MASK_SOBEL_HORIZONTAL;
		     KernelJAI kern_v = KernelJAI.GRADIENT_MASK_SOBEL_VERTICAL;

		     // Create the Gradient operation.
		     PlanarImage im2 = JAI.create("gradientmagnitude", output1, kern_h, kern_v);
			*/
		     float[] kernelMatrix = { 
						0, 0, 0, 
						0, 1, 0,  
						0, 0, 0 
						};
			// Create the kernel using the array.
			KernelJAI kernel = new KernelJAI(3, 3, kernelMatrix);
			// Create a ParameterBlock with that kernel and image.
			ParameterBlock p = new ParameterBlock();
			p.addSource(output1);
			p.add(kernel);
			
			PlanarImage output = JAI.create("erode", p, null);
			
			ParameterBlock pb = new ParameterBlock();
			pb.addSource(output1);
			pb.addSource(output);
			output = JAI.create("subtract", pb);
			
			pb = new ParameterBlock();
			pb.addSource(im0);
			pb.addSource(output);
			output = JAI.create("add", pb);
			
  
		     // Display it.
		     JFrame frame1 = new JFrame();
		     frame1.setTitle("Gray-level by averaging");
		      // Add to the JFrame's ContentPane an instance of DisplayJAI with the processed image.
		     frame1.getContentPane().add(new JScrollPane(new DisplayJAI(output)));
		     // Set the closing operation so the application is finished.
		     frame1.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		     frame1.pack(); // adjust the frame size using preferred dimensions.
		     frame1.setVisible(true); 

		}

	}
	
	public static PlanarImage sumImage(PlanarImage image1, PlanarImage image2){
		ParameterBlock pb = new ParameterBlock();
		pb.addSource(image1);
		pb.addSource(image2);
		return JAI.create("add", pb);
	}
}
