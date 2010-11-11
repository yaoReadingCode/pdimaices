package jai;

import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.image.RenderedImage;

import javax.media.jai.PlanarImage;
import javax.media.jai.iterator.RandomIter;
import javax.media.jai.iterator.RandomIterFactory;

import procesamiento.RgbHsv;

import com.sun.media.jai.widget.DisplayJAI;

public class DisplayDEM extends DisplayJAI implements MouseMotionListener {
	protected StringBuffer pixelInfo = new StringBuffer(50); // Pixel information (formatted as a
	// StringBuffer).
	protected double[] dpixel; // Pixel information as an array of doubles.
	protected RandomIter readIterator; // a RandomIter that allow us to get
	// the data of a single pixel.
	protected PlanarImage surrogateImage; // The surrogate byte image.
	protected int width, height; // Dimensions of the image
	protected double minValue, maxValue; // Range of the image values.

	/**
	 * The constructor of the class, which creates the data structures and
	 * surrogate image.
	 */
	public DisplayDEM(RenderedImage imageFront, RenderedImage imageBack) {
		display(imageFront, imageBack);
		pixelInfo = new StringBuffer(50);
		addMouseMotionListener(this); // Registers the mouse motion listener.
	}

	public DisplayDEM() {
		super();
		pixelInfo = new StringBuffer(50);
		addMouseMotionListener(this); // Registers the mouse motion listener.
	}

	public void display(RenderedImage imageFront, RenderedImage imageBack) {

		
		readIterator = RandomIterFactory.create(imageBack, null);
		// Get some facts about the image
		width = imageBack.getWidth();
		height = imageBack.getHeight();
		dpixel = new double[imageBack.getSampleModel().getNumBands()];
		surrogateImage = PlanarImage.wrapRenderedImage(imageBack);
		// We need to know the extrema of the image to create the surrogate
		// image. Let’s use the extrema operator to get them.
		
		/*
		ParameterBlock pbMaxMin = new ParameterBlock();
		pbMaxMin.addSource(imageFront);
		RenderedOp extrema = JAI.create("extrema", pbMaxMin);
		double[] allMins = (double[]) extrema.getProperty("minimum");
		double[] allMaxs = (double[]) extrema.getProperty("maximum");
		minValue = allMins[0]; // Assume that the image is one-banded.
		maxValue = allMaxs[0];
		// Rescale the image with the parameters
		double[] multiplyByThis = new double[1];
		multiplyByThis[0] = 255. / (maxValue - minValue);
		double[] addThis = new double[1];
		addThis[0] = minValue;
		// Now we can rescale the pixels gray levels:
		ParameterBlock pbRescale = new ParameterBlock();
		pbRescale.add(multiplyByThis);
		pbRescale.add(addThis);
		pbRescale.addSource(imageFront);
		surrogateImage = (PlanarImage) JAI.create("rescale", pbRescale);
		// Let’s convert the data type for displaying.
		ParameterBlock pbConvert = new ParameterBlock();
		pbConvert.addSource(surrogateImage);
		pbConvert.add(DataBuffer.TYPE_BYTE);
		surrogateImage = JAI.create("format", pbConvert); */
		set(imageFront);
		// Create the StringBuffer instance for the pixel information.
	}

	// This method is here just to satisfy the MouseMotionListener interface
	public void mouseDragged(MouseEvent e) {
	}

	// This method will be called when the mouse is moved over the image.
	public void mouseMoved(MouseEvent me) {
		if (surrogateImage != null) {
			pixelInfo.setLength(0); // Clear the StringBuffer
			int x = me.getX(); // Get the mouse coordinates.
			int y = me.getY();
			if ((x >= width) || (y >= height)) // Avoid exceptions, consider
			// only
			{ // pixels within image bounds.
				pixelInfo.append("No data!");
				return;
			}
			pixelInfo.append("(INFO) " + x + "," + y + ": ");
			readIterator.getPixel(x, y, dpixel); // Read the original pixel
			// value.
			float hsv[] = RgbHsv.RGBtoHSV((int) dpixel[0], (int) dpixel[1],
					(int) dpixel[2]);
			pixelInfo.append("H: " + Math.round(hsv[0]) + " - S: "
					+ Math.round(hsv[1]) + " - V: " + Math.round(hsv[2])+ " | "); // Append
			pixelInfo.append("R: " + dpixel[0] + " - G: "
					+ dpixel[1] + " - B: " + dpixel[2]); // Append

			// to
			// the
			// StringBuffer
			// .
		}
	} // end of method mouseMoved

	// Allows other classes to access the pixel info string.
	public String getPixelInfo() {
		if (surrogateImage != null)
			return pixelInfo.toString();
		return null;
	}
}